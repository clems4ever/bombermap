var assert = require('assert');
var amqp = require('amqplib/callback_api');
var uuid = require('node-uuid');
var game_client = require('./game_controller_client/app')
var game_server = require('../server/controllers/gamemanager')
var ErrorHandler = require('../server/controllers/errorhandler')

var client_queue_config = {exclusive: false, autoDelete: true};
var rabbitmquri = process.env.CLOUDAMQP_URL || "amqp://localhost";
rabbitmquri += "?heartbeat=60";

describe('game client', function(){

  var game_id = null;

  before(function(done) {

    amqp.connect(rabbitmquri, function(err, conn) {
      //create a channel for communication
      conn.createChannel(function(err, ch) {
        ErrorHandler.handleError(err);

        //init the client channel
        game_client.initChannel(ch);


        //create the client queue
        ch.assertQueue('', client_queue_config, function (err, q) {
          ErrorHandler.handleError(err);

          var client_queue = q.queue;

          ch.consume(client_queue, function(msg) {
            console.log("consumed");
            var content = JSON.parse(msg.content.toString());
            game_id = content.game_id;
            done();
          });

          game_client.initQueue(client_queue);
          game_client.createGame();
        });
      });
    });
  });

  after(function() {
      game_server.removeGame(game_id);
  });

  it('should have a gameId', function(){
    assert(game_id);
  });
})
