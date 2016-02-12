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
	    client.on("start_game", onStartGame);
		client.on("move", onMove);
	});
};

function onNewGame(fn)
{
    console.log("OnNewGame event");
    
    var gameId = game_count.toString();
    
    // Create a game
    games[game_count] = { 
        gameId: gameId, 
        players: {}, 
        configuration: {} 
    }
    
    game_count = game_count + 1;
    
    fn(gameId);
}

function onJoinGame(data, fn)
{
    console.log("OnJoinGame event");
    
    var game_id = data.game_id;
    var username = data.username;
    
    var game = games[game_id];
    if(game === undefined)
    {
        var err = "Error while joining the game.";
        fn(err);
    }
    
    // Join a room to which we can broadcast the messages
    this.join(game_id);
    games[game_id].players[this.id] = {username: username, x: 0, y: 0, rotation:0 };
    gamesByClient[this.id] = games[game_id];
    
    // Send player_joined to other players
    var socket = clients[this.id];
    socket.broadcast.to(game.gameId).emit("player_joined", {player_id: this.id, username: username});
    
    fn({player_id: this.id, username: username});
}

function onStartGame()
{
    console.log("OnGameStarted event");
    
    var game = gamesByClient[this.id];
    io.to(game.gameId).emit("game_started");
}

function onMove(data)
{
    console.log("OnMove event");
	var game = gamesByClient[this.id];
	
	if(game === undefined)
	    return;
	
	game.players[this.id].x = data.x;
	game.players[this.id].y = data.y;
	game.players[this.id].rotation = data.rotation;
	
	var player = game.players[this.id];
	console.log("Move received and re-emitted: " + this.id + " - " + player);
	
	var socket = clients[this.id];
	var move = { id:this.id, x:player.x, y:player.y, rotation:player.rotation };
    socket.broadcast.to(game.gameId).emit("m", move);
}

