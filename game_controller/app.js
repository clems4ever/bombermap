var uuid = require('node-uuid');
var GameManager = require('./server/controllers/gamemanager')
var RabbitMQWrapper = require('./server/controllers/rabbitmqwrapper');

//connect to mongo gloabally
var mongodb = require('./server/controllers/mongooseconnect')

function consumeCallback(msg) {
    var content = JSON.parse(msg.content.toString());

    //Verify the credentials here:
    var game_id = content.game_id || process.env.DEBUG_GAME_ID || uuid.v4();
    if (content.action == "newgame") {
        //Get a game id;
        //create the exchange for the game
        GameManager.createGameExchange(game_id, msg);
    }
    else if (content.action == "join") {
        GameManager.handlePlayerJoin(game_id, msg);
    }
    else if (content.action == "leave")
    {
        var player_id = content.player_id;
        GameManager.handlePlayerLeaveGame(game_id, player_id, msg);
    }
    else if (content.action == "remove") {
        GameManager.removeGame(game_id);
    }
}

function start() {
    RabbitMQWrapper.initServerChannel(consumeCallback);
}
start();
