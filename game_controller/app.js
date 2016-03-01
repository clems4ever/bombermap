
var uuid = require('node-uuid');
var amqp = require('amqplib/callback_api');
var mongodb = require('./server/controllers/mongooseconnect')
var PlayerModel = require("./server/model/playermodel");

var server_channel = null;
var room_exchange_config = {durable:false, autoDelete:true};
var global_queue_config = {durable:false, autoDelete:false};


function addBindingsForNewPlayer(client_queue, game_id, player_id, players) {
    var room_exchange = game_id + "_game_room";

    console.log('+ Bind queue ' + client_queue + ' to all routing key.');
    server_channel.bindQueue(client_queue, room_exchange, 'all');

    for (p in players) {
        var other_player = players[p];
        console.log('+ Bind queue ' + client_queue + ' to all_but_' + other_player.player_id + ' routing key.');
        server_channel.bindQueue(client_queue, room_exchange, 'all_but_' + other_player.player_id);

        console.log('+ Bind queue ' + other_player.queue_id + ' to all_but_' + player_id + ' routing key.');
        server_channel.bindQueue(other_player.queue_id, room_exchange, 'all_but_' + player_id);
    }
}

function unbindQueueForLeave(ch, client_queue, room_exchange, player_id, players) {
    ch.unbindQueue(client_queue, room_exchange, "all");

    for(p in players) {
        var other_player = players[p];
        if (other_player.player_id != player_id) {
            console.log('+ UnBind queue ' + client_queue + ' to all_but_' + other_player.player_id + ' routing key.');
            ch.unbindQueue(client_queue, room_exchange, 'all_but_' + other_player.player_id);

            console.log('+ UnBind queue ' + other_player.queue_id + ' to all_but_' + player_id + ' routing key.');
            ch.unbindQueue(other_player.queue_id, room_exchange, 'all_but_' + player_id);
        }
    }
}

function handleError(err) {
	if (err) {
		console.error("[AMQP]", err.message);      
		setTimeout(start, 1000);
	}
}

function addNewPlayer(client_queue, game_id, player_id) {
    var player = {
        'player_id': player_id,
        'queue_id': client_queue,
        'game_id': game_id
    };
    PlayerModel.addPlayer(player);
}

function replyToJoiningPlayer(client_queue, game_id, player_id, players, msg) {
    console.log('Reply to ' + client_queue);
    server_channel.sendToQueue(client_queue,
        new Buffer(JSON.stringify({'player_id': player_id, 'players' : players})),
        {correlationId: msg.properties.correlationId});
}

function handlePlayerJoin(game_id, msg) {
    var room_exchange = game_id+"_game_room";
    server_channel.assertExchange(room_exchange, "direct", room_exchange_config);

    // Create new player id
	var player_id = uuid.v4();
	console.log('[x] New player coming with ID ' + player_id);

	var client_queue = msg.properties.replyTo;

    //get the players in the game to update the bindings
    PlayerModel.getPlayersForGame(game_id, function(players) {
        addBindingsForNewPlayer(client_queue, game_id, player_id, players);
        addNewPlayer(client_queue, game_id, player_id);
        replyToJoiningPlayer(client_queue, game_id, player_id, players, msg);
    });
}

function handlePlayerLeaveGame(game_id, player_id, msg)
{
    var room_exchange = game_id+"_game_room";
    server_channel.assertExchange(room_exchange, "direct", room_exchange_config);

    //Get the players in the game to remove the queue
    //of the leaving player
    PlayerModel.getPlayersForGame(game_id, function(players) {
        unbindQueueForLeave(client_queue, game_id, player_id, players);
    });
    PlayerModel.removePlayer(player_id);

    var client_queue = msg.properties.replyTo;
    console.log('Reply to ' + client_queue);
    server_channel.sendToQueue(client_queue,
        new Buffer(JSON.stringify({})),
        {correlationId: msg.properties.correlationId});
}

function createGameExchange(game_id, msg) {
	console.log('Creation of game : ' + game_id);

	server_channel.prefetch(1);

	// Declare game_room exchange
	var room_exchange = game_id + '_game_room';
	server_channel.assertExchange(room_exchange, 'direct', room_exchange_config);

    //reply to user request
    var client_queue = msg.properties.replyTo;
    server_channel.sendToQueue(client_queue,
        new Buffer(JSON.stringify({'game_id': game_id})),
        {correlationId: msg.properties.correlationId});
}

function removeGame(game_id) {
    PlayerModel.removePlayersForGame(game_id);
}

var global_queue = "global_queue";
function startGameCreationWorker() {
	server_channel.assertQueue(global_queue, global_queue_config);

	server_channel.consume(global_queue, function reply(msg) {
        var content = JSON.parse(msg.content.toString());

        //Verify the credentials here:
        if (content.action == "newgame") {
            //Get a game id;
            var game_id = process.env.DEBUG_GAME_ID || uuid.v4();
            //create the exchange for the game
            createGameExchange(game_id, msg);
        }
        else if (content.action == "join") {
            var game_id = content.game_id;
            handlePlayerJoin(game_id, msg);
        }
        else if (content.action == "leave")
        {
            var game_id = content.game_id;
            var player_id = content.player_id;
            handlePlayerLeaveGame(game_id, player_id, msg);
        }
        else if (content.action == "remove") {
            var game_id = content.game_id;
            removeGame(game_id);
        }
    });
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
function start() {	
	console.log("Connection to " + rabbitmquri);	    
	amqp.connect(rabbitmquri, function(err, conn) {

		handleError(err);
		//Create game creation channel
		conn.createChannel(function(err, ch) {
			handleError(err);
			server_channel = ch;
			startGameCreationWorker(ch);
		});
	});
}

start();
