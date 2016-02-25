var amqp = require('amqplib/callback_api');
var uuid = require('node-uuid');
var create = false;		
		

function joinGame(ch, queue, game_id) {
	var corr = uuid.v4();
	if (game_id)
	{	
		console.log("joining game " + game_id);
		var join_game = {};
		var game_join = game_id + "_join";
		ch.sendToQueue(game_join,
		new Buffer(JSON.stringify(join_game)),
		{ correlationId: corr, replyTo: queue });
	}	
}

var game_id = null;
function consume(msg) {

	var content = JSON.parse(msg.content.toString());
	if (content.game_id)
	{
		console.log("consuming game id " + content.game_id);	
		game_id = content.game_id;
		joinGame(ch, q.queue, game_id);
	}
	if (content.player_id)
	{
		console.log("consuming join reply " + content.player_id);	
		ch.publish("", game_id+"_game_room", new Buffer("coucou"));
	}

}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
var global_queue = "global_queue";
function start() {	
	amqp.connect(rabbitmquri, function(err, conn) {
		if (err) {
			onConnectionError(err);
		}

		//if a game is provided by CLI, we join instead of creating
		if (!process.argv)		
			create = true;
		else
			game_id = process.argv[2];
		console.log("no game created, joining : " + game_id);
		var game_create = {newgame : create};
		
		//create a channel for communication
		conn.createChannel(function(err, ch) {
		ch.assertQueue('', {exclusive: false}, function(err, q) {
			var corr = uuid.v4();

			console.log(' [x] Requesting game');

			ch.consume(q.queue, function(msg) {
				console.log(' [.] Got %s', msg.content.toString() + " " + corr);
				consume(msg);
	
			}, {noAck: true});
			
			//Send a game creation message
			
			ch.sendToQueue('global_queue',
			new Buffer(JSON.stringify(game_create)),
			{ correlationId: corr, replyTo: q.queue });
						
			});
		});
	});
}

start();
