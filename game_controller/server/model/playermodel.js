// Load required packages
var mongoose = require('mongoose');
var ErrorHandler = require('../controllers/errorhandler')
var RabbitMQWrapper = require('../controllers/rabbitmqwrapper')
var player_model_prefix = "[PlayerModel]"

// Define our player schema
var PlayerSchema = new mongoose.Schema({
  player_id: {
    type: String,
    unique: true,
    required: true
  },
  queue_id:String,
  game_id:String
});

var PlayerModel = mongoose.model('Player', PlayerSchema);

exports.getPlayersForGame = function(game_id, callback) {
    PlayerModel.find({'game_id':game_id}, function(err, players) {
        ErrorHandler.handleError(player_model_prefix, err);
        players.forEach(function(player) {
                RabbitMQWrapper.checkIfQueueExists(player.queue_id, function () {
                    exports.removePlayer(player.player_id);
                    //players.splice(p, 1);
                });
        });
        //callback(players);
    });
}

exports.addPlayer = function(player) {
    var player_model = new PlayerModel(player);
    player_model.save(function(err) {
        ErrorHandler.handleError(player_model_prefix, err);
    });
}

exports.removePlayer = function(player_id) {
    var player = {'player_id': player_id};
    PlayerModel.remove(player, function (err) {
       console.log(err);
    });
}

exports.removePlayersForGame = function(game_id, callback) {
    var game = {'game_id':game_id}
    PlayerModel.remove(game, callback);
}
