// Load required packages
var mongoose = require('mongoose');

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

function handleError(err) {
    if (err != null)
        console.error(err);
}

exports.getPlayersForGame = function(game_id, callback) {
    PlayerModel.find({'game_id':game_id}, function(err, players) {
        handleError(err);
        callback(players);
    });
}

exports.addPlayerToGame = function(player) {
    var player_model = new PlayerModel(player);
    player_model.save(handleError);
}

exports.removePlayer = function(player_id) {
    var player = {'player_id': player_id};
    PlayerModel.remove(player, handleError);
}

exports.removePlayersForGame = function(game_id) {
    var game = {'game_id':game_id}
    PlayerModel.remove(game , handleError);
}
