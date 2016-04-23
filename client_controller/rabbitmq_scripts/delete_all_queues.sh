sudo rabbitmqctl list_queues | awk '{print $1}' | xargs -I qn python rabbitmqadmin delete queue name=qn
