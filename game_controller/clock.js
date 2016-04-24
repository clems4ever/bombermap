var GameManager = require('./server/controllers/gamemanager')
var RabbitMQWrapper = require('./server/controllers/rabbitmqwrapper');
var clock_exchange = "clock_exchange";

const readline = require('readline');
const rl = readline.createInterface(process.stdin, process.stdout);

function start() {
    RabbitMQWrapper.initServerChannel("global_queue", function(){}, function() {
	rl.on('line', function() {
	  GameManager.updateClock()
	}).on('close', function() {
	  console.log('Have a great day!');
	  process.exit(0);
	});
    });
}

start();
