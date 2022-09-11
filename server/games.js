const { v4: uuid } = require('uuid');
const Game = require('./game')
var conta = 0;

let GamesArray = []
const gamesManager = {

    getGames: () => {
        return GamesArray
    },

    setGames: (games) => {
        GamesArray = games;
    }
}

const ioGames = (socket) => {
    GamesArray = gamesManager.getGames()
    const createGame = async (callback) => {
        console.log('createGame')
        try {
            const game = new Game(socket.id);
            console.log(game)
            GamesArray.push(game)
            console.log('partita creata')
            socket.join(game.id)
            callback(game.id)
        } catch (err) {
            console.log(err)
        }
    }
    const joinGame = async (data, callback) =>  {
        console.log('joinGame player: ')
        console.log(data)
        try {
            console.log("sei dentro il try")
            //console.log(GamesArray);
            GamesArray.forEach(element =>{
                console.log("a")
                if(element.nPlayers == data.nplayers && element.status == 'joinable'){
                    console.log("b")
                    if(element.players.length == 0)
                    {
                        console.log("c")
                        element.players.push({id: socket.id, name: data.name});
                        console.log(element)
                        socket.to(element.id).emit("invioPlayer",data.name, data.nplayers,data.id)
                        console.log(socket.id+' joined in '+element.id)
                        console.log(element.players[0].id)
                        throw new Error(); 
                    }
                    else {
                        console.log("d")
                        console.log(element.players.find(e => e.id == data.id))
                        if(!element.players.find(e => e.id == data.id) && element.status == "joinable"){
                            console.log("e")
                            element.players.push({id: socket.id, name: data.name});
                            console.log(element)
                            socket.to(element.id).emit("invioPlayer",data.name, data.nplayers,data.id)
                            console.log(socket.id+' joined in '+element.id)
                            console.log(element.players[0].id)
                            //console.log(data.id)
                            if(element.players.length == element.nPlayers-1){
                                element.status = "close"
                            }
                        }
                        throw new Error(); 
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
        console.log(data);
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
            },500);
        }catch(err)
        {
            callback(err)
        }
    }

    const confGame = async (data,callback) => {
        console.log('conf game')
        console.log(data);
        const game = GamesArray.find(el => el.id == data.id);
        game.name = data.name
        game.status = 'joinable'
        game.nPlayers = data.nplayers
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

        try{
            var flag = false;
            counter=0;
            
            var timer = setInterval(function(){
                if(flag){
                    socket.to(data).emit("myTurn");
                    clearInterval(timer);
                }else{
                    flag=true;
                }
            },500);
        }catch(err)
        {
            callback(err)
        }
    }

    const overSize = async (data,callback) => {
        console.log("overSize");
        console.log(data);
        socket.broadcast.emit("overSize",data);
    }

    const continueGame = async (data,callback) => {
        console.log("continueGame");
        console.log(data);
        socket.broadcast.emit("resContinueGame", data)
    }

    const deletePlayer = async (data,callback) => {
        console.log("deletePlayer")
        var n = 0
        GamesArray.forEach(element => {
            console.log(element.id)
            for(var i = 0; i<element.players.length; i++){
                if(element.players[i].id == data){
                    console.log(element)
                    element.players.pop(element.players[i].id);
                    n = parseInt(element.nPlayers)
                    n = n - 1
                    element.nPlayers = n
                    console.log(element)
                    console.log("id player eliminato: "+data);
                }
            }
        })

    }

    const deleteGame = async (data, callback) => {

        console.log(" ***** delelte Game ***** ");
        console.log(data);
        console.log(GamesArray);
        const game = GamesArray.find(el => el.id == data);
        GamesArray.pop(game)
        console.log(GamesArray)
    }
    const saveWinner = async (data) => {
        console.log("saveWinner")
        console.log(data)
        const game = GamesArray.find(el => el.id == data.idGame);
        console.log(game)
        game.winners.push({name: data.winner})
        console.log(game)
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
    socket.on('overSize',overSize);
    socket.on('continueGame',continueGame);
    socket.on('deletePlayer',deletePlayer);
    socket.on('deleteGame',deleteGame);
    socket.on('saveWinner',saveWinner);
}
module.exports = ioGames