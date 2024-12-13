#!/bin/bash

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Iniciando serviços com Docker Compose...${NC}"
sudo docker up -d

# Aguardar serviços estarem prontos
echo -e "${YELLOW}Aguardando serviços iniciarem...${NC}"
sleep 10

# Verificar status dos containers
check_container() {
    local container_name=$1
    if [ "$(sudo docker container inspect -f '{{.State.Health.Status}}' $container_name)" == "healthy" ]; then
        echo -e "${GREEN}✓ $container_name está rodando${NC}"
        return 0
    else
        echo -e "${RED}✗ $container_name não está saudável${NC}"
        return 1
    fi
}


# Verificar todos os containers
services_ready=true
for service in "pet_sistema_postgres" "pet_sistema_mongodb" "pet_sistema_redis"; do
    if ! check_container $service; then
        services_ready=false
    fi
done

if [ "$services_ready" = false ]; then
    echo -e "${RED}Erro: Alguns serviços não iniciaram corretamente${NC}"
    exit 1
fi

# Configurar variáveis de ambiente
echo -e "${YELLOW}Configurando variáveis de ambiente...${NC}"
export JWT_SECRET=chave-secreta-desenvolvimento
export MAIL_USERNAME=${MAIL_USERNAME:-"test@example.com"}
export MAIL_PASSWORD=${MAIL_PASSWORD:-"test123"}

# Compilar e iniciar a aplicação
echo -e "${YELLOW}Compilando a aplicação...${NC}"
./mvnw clean install -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${YELLOW}Iniciando a aplicação...${NC}"
    ./mvnw spring-boot:run &
    
    # Aguardar a aplicação iniciar
    echo -e "${YELLOW}Aguardando a aplicação iniciar...${NC}"
    while ! nc -z localhost 8080; do   
        sleep 1
    done
    
    echo -e "${GREEN}Aplicação iniciada com sucesso!${NC}"
    echo -e "${GREEN}API: http://localhost:8080/api/v1${NC}"
    echo -e "${GREEN}Swagger UI: http://localhost:8080/swagger-ui.html${NC}"
    echo -e "${GREEN}API Docs: http://localhost:8080/api-docs${NC}"
else
    echo -e "${RED}Erro na compilação da aplicação${NC}"
    exit 1
fi 