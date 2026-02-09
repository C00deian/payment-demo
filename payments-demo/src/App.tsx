import { Navigate, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import SuccessPage from "./pages/SuccessPage";
import CancelPage from "./pages/CancelPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/success" element={<SuccessPage />} />
      <Route path="/cancel" element={<CancelPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

