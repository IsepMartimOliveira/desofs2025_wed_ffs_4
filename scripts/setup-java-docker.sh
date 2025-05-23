#!/bin/bash
set -e

echo "Updating package lists..."
sudo apt-get update -y

echo "Installing prerequisites..."
sudo apt-get install -y ca-certificates curl gnupg lsb-release software-properties-common

echo "Installing OpenJDK 17..."
sudo apt-get install -y openjdk-17-jdk

echo "Verifying Java installation..."
java -version

echo "Installing Docker..."

# Add Docker’s official GPG key
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Set up Docker repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

echo "Enabling Docker service..."
sudo systemctl enable docker
sudo systemctl start docker

echo "Verifying Docker installation..."
docker --version

echo "Installation complete!"
