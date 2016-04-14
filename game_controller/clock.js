var GameManager = require('./server/controllers/gamemanager')
var RabbitMQWrapper = require('./server/controllers/rabbitmqwrapper');
var clock_exchange = "clock_exchange";

function start() {
    RabbitMQWrapper.initServerChannel("global_queue", function(){}, function() {
        setInterval(GameManager.updateClock, 1000);
    });
}

start();
