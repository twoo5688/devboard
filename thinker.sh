#!/bin/bash

# Load keys from .env if it exists
if [ -f .env ]; then
    export $(grep -v '^#' .env | xargs)
else
    echo "Error: .env file not found. Create one with your GROQ_API_KEY."
    exit 1
fi

# Configuration
ENGINE="groq" # Switch to "cerebras" if you hit limits

if [ "$ENGINE" == "groq" ]; then
    echo "Starting Groq Thinker (Llama 3.3 70B)..."
    litellm --model groq/llama-3.3-70b-versatile --port 8000 --drop_params
else
    echo "Starting Cerebras Thinker (Qwen 3 235B)..."
    litellm --model cerebras/qwen-3-235b --port 8000
fi