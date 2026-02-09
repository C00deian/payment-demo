const PAYMENT_ID_KEY = "payments-demo:lastPaymentId";

export function saveLastPaymentId(paymentId: string) {
  const trimmed = paymentId?.trim?.() ?? "";
  if (!trimmed || trimmed === "undefined" || trimmed === "null") return;
  localStorage.setItem(PAYMENT_ID_KEY, trimmed);
}

export function getLastPaymentId(): string | null {
  const value = localStorage.getItem(PAYMENT_ID_KEY);
  if (!value) return null;
  const trimmed = value.trim();
  if (!trimmed || trimmed === "undefined" || trimmed === "null") return null;
  return trimmed;
}

export function clearLastPaymentId() {
  localStorage.removeItem(PAYMENT_ID_KEY);
}
