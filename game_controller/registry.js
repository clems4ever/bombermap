var uuid = require('node-uuid');
var GcmRegistryModel = require('./server/model/gcmregistry')
var RabbitMQWrapper = require('./server/controllers/rabbitmqwrapper')
var registry_queue = 'registry';

//connect to mongo gloabally
var mongodb = require('./server/controllers/mongooseconnect')

function consumeCallback(msg) {
    var content = JSON.parse(msg.content.toString());

    if (content.action == "search") {
        if (content.nickname) {
    	    console.log("searching : "+content.nickname);
            GcmRegistryModel.getRegIdByNickname(nickname, function(reg_id) {
                var client_queue = msg.properties.replyTo;
                var msgToSend = JSON.stringify({"reg_id":reg_id.reg_id, "nickname": reg_id.nickname, "email": reg_id.email});
                console.log("found : "+reg_id.nickname +" "+reg_id.email+" "+reg_id.reg_id);
                RabbitMQWrapper.replyToClient(client_queue, msg, msgToSend);                
            });
        }
    }
    else if (content.action == "register") {
        console.log("register : "+content.nickname +" "+content.email+" "+content.reg_id);
        if (content.email && content.reg_id) {
            var reg_id = {'email':content.email, 'reg_id':content.reg_id, 'nickname':content.nickname}
    	    GcmRegistryModel.addRegId(reg_id, function(reg_id) {
                console.log("successfully registered");
                var client_queue = msg.properties.replyTo;
                var msgToSend = JSON.stringify({"reg_id":content.reg_id, "nickname": content.nickname, "email": content.email});
                RabbitMQWrapper.replyToClient(client_queue, msg, msgToSend);    
            });    
        }
    }
    else if (content.action == "unregister") {
        if (content.email) {
    	    GcmRegistryModel.removeRegIdByEmail(content.email, function() {
                var client_queue = msg.properties.replyTo;
                var msgToSend = JSON.stringify({"email": content.email});
                RabbitMQWrapper.replyToClient(client_queue, msg, msgToSend);                
            });
        }
    }
}

function start() {
    RabbitMQWrapper.initServerChannel(registry_queue, consumeCallback, function () {});
}

function testByEmail() {
    var email = "email@gmail.com";
    var nickname = "serge"
    var reg_id = "xxxxx";
    console.log("test by email")
    GcmRegistryModel.removeRegIdByEmail(email, function()  {
        GcmRegistryModel.addRegId({"email": email, 'nickname':nickname, "reg_id": reg_id}, function()  {
            GcmRegistryModel.getRegIdByEmail(email, function(reg_id) {
                console.log(reg_id.email+" "+reg_id.reg_id);
                GcmRegistryModel.removeRegIdByEmail(reg_id.email, function(err) {
                    console.log("entry removed");
                    testByNickname();
                }); 
            });
        });
    });    
}

function testByNickname() {
    var email = "email@gmail.com";
    var nickname = "serge"
    var reg_id = "xxxxx";
    console.log("test by nickname")
    GcmRegistryModel.addRegId({"email": email, 'nickname':nickname, "reg_id": reg_id}, function()  {
        GcmRegistryModel.getRegIdByNickname(nickname, function(reg_id) {
            console.log(reg_id.email+" "+reg_id.reg_id+" "+reg_id.nickname);
            GcmRegistryModel.removeRegIdByEmail(reg_id.email, function(err) {
                console.log("entry removed");
            }); 
        });
    });    
}

//testByEmail();

start();
