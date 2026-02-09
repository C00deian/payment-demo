import { INTEGRATION_BASE_URL, MERCHANT_BASE_URL } from "./config";

export type CheckoutRequest = {
  orderId: string;
  amount: number;
  currency: string;
  paymentProvider: "STRIPE";
};

export type CheckoutResponse = {
  paymentId: string;
  redirectUrl: string;
  status: string;
};

export type PaymentDetails = {
  paymentId: string;
  orderId: string;
  amount: number;
  currency: string;
  provider: string;
  providerSessionId?: string;
  providerPaymentIntentId?: string;
  status: "CREATED" | "INITIATED" | "PENDING" | "SUCCESS" | "FAILED";
};

async function readJson<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const text = await response.text().catch(() => "");
    throw new Error(`${response.status} ${response.statusText}${text ? ` - ${text}` : ""}`);
  }
  return (await response.json()) as T;
}

export async function createCheckout(request: CheckoutRequest): Promise<CheckoutResponse> {
  const response = await fetch(`${MERCHANT_BASE_URL}/checkout`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request)
  });
  return readJson<CheckoutResponse>(response);
}

export async function getPayment(paymentId: string): Promise<PaymentDetails> {
  const response = await fetch(`${INTEGRATION_BASE_URL}/payments/${encodeURIComponent(paymentId)}`, {
    method: "GET"
  });
  return readJson<PaymentDetails>(response);
}
