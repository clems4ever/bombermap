
var PUBLIC_PORT = 9000

var express = require('express');
var app = express();

var mongoose = require('mongoose');
mongoose.connect('mongodb://mongodb/bombermap_website');
var db = mongoose.connection;

var bodyParser = require('body-parser')
app.use(bodyParser.json());         // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
    extended: true
})); 




var betaTesterSchema = mongoose.Schema({
    email: String,
    insertionTimestamp: Date
});

var BetaTester = mongoose.model('BetaTester', betaTesterSchema);



function connectMongoDB() {
    console.log("Connecting to MongoDB...");
	db.on('error', console.error.bind(console, 'connection error:'));
	db.once('open', function() {
	    console.log("Connected to MongoDB"); 
        webserverStartsListening();
	});
}


function webserverStartsListening() {
    var server = app.listen(PUBLIC_PORT, function () {
    
      var host = server.address().address
      var port = server.address().port
    
      console.log("BomberMap REST API listening at http://%s:%s", host, port)

      // Setup the routes
      setupRoutes();
    })
}

function setupRoutes() {
    app.post('/subscribe', function (req, res) {
        var req_email = req.body.email;
        console.log("Insert " + req_email + " email in the DB");
        betaTester = new BetaTester({ email: req_email, insertionTimestamp: new Date()});
        betaTester.save(function (err) {
            if (err) {
                console.log(err);
                res.send('FAILURE');
            } else {
                console.log('Beta Tester registered successfully');
                res.send('SUCCESS');
            }
        });
    });
}


function start() {
    connectMongoDB();
}


start();
