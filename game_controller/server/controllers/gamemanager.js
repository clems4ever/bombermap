var uuid = require('node-uuid');
var RabbitMQWrapper = require('./rabbitmqwrapper');
var PlayerModel = require('../model/playermodel');

exports.handlePlayerJoin = function(game_id, msg) {
    var room_exchange = game_id+"_game_room";
    //RabbitMQWrapper.assertRoomExchange(room_exchange);

    // Create new player id
    var player_id = uuid.v4();
    console.log('[x] New player coming with ID ' + player_id);

    var client_queue = msg.properties.replyTo;

    //get the players in the game to update the bindings
    PlayerModel.getPlayersForGame(game_id, function(players) {
        RabbitMQWrapper.addBindingsForNewPlayer(client_queue, game_id, player_id, players);
        var player = {
            'player_id': player_id,
            'queue_id': client_queue,
            'game_id': game_id
        };
        var msgToSend = JSON.stringify({
            'player_id':player_id,
            'players':players
        });
        PlayerModel.addPlayer(player);
        RabbitMQWrapper.replyToClient(client_queue, msg, msgToSend);
    });
}

exports.handlePlayerLeaveGame = function(game_id, player_id, msg)
{
    var room_exchange = game_id+"_game_room";
    RabbitMQWrapper.assertRoomExchange(room_exchange);

    //Get the players in the game to remove the queue
    //of the leaving player
    PlayerModel.getPlayersForGame(game_id, function(players) {
        RabbitMQWrapper.unbindQueueForLeave(client_queue, game_id, player_id, players);
    });
    PlayerModel.removePlayer(player_id);

    var client_queue = msg.properties.replyTo;
    var msgToSend = JSON.stringify({});
    RabbitMQWrapper.replyToClient(client_queue, msg, msgToSend);
}

exports.createGameExchange = function(game_id, msg) {
    console.log('Creation of game : ' + game_id);

    // Declare game_room exchange
    var room_exchange = game_id + '_game_room';
    RabbitMQWrapper.assertRoomExchange(room_exchange);

    //reply to user request
    var client_queue = msg.properties.replyTo;
    var msgToSend = JSON.stringify({'game_id':game_id});
    RabbitMQWrapper.replyToClient(client_queue, msg, msgToSend);
}

exports.removeGame = function(game_id, callback) {
    PlayerModel.removePlayersForGame(game_id, callback);
}