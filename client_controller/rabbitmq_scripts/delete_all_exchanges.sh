sudo rabbitmqctl list_exchanges | grep game_room | awk '{print $1}' | xargs -I qn python rabbitmqadmin delete exchange name=qn
