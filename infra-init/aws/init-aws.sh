#!/bin/sh
echo "Initializing LocalStack services..."

# 1. Crear cola SQS para eventos de usuario
awslocal sqs create-queue --queue-name user-created-events

# 2. Crear tabla en DynamoDB para guardar datos en mayúsculas
awslocal dynamodb create-table \
    --table-name users-uppercase \
    --key-schema AttributeName=id,KeyType=HASH \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --billing-mode PAY_PER_REQUEST

echo "LocalStack initialization complete!"
