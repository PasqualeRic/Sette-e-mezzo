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
        console.log('joinGame player: ')
        console.log(data)
        try {
            //console.log(GamesArray);
            GamesArray.forEach(element =>{
                if(element.numberOfPlayers == data.numberOfPlayers){
                    if(element.players.length == 0)
                    {
                        element.players.push({id: socket.id, name: data.name});
                        console.log(element)
                        socket.to(element.id).emit("invioPlayer",data.name, data.numberOfPlayers,data.id)
                        console.log(socket.id+' joined in '+element.id)
                        console.log(element.players[0].id)
                        //console.log(data.id)
                        if(element.players.length == element.numberOfPlayers-1){
                            element.status = "close"
                            socket.to(element.id).emit("start", element.numberOfPlayers)
                        }
                    }
                    else {
                        console.log(element.players.find(e => e.id == data.id))
                        if(!element.players.find(e => e.id == data.id) && element.status == "joinable"){
                            element.players.push({id: socket.id, name: data.name});
                            console.log(element)
                            socket.to(element.id).emit("invioPlayer",data.name, data.numberOfPlayers,data.id)
                            console.log(socket.id+' joined in '+element.id)
                            console.log(element.players[0].id)
                            //console.log(data.id)
                            if(element.players.length == element.numberOfPlayers-1){
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

    const startGame = async (data,callback) => {
        console.log("sei in partita")
        console.log("id server: "+data);
        try{
            socket.broadcast.emit("partita",data)
        }catch(err)
        {
            callback(err)
        }
    }

    const sendFirstCard = async (data,callback) => {
        console.log("sendFirstCard")
        console.log(data)
        try{
            var flag = false;
            counter=0;
            
            var timer = setInterval(function(){
                if(flag){
                    socket.broadcast.emit("reciveYourFirstCard",data)
                    clearInterval(timer);
                }else{
                    flag=true;
                }
            },400);
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

    const giveMeCard = async (data,callback) => {
        console.log("giveMeCard");
        console.log(data);
        socket.to(data.idServer).emit("requestCard",data.idClient)
    }   

    const terminateTurn = async (data,callback) => {
        console.log("terminateTurn");
        console.log(data);
        socket.to(data.idServer).emit("clientTerminate",data)
    }  

    const sendCard = async (data,callback) => {
        console.log("sendCard");
        console.log(data);
        socket.broadcast.emit("reciveCard",data)
    }  
    
    const closeRound = async (data,callback) => {
        console.log("closeRound");
        console.log(data);
        socket.broadcast.emit("closeRound",data);
    }

    const isYourTurn = async (data,callback) => {
        console.log("isYourTurn");
        console.log(data);
        socket.to(data).emit("myTurn");
    }

    socket.on('confGame',confGame);
    socket.on('createGame',createGame);
    socket.on('joinGame',joinGame);
    socket.on('startGame',startGame);
    socket.on('sendFirstCard',sendFirstCard);
    socket.on('giveMeCard',giveMeCard);
    socket.on('terminateTurn',terminateTurn);
    socket.on('sendCard',sendCard);
    socket.on('closeRound',closeRound);
    socket.on('isYourTurn',isYourTurn);
}
module.exports = ioGames