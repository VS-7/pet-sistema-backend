#!/bin/bash

# Definir URL base da API
BASE_URL="http://localhost:8080/api/v1"

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# Registrar um PETIANO
echo -e "${GREEN}Registrando um PETIANO...${NC}"
curl -X POST "${BASE_URL}/auth/registrar" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@example.com",
    "senha": "senha123",
    "tipo": "PETIANO"
  }' | json_pp

echo -e "\n"

# Registrar um TUTOR
echo -e "${GREEN}Registrando um TUTOR...${NC}"
curl -X POST "${BASE_URL}/auth/registrar" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria.santos@example.com",
    "senha": "senha123",
    "tipo": "TUTOR"
  }' | json_pp

# Login com usuário registrado
echo -e "\n${GREEN}Fazendo login com usuário registrado...${NC}"
curl -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao.silva@example.com",
    "senha": "senha123"
  }' | json_pp 