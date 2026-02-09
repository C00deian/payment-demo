import { Link } from "react-router-dom";
import { getLastPaymentId } from "../lib/storage";

export default function CancelPage() {
  const paymentId = getLastPaymentId();

  return (
    <div className="page">
      <div className="card">
        <h1>Payment Canceled</h1>
        <p className="muted">Stripe canceled the checkout flow.</p>
        {paymentId ? <p className="muted small">Last paymentId: {paymentId}</p> : null}
        <Link to="/" className="link">
          Try again
        </Link>
      </div>
    </div>
  );
}

