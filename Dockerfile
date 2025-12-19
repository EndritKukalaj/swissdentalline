# Use Java 21 LTS for stability
FROM eclipse-temurin:21-jdk-jammy

# Install Node.js 20 LTS and supervisor for process management
RUN apt-get update && apt-get install -y supervisor curl \
    && curl -sL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app

# Copy Maven wrapper and project files
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
COPY src src

# Copy frontend directory
COPY frontend frontend

# Build frontend (npm install instead of npm ci to avoid lockfile issues)
RUN cd frontend && npm install && npm run build

# Make mvnw executable and build backend
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw package -DskipTests

# Create supervisor configuration
RUN cat > /etc/supervisor/conf.d/supervisord.conf <<'EOF'
[supervisord]
nodaemon=true
user=root

[program:backend]
command=java -jar /usr/src/app/target/swissdentalline-0.0.1-SNAPSHOT.jar
directory=/usr/src/app
autostart=true
autorestart=true
stdout_logfile=/dev/stdout
stdout_logfile_maxbytes=0
stderr_logfile=/dev/stderr
stderr_logfile_maxbytes=0

[program:frontend]
directory=/usr/src/app/frontend
command=node build
autostart=true
autorestart=true
stdout_logfile=/dev/stdout
stdout_logfile_maxbytes=0
stderr_logfile=/dev/stderr
stderr_logfile_maxbytes=0
EOF

# Expose port 3000 for frontend (backend runs on 8080 internally)
EXPOSE 3000
ENV NODE_ENV=production
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]