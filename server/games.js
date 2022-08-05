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
        try {
            if (!data.id)
                throw new Error()
            console.log(data)
            socket.join(data.id)
            const game = GamesArray.find(el => el.id == data.id);
            game.players.push({id: socket.id, name: data.name,});
            socket.to(game.id).emit("joined", data.name)
            console.log(socket.id+' joined in '+game.id)
            console.log(game.players)
            callback(data.name)
        } catch (err) {
            console.log(err)
            callback(new Error())
        }
    }
    


    socket.on('createGame',createGame);
    socket.on('joinGame',joinGame);
}
module.exports = ioGames