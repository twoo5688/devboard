# 🚀 DevBoard Production Launch Guide

This guide provides step-by-step instructions for launching the DevBoard Kanban application in a production environment using Docker Compose.

## 📋 Prerequisites

Ensure the following are installed and available:

- **Docker** (20.10+) and **Docker Compose** (v2.x+)
- **Git** (for cloning the repository)
- **4GB RAM minimum** recommended for smooth operation
- **Port access**: 8080 (backend), 4200 (frontend), 5432 (PostgreSQL) available

### Verify Prerequisites

```bash
# Check Docker version
docker --version

# Check Docker Compose version
docker compose version

# Check Git
git --version
```

## 🔐 Step 1: Clone the Repository

```bash
# Clone the repository
git clone <your-repo-url>
cd devboard
```

## ⚙️ Step 2: Configure Environment Variables

### 2.1 Create a `.env` file

Copy the example `.env` file and customize it:

```bash
cp .env.example .env
```

### 2.2 Edit `.env` with your configuration

```bash
# Database Configuration
DB_NAME=devboard
DB_URL=jdbc:postgresql://db:5432/devboard
DB_USER=devboard_user
DB_PASSWORD=your_secure_db_password_here

# JWT Configuration (IMPORTANT: change this!)
JWT_SECRET=your_very_secure_jwt_secret_min_32_chars_long_here
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# CORS Configuration (for production, set to your frontend domain)
CORS_ALLOWED_ORIGINS=https://yourdomain.com,http://localhost:4200
```

### ⚠️ Security Important

- **Change the default JWT secret** in `.env` - use a 32+ character random string
- **Change the default database password** - don't use `devboard_pass` in production
- **Restrict CORS origins** - only allow your frontend domains
- **Keep `.env` secure** - never commit it to version control (already in `.gitignore`)

### Generate a Secure JWT Secret

```bash
# Linux/Mac
openssl rand -base64 32

# Or use this Python one-liner
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```

## 🐳 Step 3: Launch with Docker Compose

### 3.1 Build and start all services

```bash
# Build images and start containers in detached mode
docker compose up -d --build
```

This will:
- Build the Spring Boot backend application
- Build the Angular frontend
- Pull PostgreSQL 16 image
- Create a Docker network for service communication
- Start all three services (db, backend, frontend)

### 3.2 Monitor the startup process

```bash
# Watch container logs
docker compose logs -f
```

You should see:
1. **PostgreSQL** starts first (port 5432)
2. **Backend** waits for DB health check, then starts (port 8080)
3. **Frontend** starts last (port 4200)

### 3.3 Verify all services are running

```bash
# Check container status
docker compose ps
```

Expected output:
```
NAME                IMAGE               STATUS
devboard_db         postgres:16         Up (healthy)
devboard_backend    devboard-backend    Up
devboard_frontend   devboard-frontend   Up
```

## ✅ Step 4: Verify the Deployment

### 4.1 Check backend health

```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{"message":"DevBoard backend is running!","status":"UP"}
```

### 4.2 Access the frontend

Open your browser to:
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api/health
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### 4.3 Test the complete flow

1. Open http://localhost:4200 in your browser
2. Click **Register** and create a new user account
3. Log in with your credentials
4. Create a new project
5. Add tasks and test the Kanban drag-and-drop

## 🔍 Step 5: Check Application Logs

### View all logs

```bash
# All services
docker compose logs

# Backend only
docker compose logs backend

# Frontend only
docker compose logs frontend

# Database only
docker compose logs db

# Follow logs in real-time
docker compose logs -f backend
```

### Check for errors

```bash
# Look for ERROR or WARNING messages
docker compose logs | grep -i error
```

## 🔄 Step 6: Manage the Application

### Stop all services

```bash
# Stop but keep data
docker compose stop
```

### Start stopped services

```bash
# Restart stopped services
docker compose start
```

### Restart services

```bash
# Full restart
docker compose restart
```

### Stop and remove everything

```bash
# Stop and remove containers (keeps data)
docker compose down

# Stop, remove, and DELETE all data (DANGEROUS!)
docker compose down -v
```

### Update to latest version

```bash
# Pull latest code
git pull

# Rebuild and restart
docker compose up -d --build
```

### View resource usage

```bash
# Show resource stats
docker stats
```

## 💾 Backup and Restore

### Backup PostgreSQL data

Docker volumes are stored at:
- **Linux**: `/var/lib/docker/volumes/devboard_postgres_data/_data`
- **Mac**: `~/Library/Containers/com.docker.docker/Data/vms/0/data/docker/volumes/devboard_postgres_data/_data`
- **Windows**: `\wsl$\docker-desktop-data\version-pack-data\community\docker\volumes\devboard_postgres_data\_data`

### Manual database backup

```bash
# Create a backup
docker compose exec db pg_dump -U devboard_user devboard > backup.sql

# Restore from backup
cat backup.sql | docker compose exec -T db psql -U devboard_user -d devboard
```

## 🔒 Production Security Checklist

Before going live, ensure:

- [ ] JWT secret changed from default
- [ ] Database password changed from default
- [ ] CORS origins restricted to your domains only
- [ ] Database port (5432) not exposed to public internet
- [ ] HTTPS/TLS configured (use reverse proxy like Nginx)
- [ ] Firewall rules configured
- [ ] Regular backups scheduled
- [ ] Monitoring/logging in place
- [ ] `.env` file secured and not in version control

## 🚨 Troubleshooting

### Backend won't start

```bash
# Check database connectivity
docker compose exec db pg_isready -U devboard_user -d devboard

# View backend logs
docker compose logs backend
```

### Port already in use

```bash
# Check what's using port 5432
sudo lsof -i :5432

# Or change ports in docker-compose.yml
ports:
  - "5433:5432"  # Map host 5433 to container 5432
```

### Database connection errors

```bash
# Restart database service
docker compose restart db

# Check database logs
docker compose logs db

# Verify environment variables
cat .env
```

### Frontend can't connect to backend

```bash
# Check CORS settings in .env
CORS_ALLOWED_ORIGINS should include your frontend URL

# Restart all services
docker compose restart
```

### Out of memory errors

```bash
# Increase Docker memory allocation
# Docker Desktop: Settings -> Resources -> Memory (set to 4GB+)

# Or reduce Java heap size
# Add to backend Dockerfile or docker-compose.yml:
JAVA_OPTS: "-Xmx512m -Xms256m"
```

## 📊 Monitoring

### Health checks

Docker Compose includes health checks:
- PostgreSQL: Checks every 5 seconds
- Backend: Monitors JVM health

```bash
# View health status
docker compose ps
```

### Application metrics

The backend exposes:
- Health: `GET /api/health`
- Swagger API docs: `GET /v3/api-docs`
- Swagger UI: `GET /swagger-ui/index.html`

## 🔧 Advanced Configuration

### Custom database settings

Edit `docker-compose.yml`:

```yaml
environment:
  POSTGRES_DB: custom_db_name
  POSTGRES_USER: custom_user
  POSTGRES_PASSWORD: custom_password
```

### JVM tuning

Add to `docker-compose.yml` under backend environment:

```yaml
environment:
  JAVA_OPTS: "-Xmx1g -Xms512m -XX:+UseG1GC"
```

### Using external PostgreSQL

Modify `docker-compose.yml`:

```yaml
# Remove or comment out the db service
# db:
#   ...

# Update backend environment
environment:
  DB_URL: jdbc:postgresql://your-external-db:5432/devboard
  SPRING_DATASOURCE_URL: jdbc:postgresql://your-external-db:5432/devboard
```

## 📄 API Documentation

All endpoints are documented via Swagger UI at:
http://localhost:8080/swagger-ui/index.html

### Key Endpoints

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/projects` - List user's projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}/tasks` - List project tasks
- `POST /api/projects/{id}/tasks` - Create new task

## 🆘 Getting Help

If you encounter issues:

1. Check the logs: `docker compose logs`
2. Verify environment variables are correct
3. Ensure ports are available
4. Check the troubleshooting section above
5. Review the main README.md for architecture details

## 🌟 Next Steps

Once running, consider:

- Setting up HTTPS with Let's Encrypt
- Configuring a reverse proxy (Nginx, Traefik)
- Setting up automated backups
- Implementing monitoring (Prometheus, Grafana)
- Adding authentication (OAuth2, LDAP)
- Scaling horizontally with load balancing

## 📦 Project Structure

```
devboard/
├── backend/                 # Spring Boot API
├── frontend/                # Angular SPA
├── docker-compose.yml       # Service orchestration
├── .env                     # Environment variables
├── README.md                # Main documentation
└── README_PRODUCTION.md     # This file
```

---

**Ready to launch?** Follow these steps, and your DevBoard will be up and running in minutes! 🚀

For development instructions, see the main [README.md](README.md).
