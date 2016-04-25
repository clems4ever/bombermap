var channel_tag = "ch";
var content_tag = "co";

var events = {};
var events_file = require("./events_data.json");
var keys = [];

exports.getEvents = function(time) {
        return events[time+""];
}

exports.createFireEvent = function(owner, dest, time) {
    
    var event_msg = {
        player_id : owner,
        lat : dest.lat,
        long : dest.long,
        time : time
    } 
    return {ch:"fire", co : event_msg};
}

exports.createMoveEvent = function(playerId, dest, time) {
    var event_msg = {
        player_id : playerId,
        lat : dest.lat,
        long : dest.long,
        time : time
    } 
    return {ch : "move", co : event_msg};    
}

exports.createDieEvent = function(playerId, time) {
    var event_msg = {
        player_id : playerId,
        time : time
    } 
    return {ch : "die", co : event_msg};        
}

exports.createRespawnEvent = function(playerId, time) {
    var event_msg = {
        player_id : playerId,
        time : time
    } 
    return {ch : "respawn", co : event_msg};        

}

exports.createShieldEvent = function(playerId, isOn, time) {
    var event_msg = {
        player_id : playerId,
        time : time
    } 
    return {ch : "shield", co : event_msg};        
}

exports.pushEvent = function(event) {
    if (event.co && event.co.time) {
        if (events[event.co.time+""] == null)
            events[event.co.time+""] = new Array();
        events[event.co.time+""].push(event);
    }
}

var init = function() {
    events_file.forEach(function (event) {
        exports.pushEvent(event);
    });
}
init();