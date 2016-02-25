
var uuid = require('node-uuid');
var amqp = require('amqplib/callback_api');

var players = [];

function updateQueuesForNewPlayer(player_id, ch, room_exchange, client_queue) {
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
    console.log('Creation of game');
    conn.createChannel(function(err, ch) {

	    if (err)
	    {
		    console.log("Error on creation of game channel for " + game_id);
	    }
	    
        var room_exchange = game_id + '_game_room';
        ch.prefetch(1);

        // Declare game_room exchange
        ch.assertExchange(room_exchange, 'direct', {durable: false, autoDelete: false});    

        // JOIN RPC queue
        var join_queue_name = game_id + '_join';

        // declare join_queue
        ch.assertQueue(join_queue_name, {durable: false, autoDelete: false});
        ch.purgeQueue(join_queue_name, function(err, ok) {});
        ch.bindQueue(join_queue_name, room_exchange, 'join');

        console.log('[x] Awaiting new players');
        ch.consume(join_queue_name, function reply(msg) {
            // Create new player id
            var player_id = uuid.v4();
            console.log('[x] New player coming with ID ' + player_id);

            var client_queue = msg.properties.replyTo;
            ch.bindQueue(client_queue, room_exchange, "all");
                        
            updateQueuesForNewPlayer(player_id, ch, room_exchange, client_queue);

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

var create_game_queue_name = "create_game";

function startGameCreationChannel(conn) {
    console.log('Listening for new game creation requests...');

    conn.createChannel(function(err, ch) {

	    ch.assertQueue(create_game_queue_name, {durable: false, autoDelete: false});
	    ch.purgeQueue(create_game_queue_name, function(err, ok) {});             	

	    ch.consume(create_game_queue_name, function reply(msg) {
            //Get a game id;
            var game_id = "abc"; // uuid.v4(); J'ai commenté l'ID UUID car c'est trop compliqué à rentrer manuellement pour faire les tests côté client.

	        //Verify the credentials here:
        	startGameChannel(conn, game_id);
    	    ch.sendToQueue(msg.properties.replyTo, new Buffer(JSON.stringify({'game_id': game_id})), {correlationId: msg.properties.correlationId});               
	    });
    });
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
function start() {	
	amqp.connect(rabbitmquri, function(err, conn) {

	    console.log("Connection to " + rabbitmquri);	    
	    if (err) {
		    onConnectionError(err);
	    }

	    //Create game creation channel
	    startGameCreationChannel(conn);
	});
}

start();
