@echo off
echo "Navegando para a pasta terraform..."
cd terraform

echo "Inicializando o Terraform..."
terraform init

echo "Aplicando as configuracoes do Terraform..."
terraform apply -auto-approve

echo "Configuracoes aplicadas com sucesso!"
pause
