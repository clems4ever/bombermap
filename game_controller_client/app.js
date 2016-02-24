var amqp = require('amqplib/callback_api');
var uuid = require('node-uuid');

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
var global_queue = "global_queue";
var abc_join = "abc_join";
function start() {	
	amqp.connect(rabbitmquri, function(err, conn) {
		if (err) {
			onConnectionError(err);
		}

		//Send a game creation message
		var game_create = {newgame : true};
		var join_game = {};

		//create a channel for communication
		conn.createChannel(function(err, ch) {
		ch.assertQueue('', {exclusive: false}, function(err, q) {
			var corr = uuid.v4();

			console.log(' [x] Requesting game');

			ch.consume(q.queue, function(msg) {
				if (msg.properties.correlationId == corr) {
					console.log(' [.] Got %s', msg.content.toString());
					setTimeout(function() { conn.close(); process.exit(0) }, 500);
				}
			}, {noAck: true});

			ch.sendToQueue('global_queue',
			new Buffer(JSON.stringify(game_create)),
			{ correlationId: corr, replyTo: q.queue });
			
			ch.sendToQueue('abc_join',
			new Buffer(JSON.stringify(join_game)),
			{ correlationId: corr, replyTo: q.queue });
			
			});
		});
	});
}

start();
