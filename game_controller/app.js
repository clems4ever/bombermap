
var uuid = require('node-uuid');
var amqp = require('amqplib/callback_api');

var players = [];

function updateQueuesForNewPlayer(player_id) {
    for(p in players) {            
        console.log('+ Bind queue ' + client_queue + ' to all_but_' + players[p].player_id + ' routing key.');
        ch.bindQueue(client_queue, room_exchange, 'all_but_' + players[p].player_id);
        
        console.log('+ Bind queue ' + players[p].queue + ' to all_but_' + player_id + ' routing key.');
        ch.bindQueue(players[p].queue, room_exchange, 'all_but_' + player_id);
    }
}

function onConnectionError(err) {
      console.error("[AMQP]", err.message);
      return setTimeout(start, 1000);
}

function startGameChannel(conn, game_id) {
    conn.createChannel(function(err, ch) {

	if (err)
		console.log("error on creation of game channel for " + game_id); 
        var room_exchange = game_id + '_game_room';

        ch.prefetch(1);
        
	// Declare game_room exchange
        ch.assertExchange(room_exchange, 'direct', {durable: false, autoDelete: false});    
	
        // JOIN RPC queue
        var join_queue_name = game_id + '_join';

        // declare join_queue
        ch.assertQueue(join_queue_name, {durable: false, autoDelete: false});

        console.log('[x] Awaiting new players');
        ch.consume(join_queue_name, function reply(msg) {
        
            // Create new player id
            var player_id = uuid.v4();
            console.log('[x] New player coming with ID ' + player_id);
            
            var client_queue = msg.properties.replyTo;
            
            ch.bindQueue(client_queue, room_exchange, "all");
                        
	    updateQueuesForNewPlayer(player_id);

            var player = { 
                'player_id': player_id,
                'queue': client_queue
            }
            players.push(player);
            
            console.log('Reply to ' + client_queue);
            ch.sendToQueue(msg.properties.replyTo, new Buffer(JSON.stringify({'player_id': player_id})), {correlationId: msg.properties.correlationId});
            ch.ack(msg);
        });
    });
}

var global_queue = "global_queue";
function startGameCreationChannel(conn, gamesChannel) {
    conn.createChannel(function(err, ch) {

	ch.assertQueue(global_queue, {durable: false, autoDelete: false});                	

	ch.consume(global_queue, function reply(msg) {
            //Get a game id;
            var game_id = "abc";

	    var game_creation = JSON.parse(msg.content.toString());

	    //Verify the credentials here:
	    if (game_creation.newgame)
	    	startGameChannel(conn, game_id);        
	});
    });
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
function start() {	
	amqp.connect(rabbitmquri, function(err, conn) {

	    console.log("connection to " + rabbitmquri);	    

	    if (err) {
		onConnectionError(err);
	    }

	    //Create game creation channel
	    startGameCreationChannel(conn);
	});
}

start();
