#!/bin/bash

# PARSING SERVICE ******************
services=("long" "ngrok" "static")
service=$1
if [ -z "$service" ]; then
  echo "Service is empty. Choose '${services[*]}'"
  exit 1
fi
if [[ ! ${services[*]} =~ $service ]]; then
  echo "Incorrect service '$service'. Choose '${services[*]}'"
  exit 1
fi

# PARSING COMMAND ******************
commands=("start" "stop" "rm", "ps")
command=$2
if [ -z "$command" ]; then
  echo "Command is empty. Choose '${commands[*]}'"
  exit 1
fi
if [[ ! ${commands[*]} =~ $command ]]; then
  echo "Incorrect command '$command'. Choose '${commands[*]}'"
  exit 1
fi

# SETTING SERVICE
compose=""
# LONG service
if [ "$service" = "long" ]; then
  compose="long/docker-compose_long.yml"
fi
# NGROK service
if [ "$service" = "ngrok" ]; then
  compose="webhook/ngrok/docker-compose_ngrok.yml"
fi
# STATIC service
if [ "$service" = "static" ]; then
  compose="webhook/static/docker-compose_static.yml"
fi

# SETTING COMMAND
docker_command=""
if [ "$command" = "start" ]; then
  docker_command="up -d"
fi
if [ "$command" = "stop" ]; then
  docker_command="stop"
fi
if [ "$command" = "rm" ]; then
  docker_command="rm"
fi
if [ "$command" = "ps" ]; then
  docker_command="ps"
fi

echo "RUNNING docker compose -f $compose $docker_command"
docker compose -f $compose $docker_command

