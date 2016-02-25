var amqp = require('amqplib/callback_api');

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
var global_queue = "global_queue";
function start() {	
	amqp.connect(rabbitmquri, function(err, conn) {
		if (err) {
			onConnectionError(err);
		}

		//Send a game creation message
		var game_create = {newgame : true};

		//create a channel for communication
		conn.createChannel(function(err, ch) {
			console.log("sending message : " + JSON.stringify(game_create));
			setInterval(function() { ch.publish("", global_queue, new Buffer(JSON.stringify(game_create))); }, 1000);			
		});	
	});
}

start();
