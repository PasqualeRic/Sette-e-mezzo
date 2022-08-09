const { v4: uuid } = require('uuid');
const gamesManager = require('./gamesmanager');
const Game = require('./game')
const ioGames = (socket) => {
    GamesArray = gamesManager.getGames()
    const createGame = async (callback) => {
        console.log('createGame')
        try {
            const game = new Game(socket.id);
            game.players.push({id:socket.id, name: "admin", point:0})
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
                    if(element.players.length == 0)
                    {
                        element.players.push({id: socket.id, name: data.name, point: 0});
                                console.log(element)
                                socket.to(element.id).emit("invioPlayer",data.name, data.numberOfPlayers)
                                console.log(socket.id+' joined in '+element.id)
                                if(element.players.length == element.numberOfPlayers){
                                    element.status = "close"
                                    socket.to(element.id).emit("start", element.numberOfPlayers)
                                }
                    }
                    else {
                        console.log(element.players.find(e => e.id == data.id))
                            if(!element.players.find(e => e.id == data.id) && element.status == "joinable"){
                                element.players.push({id: socket.id, name: data.name, point: 0});
                                console.log(element)
                                socket.to(element.id).emit("invioPlayer",data.name, data.numberOfPlayers)
                                console.log(socket.id+' joined in '+element.id)
                                console.log(element.players[0].id)
                                console.log(data.id)
                                if(element.players.length == element.numberOfPlayers){
                                    element.status = "close"
                                    socket.to(element.id).emit("start", element.numberOfPlayers)
                                }
                            }

                    }
                }
            })
           
        } catch (err) {
            console.log(err)
            callback(new Error())
        }
    }

    const midGame = async(data,callback) => {
        console.log("sei in mid game")
        console.log(data)

        try {

            GamesArray.forEach(element =>{
                element.players.forEach(i =>{
                    socket.to(i.id).emit("turno", "tocca a te")
                    socket.on("prova", args =>{
                        console.log(args)
                    })
                })
                //const player = element.players.find(e => e.id == data.id)
                //player.point = data.point
            })
            
            
        } catch (error) {
            callback(error)
        }
    }

    const startGame = async (data,callback) => {
        console.log("sei in partita")
        try{
            socket.broadcast.emit("partita")
        }catch(err)
        {
            callback(err)
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
    socket.on('startGame',startGame);
    socket.on('midGame',midGame);
}
module.exports = ioGames