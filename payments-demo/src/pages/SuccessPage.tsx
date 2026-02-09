import { useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { getPayment, type PaymentDetails } from "../lib/api";
import { clearLastPaymentId, getLastPaymentId, saveLastPaymentId } from "../lib/storage";

export default function SuccessPage() {
  const [searchParams] = useSearchParams();
  const paymentId = useMemo(() => {
    const fromUrl = searchParams.get("paymentId");
    if (fromUrl) {
      saveLastPaymentId(fromUrl);
      return fromUrl;
    }
    return getLastPaymentId();
  }, [searchParams]);
  const [payment, setPayment] = useState<PaymentDetails | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!paymentId) return;

    let cancelled = false;
    const poll = async () => {
      try {
        const p = await getPayment(paymentId);
        if (!cancelled) setPayment(p);
        if (p.status === "SUCCESS" || p.status === "FAILED") return;
      } catch (e) {
        if (!cancelled) setError(e instanceof Error ? e.message : "Failed to fetch payment");
        return;
      }
      setTimeout(poll, 1500);
    };

    poll();
    return () => {
      cancelled = true;
    };
  }, [paymentId]);

  return (
    <div className="page">
      <div className="card">
        <h1>Payment Result</h1>
        <p className="muted">Stripe redirected back. Checking DB status…</p>

        {!paymentId ? (
          <div className="error">
            Missing paymentId in browser storage. Start a new payment from the home page.
          </div>
        ) : null}

        {error ? <div className="error">{error}</div> : null}

        {payment ? (
          <div className="details">
            <div>
              <span className="k">Payment ID</span>
              <span className="v">{payment.paymentId}</span>
            </div>
            <div>
              <span className="k">Order ID</span>
              <span className="v">{payment.orderId}</span>
            </div>
            <div>
              <span className="k">Status</span>
              <span className={`badge ${payment.status.toLowerCase()}`}>{payment.status}</span>
            </div>
            <div>
              <span className="k">Amount</span>
              <span className="v">
                {payment.amount} {payment.currency.toUpperCase()}
              </span>
            </div>
            <div>
              <span className="k">Provider</span>
              <span className="v">{payment.provider}</span>
            </div>
            {payment.providerSessionId ? (
              <div>
                <span className="k">Provider Session</span>
                <span className="v">{payment.providerSessionId}</span>
              </div>
            ) : null}
            {payment.providerPaymentIntentId ? (
              <div>
                <span className="k">Payment Intent</span>
                <span className="v">{payment.providerPaymentIntentId}</span>
              </div>
            ) : null}
          </div>
        ) : paymentId ? (
          <div className="muted">Loading…</div>
        ) : null}

        <div className="row">
          <Link to="/" className="link">
            Start new payment
          </Link>
          <button
            className="secondary"
            onClick={() => {
              clearLastPaymentId();
              setPayment(null);
              setError(null);
            }}
          >
            Clear saved paymentId
          </button>
        </div>
      </div>
    </div>
  );
}
