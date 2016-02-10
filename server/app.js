var express = require("express");
var app = express();
var server = require("http").Server(app);

var io = require("socket.io").listen(server);

var Player = require("./game/entities/player");
var Weapon = require("./game/entities/weapon");

var games = {};

var updateInterval = 50;

init();

function init() {
	// Begin listening for events.
	setEventHandlers();

	// Start game loop
	setInterval(broadcastingUpdate, updateInterval);
};

function setEventHandlers() {
	io.on("connection", function(client) {
		console.log("New player has connected: " + client.id);
	
		client.on("move", onMove);
	});
};

function onMove(data)
{
	var game = games[this.gameId];
	game.players[this.id].x = data.x;
	game.players[this.id].y = data.y;
	game.players[this.id].o = data.o;
}

function broadcastingUpdate() {
   
   var lenGames = games.length
   for (var g = 0; g<lenGames; g++)
   {
        var game = games[g];
        var lenPlayers = game.players.length
        for (var p = 0; p<lenPlayers; p++)
        {
            io.to(g).emit("m", {id:player.id, x:player.x, y:player.y, o:player.orientation });
	}
   }

}
