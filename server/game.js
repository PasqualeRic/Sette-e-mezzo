const { v4: uuid } = require('uuid');

class Game {
    id;
    name;
    status;
    numberOfPlayers;
    players;
    admin;
    currentQuestion;
    constructor(admin,name = '', status = 'waiting', players = [], numberOfPlayers = 0) {
        this.admin = admin;
        this.id = uuid();
        this.name = name;
        this.status = status;
        this.players = players;
        this.numberOfPlayers = numberOfPlayers;
    }
}

module.exports = Game