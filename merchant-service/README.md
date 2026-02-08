# Merchant Service

This service represents your “merchant app” entrypoint. It does **not** talk to Stripe directly.

It only:
- accepts checkout requests from UI/client
- calls `payment-integration-service` using Feign
- returns the `redirectUrl` (Stripe Checkout URL) back to the UI

## Runs On
- `http://localhost:8080`

## Main Endpoint

### `POST /checkout`
Creates a payment via integration service and returns a redirect URL.

Example:
```bash
curl -s -X POST http://localhost:8080/checkout \
  -H 'Content-Type: application/json' \
  -d '{"orderId":"ORD-1","amount":1000,"currency":"usd","paymentProvider":"STRIPE"}'
```

Response (example):
```json
{
  "paymentId": "uuid...",
  "redirectUrl": "https://checkout.stripe.com/...",
  "status": "PENDING"
}
```

## Config

`merchant-service/src/main/resources/application.yaml`
- `payment.integration.base-url` → defaults to `http://localhost:8081`

