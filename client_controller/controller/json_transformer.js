var json = require("./events_data.json");
var fs = require("fs");

exports.transformRawJson = function(array) {
    var result = [];
    var playersId = [];
    var newPlayersId = ["Jose", "Elvis", "Clem", "Serg"]
    array.forEach(function(elem) {    
        if(elem.co && elem.co.player_id) {
            var index = playersId.indexOf(elem.co.player_id); 
            if (index == -1)
                playersId.push(elem.co.player_id);
            else
                elem.co.player_id = newPlayersId[index];
        }    
    });
    
    console.log(JSON.stringify(array));
    fs.appendFile("/tmp/test", JSON.stringify(JSON.stringify(array)), function(err) {
            if(err) {
                return console.log(err);
            }

            console.log("json saved to file");
    });     
}

exports.transformRawJson(json);