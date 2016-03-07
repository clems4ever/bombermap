var amqp = require('amqplib/callback_api');
var ErrorHandler = require("./errorhandler");

var ampq_prefix = "[AMPQ]";

var server_channel = null;
var room_exchange_config = {durable:false, autoDelete:true};
var global_queue_config = {durable:false, autoDelete:false};

exports.addBindingsForNewPlayer = function(client_queue, game_id, player_id, players) {
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

exports.unbindQueueForLeave = function(client_queue, room_exchange, player_id, players) {
    server_channel.unbindQueue(client_queue, room_exchange, "all");

    for(p in players) {
        var other_player = players[p];
        if (other_player.player_id != player_id) {
            console.log('+ UnBind queue ' + client_queue + ' to all_but_' + other_player.player_id + ' routing key.');
            server_channel.unbindQueue(client_queue, room_exchange, 'all_but_' + other_player.player_id);

            console.log('+ UnBind queue ' + other_player.queue_id + ' to all_but_' + player_id + ' routing key.');
            server_channel.unbindQueue(other_player.queue_id, room_exchange, 'all_but_' + player_id);
        }
    }
}

exports.replyToClient = function(client_queue, receivedMsg, msgToSend) {
    server_channel.sendToQueue(client_queue,
        new Buffer(msgToSend),
        {correlationId: receivedMsg.properties.correlationId});
}

exports.assertRoomExchange = function(room_exchange) {
    server_channel.assertExchange(room_exchange, "direct", room_exchange_config);
}


var global_queue = "global_queue";
function startGameCreationWorker(consume_callback) {
    server_channel.assertQueue(global_queue, global_queue_config);

    server_channel.consume(global_queue, consume_callback);
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";
exports.initServerChannel = function(consume_callback) {
    console.log("Connection to " + rabbitmquri);
    amqp.connect(rabbitmquri, function(err, conn) {

        ErrorHandler.handleError(ampq_prefix, err);
        //Create game creation channel
        conn.createChannel(function(err, ch) {
            ErrorHandler.handleError(ampq_prefix, err);

            server_channel = ch;
            startGameCreationWorker(consume_callback);
        });
    });
}