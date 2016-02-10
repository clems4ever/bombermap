var express = require("express");
var app = express();
var server = require("http").Server(app);

var io = require("socket.io").listen(server);

var Player = require("./game/entities/player");
var Weapon = require("./game/entities/weapon");

var games = {};
var game_count = 0;

var clients = {};
var gamesByClient = {};

init();

function init() {
    console.log("Server is starting...");
    
    server.listen(3001);

	// Begin listening for events.
	setEventHandlers();
};

function setEventHandlers() {
	io.on("connection", function(client) {
	    clients[client.id] = client;
		console.log("New player has connected: " + client.id);
	
	    client.on("new_game", onNewGame);
	    client.on("join_game", onJoinGame);
		client.on("move", onMove);
	});
};

function onNewGame()
{
    console.log("OnNewGame event");
    // Create a game
    games[game_count] = { 
        gameId: game_count, 
        players: {}, 
        configuration: {} 
    }
    
    game_count = game_count + 1;
}

function onJoinGame(gameId)
{
    console.log("OnJoinGame event");
    
    // Join a room to which we can broadcast the messages
    this.join(gameId);
    games[gameId].players[this.id] = {x: 0, y: 0, rotation:0 };
    gamesByClient[this.id] = games[gameId];
}

function onMove(data)
{
    console.log("OnMove event");
	var game = gamesByClient[this.id];
	game.players[this.id].x = data.x;
	game.players[this.id].y = data.y;
	game.players[this.id].rotation = data.rotation;
	
	var player = game.players[this.id];
	console.log("Move received and re-emitted: " + this.id + " - " + player);
	
	var move = { id:this.id, x:player.x, y:player.y, rotation:player.rotation };
    io.to(game.gameId).emit("m", move);
}

