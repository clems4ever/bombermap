
var uuid = require('node-uuid');
var amqp = require('amqplib/callback_api');

var rabbitmq_uri = 'amqp://localhost';
var game_id = "abc";

console.log('Starting game ' + game_id);


var players = [];

amqp.connect(rabbitmq_uri, function(err, conn) {
    conn.createChannel(function(err, ch) {
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
            for(p in players) {            
                console.log('+ Bind queue ' + client_queue + ' to all_but_' + players[p].player_id + ' routing key.');
                ch.bindQueue(client_queue, room_exchange, 'all_but_' + players[p].player_id);
                
                console.log('+ Bind queue ' + players[p].queue + ' to all_but_' + player_id + ' routing key.');
                ch.bindQueue(players[p].queue, room_exchange, 'all_but_' + player_id);
            }
            
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
});


