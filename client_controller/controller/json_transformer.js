var json = require("./events_data4.json");
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
        
            if (elem.co.killer_id) {
                index = playersId.indexOf(elem.co.player_id);
                elem.co.killer_id = newPlayersId[index];
            }
        }
        
        if(elem.ch === "player_join" && elem.co && !elem.co.time) {
           elem.co.time = 3000; 
        }
        
        if(elem.ch === "move" && elem.co && !elem.co.time) {
           elem.co.time = 5000; 
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