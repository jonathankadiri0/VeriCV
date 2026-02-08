#!/bin/bash

# VeriCV Quick Start Script
# This script helps you get VeriCV up and running quickly

echo "🚀 VeriCV - Quick Start Script"
echo "================================"
echo ""

# Check if Java is installed
echo "Checking prerequisites..."
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java found: $(java -version 2>&1 | head -n 1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.9 or higher."
    exit 1
fi

echo "✅ Maven found: $(mvn -version | head -n 1)"

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "⚠️  Docker is not installed. You'll need to set up PostgreSQL manually."
    DOCKER_AVAILABLE=false
else
    echo "✅ Docker found"
    DOCKER_AVAILABLE=true
fi

echo ""
echo "================================"
echo "Starting VeriCV setup..."
echo "================================"
echo ""

# Start PostgreSQL with Docker if available
if [ "$DOCKER_AVAILABLE" = true ]; then
    echo "📦 Starting PostgreSQL with Docker..."
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        echo "✅ PostgreSQL started successfully"
        echo "⏳ Waiting for PostgreSQL to be ready..."
        sleep 5
    else
        echo "❌ Failed to start PostgreSQL"
        exit 1
    fi
else
    echo "⚠️  Please make sure PostgreSQL is running on localhost:5432"
    echo "   Database name: vericv_db"
    echo "   Username: postgres"
    echo "   Password: postgres"
    echo ""
    read -p "Press Enter when PostgreSQL is ready..."
fi

echo ""
echo "🔨 Building backend..."
cd backend

mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Backend built successfully"
else
    echo "❌ Failed to build backend"
    exit 1
fi

echo ""
echo "================================"
echo "✅ Setup Complete!"
echo "================================"
echo ""
echo "To start the application:"
echo "  cd backend"
echo "  mvn spring-boot:run"
echo ""
echo "Then open:"
echo "  🌐 API: http://localhost:8080"
echo "  📚 Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "Next steps:"
echo "  1. Read FIRST_WEEK_GUIDE.md for implementation steps"
echo "  2. Test authentication endpoints"
echo "  3. Start building CV features"
echo ""
echo "Good luck! 🎉"
