# VeriCV - Verified Professionals Directory

## Overview

VeriCV is a full-stack web application that functions as a searchable directory of verified professionals. Inspired by the concept of a traditional phone book, VeriCV allows users to register, build a profile containing their education and work experience, and appear in a publicly searchable directory.

## Live Demo

https://vericv-app-d701eba5cd19.herokuapp.com/

## Technology Stack

- **Backend:** Spring Boot 3.2, Java 17
- **Database:** PostgreSQL 15
- **Frontend:** React, Tailwind CSS
- **Authentication:** JWT (RFC 7519), BCrypt
- **Containerisation:** Docker
- **Deployment:** Heroku

## Features

- User registration and login with JWT authentication
- CV creation with structured education and experience entries
- Public directory search by keyword (no login required)
- Public profile viewing with view counters
- Tiered verification badge system (None, Bronze, Silver, Gold, Platinum)
- Profile visibility controls

## Running Locally

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker
- Node.js 18+

### Setup

1. Clone the repository
   git clone https://github.com/jonathankadiri0/VeriCV.git
   cd VeriCV

2. Start the database
   docker-compose up -d

3. Start the backend
   cd backend
   ./mvnw spring-boot:run

4. Start the frontend
   cd frontend
   npm install
   npm run dev

5. Open http://localhost:5173 in your browser

## Author

Jonathan Kadiri - Final Year Project, Maynooth University
Supervisor: Dr. Keith Maycock
