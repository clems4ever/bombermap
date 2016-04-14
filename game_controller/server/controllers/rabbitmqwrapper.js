var amqp = require('amqplib/callback_api');
var ErrorHandler = require("./errorhandler");


var ampq_prefix = "[AMPQ]";

var connection = null;
var server_channel = null;

var clock_exchange_config = {durable:false, autoDelete:false};
var room_exchange_config = {durable:false, autoDelete:true};
var client_queue_config = {durable:false, autoDelete:true, passive:true};
var global_queue_config = {durable:false, autoDelete:false};

exports.addBindingsForNewPlayer = function(client_queue, game_id, player_id, players) {
    var room_exchange = game_id + "_game_room";

    console.log('+ Bind queue ' + client_queue + ' to all routing key.');
    server_channel.bindQueue(client_queue, room_exchange, 'all');

    players.forEach(function(other_player) {
        exports.checkIfQueueExists(other_player.queue_id, function(){}, function() {
            console.log('+ Bind queue ' + client_queue + ' to all_but_' + other_player.player_id + ' routing key.');
            server_channel.bindQueue(client_queue, room_exchange, 'all_but_' + other_player.player_id);

            console.log('+ Bind queue ' + other_player.queue_id + ' to all_but_' + player_id + ' routing key.');
            server_channel.bindQueue(other_player.queue_id, room_exchange, 'all_but_' + player_id);

            exports.sendJoinMessage(client_queue, other_player.player_id);
            exports.sendJoinMessage(other_player.queue_id, player_id);
        });
    });
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
    server_channel.ack(receivedMsg);
}

exports.assertRoomExchange = function(room_exchange) {
    server_channel.assertExchange(room_exchange, "direct", room_exchange_config);
}


function startGameCreationWorker(queue_name, consume_callback) {
    server_channel.assertQueue(queue_name, global_queue_config);
    server_channel.assertExchange('clock_exchange', "fanout", clock_exchange_config);

    server_channel.consume(queue_name, consume_callback);
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://server:server@broker.wargame.ingenious-cm.fr";
if(process.env.AMQP_VIRTUAL_HOST) {
rabbitmquri += process.env.AMQP_VIRTUAL_HOST;
}
rabbitmquri += "?heartbeat=60";
exports.initServerChannel = function(queue_name, consume_callback, on_channel_ready) {
    console.log("Connection to " + rabbitmquri);
    amqp.connect(rabbitmquri, function(err, conn) {
        ErrorHandler.handleError(ampq_prefix, err);

        connection = conn;
        //Create game creation channel
        conn.createChannel(function(err, ch) {
            ErrorHandler.handleError(ampq_prefix, err);
            server_channel = ch;
            server_channel.on('error', function(err) {
                console.log(err);
            });
            on_channel_ready();
            startGameCreationWorker(queue_name, consume_callback);
        });
    });
}

exports.purgeGlobalQueue = function(global_queue) {
    server_channel.purgeQueue(global_queue);
}

exports.clearAllQueues = function() {

}

exports.sendMessage = function(client_queue, message) {
    var buffer = new Buffer(message);
    server_channel.sendToQueue(client_queue, buffer);
}

exports.checkIfQueueExists = function(client_queue, ifNotExists, ifExists) {

    connection.createChannel(function(err, ch) {
        ErrorHandler.handleError(ampq_prefix, err);
        ch.checkQueue(client_queue, function(err) {
            if (err)
                ifNotExists();
            else
                ifExists()
            ch.on('error', function(err) {
                console.log(err);
            });
        });
    });
}

exports.sendClockSync = function(clock_exchange, ticks) {
    var message = JSON.stringify({'ch':'clock_sync',
                                  'co':{'ticks': ticks}
                                 });
    console.log('[x] Sending clock sync : ' + ticks);
    var buffer = new Buffer(message);
    server_channel.publish(clock_exchange, "all", buffer);
}

exports.sendJoinMessage = function(client_queue, player_id)
{
    var message = JSON.stringify({'ch':'player_join',
        'co':{'player_id': player_id}
    });
    console.log('[x] Player confirmed : ' + player_id + ' for '+ client_queue);
    var buffer = new Buffer(message);
    server_channel.sendToQueue(client_queue, buffer);
}

exports.bindExchange = function (exchange1, exchange2) {
    console.log('+ Bind exchange ' + exchange1 + ' to exchange' + exchange2);
    server_channel.bindExchange(exchange1, exchange2);
}
