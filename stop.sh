#!/bin/bash

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Parando a aplicação Spring Boot...${NC}"
if pgrep -f "spring-boot:run" > /dev/null; then
    pkill -f "spring-boot:run"
    echo -e "${GREEN}Aplicação Spring Boot parada${NC}"
else
    echo -e "${YELLOW}Aplicação Spring Boot não está rodando${NC}"
fi

echo -e "${YELLOW}Parando containers Docker...${NC}"
docker-compose down
echo -e "${GREEN}Containers parados e removidos${NC}" 