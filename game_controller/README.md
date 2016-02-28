=== WarGame ===

Game controller server API :

create a game :
{
 'action' : 'newgame'
}

join a game :
{
 'action':'join', 
 'game_id':game_id}

leave a game :
{
 'action':'leave',
 'game_id':game_id,
 'player_id':player_id
}

remove a game (removes only database entries associated with the game, 
as the queues and exchanges autodelete):
{
 'action':'remove'
 'game_id':game_id
}
