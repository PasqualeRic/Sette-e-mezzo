/*const SocketServer = require('websocket').server
const http = require('http')
const { stringify } = require('querystring')

const server = http.createServer((req, res) => {})

server.listen(3000, ()=>{
    console.log("Listening on port 3000...")
})

wsServer = new SocketServer({httpServer:server})

const connections = []

wsServer.on('request', (req) => {
    const connection = req.accept()
    console.log('new connection')
    connections.push(connection)


    connection.on('message', (mes) => {
        console.log(stringify(mes))
        connections.forEach(element => {
            if (element != connection)
                element.sendUTF(mes)
        })
    })

    connection.on('close', (resCode, des) => {
        console.log('connection closed')
        connections.splice(connections.indexOf(connection), 1)
    })

})*/
const http = require('http')
const express = require('express')
const socketio = require('socket.io')
const ip = require("ip");
const handler = require('./handler');


const port = 3000;
const bodyParser = require('body-parser')
const cors = require('cors');
const app = express()
app.use(bodyParser.json());
app.use(cors());

const server = http.createServer(app)
const io = socketio(server)

io.on('connection', handler(io))



server.listen(port, () => console.log('Server listening on http://'+ip.address()+':'+port))