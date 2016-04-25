var GameManager = require('./server/controllers/gamemanager')
var RabbitMQWrapper = require('./server/controllers/rabbitmqwrapper');
var clock_exchange = "clock_exchange";

const readline = require('readline');
const rl = readline.createInterface(process.stdin, process.stdout);

console.log(process.argv);

function triggerUpdateWithCommandLine() {
    rl.on('line', function() {
        GameManager.updateClock()
    }).on('close', function() {
        console.log('Have a great day!');
        process.exit(0);
    });
}

function triggerUpdateWithTimer() {
    setInterval(GameManager.updateClock, 1000);
}

function start() {
    var triggerMethod = triggerUpdateWithTimer;

    if(process.argv.indexOf("--manual") >= 0) {
        console.log("Manual trigger, you must type enter in the command line to trigger one tick");
        triggerMethod = triggerUpdateWithCommandLine;
    }

    RabbitMQWrapper.initServerChannel("global_queue", function(){}, triggerMethod);
}

start();
