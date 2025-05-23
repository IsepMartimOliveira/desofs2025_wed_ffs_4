#!/bin/bash

set -e

echo " Checking if Java 17 is already installed..."
if java -version 2>&1 | grep -q '17'; then
    echo " Java 17 is already installed. Skipping Java installation."
else
    echo " Installing OpenJDK 17..."
    sudo apt-get update
    sudo apt-get install -y openjdk-17-jdk
    echo " Java 17 installed."
fi

echo "Checking if Docker is already installed..."
if command -v docker >/dev/null 2>&1; then
    echo " Docker is already installed. Skipping Docker installation."
else
    echo " Installing Docker..."

    echo "Installing prerequisites..."
    sudo apt-get install -y \
        ca-certificates \
        curl \
        gnupg \
        lsb-release \
        software-properties-common

    echo "Adding Docker’s GPG key (non-interactive)..."
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
        gpg --dearmor --no-tty | sudo tee /usr/share/keyrings/docker-archive-keyring.gpg > /dev/null

    echo "Setting up Docker repository..."
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
      https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable" | \
      sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    echo "Installing Docker..."
    sudo apt-get update
    sudo apt-get install -y \
        docker-ce \
        docker-ce-cli \
        containerd.io \
        docker-buildx-plugin \
        docker-compose-plugin

    echo "✅ Docker installed."
fi

echo " Verifying installations..."
java -version
docker --version
docker compose version || true

echo " Java and Docker setup completed successfully."
