const { v4: uuid } = require('uuid');
const gamesManager = require('./gamesmanager');
const Game = require('./game')
const ioGames = (socket) => {
    GamesArray = gamesManager.getGames()
    const createGame = async (callback) => {
        console.log('createGame')
        try {
            const game = new Game(socket.id);
            console.log(game)
            GamesArray.push(game)
            console.log('Game created.')
            socket.join(game.id)
            callback(game.id)
        } catch (err) {
            console.log(err)
            callback(new Error())
        }
    }
    const joinGame = async (data, callback) =>  {
        console.log('joinGame')
        console.log(data)
        try {
            console.log(GamesArray);
            GamesArray.forEach(element =>{
                if(element.numberOfPlayers == data.numberOfPlayers){
                    const game = GamesArray.find(el => el.id == element.id);
                    game.players.push({id: socket.id, name: data.name});
                    console.log(game)
                    socket.to(game.id).emit("invioPlayer",data.name)
                    console.log(socket.id+' joined in '+game.id)
                    console.log(game.players.length)
                    if(game.players.length == game.numberOfPlayers-1){
                        game.status = "close"
                        socket.to(game.id).emit("start")
                    }
                }
            })
           
        } catch (err) {
            console.log(err)
            callback(new Error())
        }
    }
    const confGame = async (data,callback) => {
        console.log('conf game')
        console.log(data);
        const game = GamesArray.find(el => el.id == data.id);
        GamesArray.forEach(element => {
            // controllo se esiste già una partita con questo nome
            if(element.name == data.name)
            {
                throw new Error()
            }
        });
        game.name = data.name
        game.status = 'joinable'
        game.numberOfPlayers = data.numberOfPlayers
        console.log(game)
        socket.join(game)
        
    }
    

    socket.on('confGame',confGame);
    socket.on('createGame',createGame);
    socket.on('joinGame',joinGame);
}
module.exports = ioGames