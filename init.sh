#!/bin/bash

# --- Formatting ---
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "🚀 Starting Agentic Environment Validation..."

# Function to check tool status
check_tool() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}✅ $1 is installed ($($2))${NC}"
    else
        echo -e "${RED}❌ $1 is NOT found.${NC}"
        exit 1
    fi
}

# 1. Check Git
check_tool "git" "git --version"

# 2. Check Java & Maven
check_tool "java" "java -version 2>&1 | head -n 1"
check_tool "mvn" "mvn -v | head -n 1"

# 3. Check Node & Angular
check_tool "node" "node -v"
check_tool "ng" "ng version | grep 'Angular CLI' | head -n 1"

# 4. Check Docker
if docker info &> /dev/null; then
    echo -e "${GREEN}✅ Docker is running${NC}"
else
    echo -e "${RED}❌ Docker is NOT running or accessible.${NC}"
fi

echo "---"
echo "🛠️ Environment ready."