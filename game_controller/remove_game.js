var GameManager = require('./server/controllers/gamemanager')

//connect to mongo gloabally
var mongodb = require('./server/controllers/mongooseconnect')
var ErrorHandler = require('./server/controllers/errorhandler')

var game_id = process.argv[2] || 'abc';

function remove_game_callback(err)
{
    ErrorHandler.handleError('remove_game', err)
    process.exit();
}

//doesn't remove the exchange, only db data
GameManager.removeGame(game_id, remove_game_callback);