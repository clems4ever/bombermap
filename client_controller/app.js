var amqp = require('amqplib/callback_api');
var uuid = require('node-uuid');
var event_factory = require('./controller/event_creator')

var room_exchange_config = {durable:false, autoDelete:true};
var global_queue_config = {durable:false, autodelete:false};
var client_queue_config = {exclusive: false, autoDelete: true};

var server_update_increment = 1000;

var create = false;		
var game_id = "abc";
var client_channel = null;
var client_queue = null;
var player_ids = [];
var time = 0;
var room_exchange = "abc_game_room";

var fs = require('fs');

var parislong = 2.3228797;
var parislat = 48.8870407;

var offset1 = 0.01;
var offset2 = 0.01;

function handleError(err) {
	if (err) {
		console.error("[AMQP]", err.message);
		setTimeout(start, 1000);
	}
}

function joinGame() {
	var corr = uuid.v4();
	if (game_id) {	
		console.log("joining game " + game_id);
		var game_join_msg = {'action' : 'join',
                             'game_id' : game_id};
		client_channel.assertQueue(global_queue, global_queue_config);
        client_channel.sendToQueue(global_queue,
			new Buffer(JSON.stringify(game_join_msg)),
			{ correlationId: corr, replyTo: client_queue });
	}	
}

function consumeCallback(msg) {
    console.log(' [.] Got %s', msg.content.toString());
	var content = JSON.parse(msg.content.toString());
	if (content.player_id) {
		console.log("consuming player id " + content.player_id);
		player_ids.push(content.player_id);

        room_exchange = game_id+"_game_room";

        client_channel.assertExchange(room_exchange, 'direct', room_exchange_config);
	}
    else if (content.ch && content.ch == "clock_sync") {
        time = content.co.ticks*server_update_increment;
    }
    else if (content.ch && content.ch != "clock_sync") {
        /*fs.appendFile("/tmp/test", JSON.stringify(content)+",", function(err) {
            if(err) {
                return console.log(err);
            }

            console.log("json saved to file");
        }); */
    }
    client_channel.ack(msg);
}

function sendEvent(event) {
    if (room_exchange) {
        console.log("[x] Sending event : "+JSON.stringify(event));
        client_channel.assertExchange(room_exchange, 'direct', room_exchange_config);
        client_channel.publish(room_exchange, "all", new Buffer(JSON.stringify(event)));
    }
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://server:server@broker.wargame.ingenious-cm.fr";
rabbitmquri += "?heartbeat=60";
var global_queue = "global_queue";
function start() {	
	amqp.connect(rabbitmquri, function(err, conn) {
		handleError(err);

		//create a channel for communication
		conn.createChannel(function(err, ch) {
			handleError(err);
			client_channel = ch;
			//create the client queue
                ch.assertQueue('', client_queue_config, function (err, q) {
                    handleError(err);
                    client_queue = q.queue;
                    
                    ch.consume(client_queue, consumeCallback);
                    
                    ch.bindQueue(client_queue, room_exchange, 'all');
                    /*setTimeout(function() {
                        joinGame();			    
                    }, 1000);
                    
                    setTimeout(function() {
                        joinGame();			    
                    }, 2000);
                    
                    setTimeout(function() {
                        joinGame();			    
                    }, 3000);
                    
                    setTimeout(function() {
                        joinGame();			    
                    }, 4000);
                    
                        setInterval(function() {
                            for (var i = 0; i<4; i++)
                                if (player_ids[i])
                                    event_factory.pushEvent(event_factory.createFireEvent(player_ids[i%4], 
                                                                                            {lat: parislat+offset1*Math.random(),
                                                                                            long:parislong+offset1*Math.random()}, 
                                                                                            time+1000));    
                            for (var i = 0; i<4; i++)
                                if (player_ids[i])
                                    event_factory.pushEvent(event_factory.createMoveEvent(player_ids[i], 
                                                                                    {lat: parislat+offset2*Math.random(), 
                                                                                    long:parislong+offset2*Math.random()}, 
                                                                                    time+1000));    
                        }, 1000);
                    */
                
                    setInterval(function() {
                        time += 200;
                        var events = event_factory.getEvents(time);
                        console.log("time : "+time+" found events : "+JSON.stringify(events));
                        if (events != null)
                            events.forEach(function (event) {
                                sendEvent(event); 
                            });
                    }, 200);    
                });
		    });  
	});
}

start();
