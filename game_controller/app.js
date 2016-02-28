
var uuid = require('node-uuid');
var amqp = require('amqplib/callback_api');

var players = {};

function updateQueuesForNewPlayer(player_id, ch, room_exchange, client_queue) {
    var toUnbindQueues = [];

    for(oplayer_id in players) {
        var oplayer_queue = players[oplayer_id].queue;
        
        ch.checkQueue(oplayer_queue, function(err, ok) {
            if(err) {
                console.log("Player " + oplayer_id + " has been disconnected.");
                toUnbindQueues.push(oplayer_id);
                
                delete players[oplayer_id];
            }
            
        });
    }

    bindQueue(ch, client_queue, room_exchange, player_id);
}

function bindQueue(ch, client_queue, room_exchange, player_id) {
    console.log("+ Bind queue " + client_queue + " to only_" + player_id);

    ch.bindQueue(client_queue, room_exchange, "all");
    ch.bindQueue(client_queue, room_exchange, "only_" + player_id);

    for(oplayer_id in players) {
        var oplayer_queue = players[oplayer_id].queue;
        
       // Add routing keys from all players and bind them to this new queue 
        console.log('+ Bind queue ' + client_queue + ' to all_but_' + oplayer_id + ' routing key.');
        ch.bindQueue(client_queue, room_exchange, 'all_but_' + oplayer_id);
        
        // Add new routing key and bind it to all player queues
        console.log('+ Bind queue ' + oplayer_queue + ' to all_but_' + player_id + ' routing key.');
        ch.bindQueue(oplayer_queue, room_exchange, 'all_but_' + player_id);
    }
}

function unbindQueue(ch, client_queue, room_exchange, player_id) {
    ch.unbindQueue(client_queue, room_exchange, "all");
    ch.unbindQueue(client_queue, room_exchange, "only_" + player_id);
    
    for(oplayer_id in players) {
        var oplayer_queue = players[oplayer_id].queue;
        
        console.log('+ UnBind queue ' + client_queue + ' to all_but_' + oplayer_id + ' routing key.');
        ch.unbindQueue(client_queue, room_exchange, 'all_but_' + oplayer_id);
        
        console.log('+ UnBind queue ' + oplayer_queue + ' to all_but_' + player_id + ' routing key.');
        ch.unbindQueue(oplayer_queue, room_exchange, 'all_but_' + player_id);
   }
}

function onConnectionError(err) {
      console.error("[AMQP]", err.message);
      return setTimeout(start, 1000);
}

function startGameChannel(ch, game_id) {
    var room_exchange = game_id + '_game_room';
    ch.prefetch(1);

    // Declare game_room exchange
    ch.assertExchange(room_exchange, 'direct', {durable: false, autoDelete: false});
}

function createGame(ch, msg) {
    //Get a game id;
    var game_id = "abc"; // uuid.v4(); J'ai commenté l'ID UUID car c'est trop compliqué à rentrer manuellement pour faire les tests côté client.
    console.log('[x] New game created with ID ' + game_id);
    
    players = {};

    //Verify the credentials here:
	startGameChannel(ch, game_id);
    ch.sendToQueue(msg.properties.replyTo, new Buffer(JSON.stringify({'game_id': game_id})), {correlationId: msg.properties.correlationId});
    ch.ack(msg);
}

function joinGame(ch, msg) {
    // Create new player id
    var player_id = uuid.v4();
    console.log('[x] New player coming with ID ' + player_id);

    var room_exchange = JSON.parse(msg.content.toString()).game_id + "_game_room";
    var client_queue = msg.properties.replyTo;
    
    console.log("Exchange = " + room_exchange);
    updateQueuesForNewPlayer(player_id, ch, room_exchange, client_queue);

    var player = { 
        'queue': client_queue
    }
    players[player_id] = player;
    
    console.log("Players = " + JSON.stringify(players));

    ch.sendToQueue(msg.properties.replyTo, new Buffer(JSON.stringify({'player_id': player_id})), {correlationId: msg.properties.correlationId});
    ch.ack(msg);
}

function leaveGame(ch, msg) {
    var player_id = JSON.parse(msg.content.toString()).player_id;
    var room_exchange = "abc_game_room";
    var player_queue = players[player_id].queue;
    console.log('[x] Player with ID ' + player_id + ' is leaving');
    
    ch.unbindQueue(player_queue, room_exchange, "all");
    ch.unbindQueue(player_queue, room_exchange, "only_" + player_id);
    
    delete players[player_id];
    
    for(oplayer_id in players) {
        var oplayer_queue = players[oplayer_id].queue;
        ch.unbindQueue(oplayer_queue, room_exchange, 'all_but_' + player_id);
    }
    
    ch.sendToQueue(msg.properties.replyTo, new Buffer("{}"), {correlationId: msg.properties.correlationId});
    ch.ack(msg);
}

var create_game_queue_name = "create_game";
var join_game_queue_name = "join_game";
var leave_game_queue_name = "leave_game";

function createGlobalQueue(conn) {
    console.log('Creating global queue...');

    conn.createChannel(function(err, ch) {

        // Create game queue declaration
	    ch.assertQueue(create_game_queue_name, {durable: false, autoDelete: false});
	    ch.purgeQueue(create_game_queue_name, function(err, ok) {});             	
	    ch.consume(create_game_queue_name, function reply(msg) {
	        createGame(ch, msg);
	    });
	    
	    // Join game queue declaration
	    ch.assertQueue(join_game_queue_name, {durable: false, autoDelete: false});
	    ch.purgeQueue(join_game_queue_name, function(err, ok) {});             	
	    ch.consume(join_game_queue_name, function reply(msg) {
	        joinGame(ch, msg);
	    });
	    
	    ch.assertQueue(leave_game_queue_name, {durable: false, autoDelete: false});
	    ch.purgeQueue(leave_game_queue_name, function(err, ok) {});             	
	    ch.consume(leave_game_queue_name, function reply(msg) {
	        leaveGame(ch, msg);
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
	    createGlobalQueue(conn);
	});
}

start();
