const { v4: uuid } = require('uuid');

class Game {
    id;
    name;
    status;
    nPlayers;
    players;
    dealer;
    winners;
    constructor(dealer,name = '', status = 'created', players = [], nPlayers = 0, winners = []) {
        this.dealer = dealer;
        this.id = uuid();
        this.name = name;
        this.status = status;
        this.players = players;
        this.nPlayers = nPlayers;
        this.winners = winners;
    }
}

module.exports = Game
