# Payments Demo UI (React)

A minimal React UI to test the end-to-end payment flow.

## Runs On
- `http://localhost:5173`

## What It Does

1. Calls Merchant API:
   - `POST http://localhost:8080/checkout`
2. Redirects the browser to Stripe Checkout (using the `redirectUrl`)
3. After Stripe finishes, Stripe redirects back to:
   - `http://localhost:5173/success?paymentId=...`
4. The UI polls Integration API until the payment is final:
   - `GET http://localhost:8081/payments/{paymentId}`

## Setup

Create an env file:
```bash
cp .env.example .env
```

Install & run:
```bash
npm install
npm run dev
```

## Env Vars

In `.env`:
- `VITE_MERCHANT_BASE_URL` (default: `http://localhost:8080`)
- `VITE_INTEGRATION_BASE_URL` (default: `http://localhost:8081`)

## Notes

- The final status comes from DB (webhook-driven), not from the redirect.
- If you clear browser storage, the UI can still read `paymentId` from the `/success` URL.

