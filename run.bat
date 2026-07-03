@echo off
start "Backend" cmd /k "cd /d D:\LHWYAN\program\neu\Smart-Mushroom-Web-Terminal-Code\backend && java -jar target/smart-mushroom-backend-3.0.0.jar --server.port=8080"
timeout /t 5
start "Frontend" cmd /k "cd /d D:\LHWYAN\program\neu\Smart-Mushroom-Web-Terminal-Code\frontend && npx vite --host"