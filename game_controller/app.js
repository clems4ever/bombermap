
var uuid = require('node-uuid');
var amqp = require('amqplib/callback_api');

var rabbitmq_uri = 'amqp://localhost';
var game_id = "abc";

console.log('Starting game ' + game_id);

amqp.connect(rabbitmq_uri, function(err, conn) {
    conn.createChannel(function(err, ch) {
        var room_exchange = game_id + '_game_room';

        ch.prefetch(1);

        // Declare game_room exchange
        ch.assertExchange(room_exchange, 'direct', {durable: false, autoDelete: true});    

        // JOIN RPC queue
        var q = game_id + '_join';

        // declare join_queue
        ch.assertQueue(q, {durable: false, autoDelete: true});

        console.log(' [x] Awaiting new players');
        ch.consume(q, function reply(msg) {
        
            // Create new player id
            var player_id = uuid.v4();
            console.log(' [x] New player coming with ID ' + player_id);
            
            ch.bindQueue(q.queue, room_exchange, player_id );

            ch.sendToQueue(msg.properties.replyTo, new Buffer(JSON.stringify({'player_id': player_id})), {correlationId: msg.properties.correlationId});
            ch.ack(msg);
        });
    });

    //setTimeout(function() { conn.close(); process.exit(0) }, 500);
});


