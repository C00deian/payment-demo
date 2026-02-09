import { useMemo, useState } from "react";
import { createCheckout } from "../lib/api";
import { saveLastPaymentId } from "../lib/storage";

export default function HomePage() {
  const [orderId, setOrderId] = useState(() => `ORD-${Date.now()}`);
  const [amount, setAmount] = useState<number>(1000);
  const [currency, setCurrency] = useState("usd");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const amountHelp = useMemo(() => {
    return "Amount in smallest unit (cents/paise). Example: $10.00 => 1000";
  }, []);

  async function onPay() {
    setError(null);
    setBusy(true);
    try {
      const response = await createCheckout({
        orderId,
        amount,
        currency,
        paymentProvider: "STRIPE"
      });
      saveLastPaymentId(response.paymentId);
      window.location.assign(response.redirectUrl);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Checkout failed");
      setBusy(false);
    }
  }

  return (
    <div className="page">
      <div className="card">
        <h1>Payments Demo</h1>
        <p className="muted">Merchant Service → Payment Integration → Stripe</p>

        <div className="grid">
          <label>
            Order ID
            <input value={orderId} onChange={(e) => setOrderId(e.target.value)} />
          </label>

          <label>
            Currency
            <select value={currency} onChange={(e) => setCurrency(e.target.value)}>
              <option value="usd">USD</option>
              <option value="inr">INR</option>
              <option value="eur">EUR</option>
            </select>
          </label>

          <label>
            Amount
            <input
              type="number"
              min={1}
              value={amount}
              onChange={(e) => setAmount(Number(e.target.value))}
            />
            <div className="hint">{amountHelp}</div>
          </label>
        </div>

        {error ? <div className="error">{error}</div> : null}

        <button className="primary" disabled={busy} onClick={onPay}>
          {busy ? "Redirecting…" : "Pay with Stripe"}
        </button>

        <div className="muted small">
          Test card: 4242 4242 4242 4242 · Any future expiry · Any CVC
        </div>
      </div>
    </div>
  );
}

