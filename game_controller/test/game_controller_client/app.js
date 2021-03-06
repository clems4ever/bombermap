var amqp = require('amqplib/callback_api');
var uuid = require('node-uuid');
var global_queue_config = {durable:false, autodelete:false};
var client_queue_config = {exclusive: false, autoDelete: true};

var create = false;		
var game_id = null;
var client_channel = null;
var client_queue = null;
var player_id = null;


function handleError(err) {
	if (err) {
		console.error("[AMQP]", err.message);
		setTimeout(start, 1000);
	}
}

exports.joinGame = function(game_id) {
	var corr = uuid.v4();
	if (game_id)
	{	
		console.log("joining game " + game_id);
		var game_join_msg = {'action' : 'join',
                             'game_id' : game_id};
		client_channel.sendToQueue(global_queue,
			new Buffer(JSON.stringify(game_join_msg)),
			{ correlationId: corr, replyTo: client_queue });
	}	
}

exports.sendHello = function(player_id)
{
    var coucou = {coucou:"coucou by player : "+player_id};
    console.log("sending coucou");
    client_channel.publish(game_id+"_game_room", "all_but_"+player_id, new Buffer(JSON.stringify(coucou)));
}

function sendRandomPos(player_id)
{
    var lat = 10;
    var long = 10;
    var move = {ch:"move", co:{'player_id':player_id, 'lat':lat, 'long':long}};
    console.log("sending move");
    client_channel.publish(game_id+"_game_room", "all_but_"+player_id, new Buffer(JSON.stringify(move)));
}

function sendJoin(player_id)
{
    var move = {ch:"player_join", co:{'player_id':player_id}};
    console.log("sending join");
    client_channel.publish(game_id+"_game_room", "all_but_"+player_id, new Buffer(JSON.stringify(move)));
}

function consumeCallback(msg) {
	console.log(' [.] Got %s', msg.content.toString());
	var content = JSON.parse(msg.content.toString());
	if (content.game_id)
	{
		console.log("consuming game id " + content.game_id);	
		game_id = content.game_id;
		joinGame();
	}
	if (content.player_id)
	{
		console.log("consuming player id " + content.player_id);
		player_id = content.player_id;

        var room_exchange = game_id+"_game_room";
        console.log("sending coucou to exchange : " + room_exchange);
        console.log("via key : " + "all_but_"+player_id);

        //client_channel.assertExchange(room_exchange, 'direct', room_exchange_config);
        sendJoin(player_id);
        setInterval(function(){sendRandomPos(player_id)}, 1000);
	}
	if (content.coucou)
	{
		console.log("consuming coucou " + content.coucou);
	}
	client_channel.ack(msg);
}

exports.createGame = function() {
	console.log("[x] ask for game creation" + JSON.stringify(client_queue))
	var corr = uuid.v4();
	var game_create = {action : "newgame"};
    //client_channel.assertQueue(global_queue, global_queue_config);
	console.log("[x] global queue exists");

	client_channel.sendToQueue(global_queue,
		new Buffer(JSON.stringify(game_create)),
		{ correlationId: corr, replyTo: client_queue });
}

function gameCreateJoin()
{
	console.log(' [x] Requesting game');
	if (!game_id) {
		console.log("creating game");				
		createGame();			
	}
	else {
		console.log("joining game "+ game_id);
		joinGame(game_id);
	}
}


function initGameId()
{
	if (process.argv[2]) {
		//if a game is provided by CLI, we join instead of creating
		console.log("game provided : " +process.argv[2]); 		
		game_id = process.argv[2];		
	} else {
		console.log("game not provided"); 		
		game_id = null;
	}
}

function initializeGame()
{
	//check if game id provided
	initGameId();
	//Setting the consume callback for the client queue	
	client_channel.consume(client_queue, consumeCallback);
	//Creating or joining a game depending on game_id being provided
	gameCreateJoin();
}

exports.initChannel = function(ch) {
	client_channel = ch;
}

exports.initQueue = function(queue) {
	client_queue = queue
}

var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
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
				this.initQueue(q.queue);
				initializeGame();
			});
		});
	});
}

//start();
