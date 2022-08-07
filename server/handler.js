const ioGames = require('./games')

const ioHandler = (io) => (socket) => {
   console.log(socket.id);
   ioGames(socket)
}

module.exports = ioHandler