# Payment Integration Service

This is the **orchestrator** / **source of truth** service.

Responsibilities:
- generates `paymentId`
- persists the payment in DB
- routes to the selected provider (`STRIPE`)
- exposes read APIs to check payment status
- receives internal provider webhook updates and updates DB

## Runs On
- `http://localhost:8081`

## Public API

### `POST /payments`
Creates a payment and returns the provider redirect URL.

### `GET /payments/{paymentId}`
Returns the current payment status/details from DB.

### `GET /payments/recent`
Debug endpoint showing recent payments.

## Internal API (Protected)

All `/internal/**` endpoints require:
- header: `X-Internal-Api-Key: <INTERNAL_API_KEY>`

These endpoints are called by `stripe-provider-service` via Feign.

### `POST /internal/webhooks/stripe`
Receives webhook updates (event id + status) from Stripe Provider and updates the payment status idempotently.

## DB Tables (Important)

- `payments` → payment record + status + provider references
- `payment_webhook_events` → stores provider webhook event IDs for idempotency

## Config

`payment-integration-service/src/main/resources/application.yaml`
- `spring.datasource.*` → PostgreSQL connection
- `provider.stripe.base-url` → defaults to `http://localhost:8082`
- `internal.api.key` → defaults to `dev-internal-key` (override with `INTERNAL_API_KEY`)
