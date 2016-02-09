var Player = function(id, xSpawn, ySpawn, orientation){
    this.id = id;
    this.x = xSpawn;
    this.y = ySpawn;
    this.orientation = orientation
}

Player.prototype = {
	resetForNewRound: function() {
	         
        }
}

module.exports = Player;
