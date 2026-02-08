# Stripe Provider Service

This service is the Stripe-specific provider implementation.

Responsibilities:
- creates a Stripe Checkout Session
- receives Stripe webhooks and verifies `Stripe-Signature`
- converts Stripe event → internal webhook update → calls `payment-integration-service`

## Runs On
- `http://localhost:8082`

## Endpoints

### `POST /stripe/payments`
Called by `payment-integration-service` to create a Stripe Checkout URL.

### `POST /webhooks/stripe`
Stripe webhook receiver. Must be reachable by Stripe (use Stripe CLI for local dev).

## Local Webhook Setup (Stripe CLI)

```bash
stripe listen --forward-to http://localhost:8082/webhooks/stripe
```

Set the webhook secret:
```bash
export STRIPE_WEBHOOK_SECRET=whsec_...
```

## Config

`stripe-provider-service/src/main/resources/application.yaml`
- `stripe.secret.key` → set via `STRIPE_SECRET_KEY`
- `stripe.webhook.secret` → set via `STRIPE_WEBHOOK_SECRET`
- `payment.integration.base-url` → defaults to `http://localhost:8081`
- `internal.api.key` → shared internal key (`INTERNAL_API_KEY`)

