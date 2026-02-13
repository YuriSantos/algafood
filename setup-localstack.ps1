# Script para configurar o LocalStack (AWS Local)
# Execute este script após iniciar o docker-compose

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Setup LocalStack - AlgaFood" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Verificar se o Docker está rodando
Write-Host "`n[1/5] Verificando Docker..." -ForegroundColor Yellow
$dockerRunning = docker info 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}
Write-Host "Docker OK!" -ForegroundColor Green

# 2. Iniciar containers
Write-Host "`n[2/5] Iniciando LocalStack via Docker Compose..." -ForegroundColor Yellow
docker-compose up -d localstack algafood-mysql algafood-redis
Start-Sleep -Seconds 10

# 3. Aguardar LocalStack estar pronto
Write-Host "`n[3/5] Aguardando LocalStack estar pronto..." -ForegroundColor Yellow
$maxRetries = 30
$retry = 0
do {
    Start-Sleep -Seconds 2
    $retry++
    $status = docker exec algafood-localstack-1 curl -s http://localhost:4566/_localstack/health 2>$null
    Write-Host "  Tentativa $retry de $maxRetries..."
} while ($status -notmatch '"sqs": "running"' -and $retry -lt $maxRetries)

if ($retry -eq $maxRetries) {
    Write-Host "ERRO: LocalStack não iniciou corretamente." -ForegroundColor Red
    exit 1
}
Write-Host "LocalStack OK!" -ForegroundColor Green

# 4. Aplicar Terraform
Write-Host "`n[4/5] Aplicando configurações Terraform..." -ForegroundColor Yellow
Push-Location terraform
terraform init
terraform apply -auto-approve
Pop-Location
Write-Host "Terraform aplicado!" -ForegroundColor Green

# 5. Verificar recursos criados
Write-Host "`n[5/5] Verificando recursos criados..." -ForegroundColor Yellow

Write-Host "`n  Filas SQS:" -ForegroundColor Cyan
aws --endpoint-url=http://localhost:4566 sqs list-queues --region us-east-1

Write-Host "`n  Event Buses:" -ForegroundColor Cyan
aws --endpoint-url=http://localhost:4566 events list-event-buses --region us-east-1

Write-Host "`n  Identidades SES:" -ForegroundColor Cyan
aws --endpoint-url=http://localhost:4566 ses list-identities --region us-east-1

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " LocalStack configurado com sucesso!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nEndpoint AWS Local: http://localhost:4566"
Write-Host "Fila SQS: algafood-pedido-status"
Write-Host "Event Bus: algafood-event-bus"
Write-Host "`nPara iniciar a aplicação, rode:"
Write-Host "  .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow

