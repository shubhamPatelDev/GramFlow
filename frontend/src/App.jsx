import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import LandingPage from "./pages/LandingPage";
import LoginPage from "./pages/LoginPage";
import DashboardLayout from "./pages/DashboardLayout";
import RulesView from "./pages/RulesView";
import BillingView from "./pages/BillingView";
import SupportView from "./pages/SupportView";
import InsightsView from "./pages/InsightsView";
import AccountsView from "./pages/AccountsView";
import SettingsView from "./pages/SettingsView";
import PrivacyPolicy from "./pages/PrivacyPolicy";
import TermsOfService from "./pages/TermsOfService";
import { Toaster } from "react-hot-toast";

function App() {
  return (
    <AuthProvider>
      <Toaster position="bottom-right" />
      <Router>
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/privacy-policy" element={<PrivacyPolicy />} />
          <Route path="/terms" element={<TermsOfService />} />
          <Route path="/dashboard" element={<DashboardLayout />}>
            <Route index element={<InsightsView />} />
            <Route path="rules" element={<RulesView />} />
            <Route path="billing" element={<BillingView />} />
            <Route path="support" element={<SupportView />} />
            <Route path="insights" element={<InsightsView />} />
            <Route path="accounts" element={<AccountsView />} />
            <Route path="profile" element={<SettingsView />} />
            <Route path="settings" element={<SettingsView />} />
            <Route path="*" element={<div className="p-8 text-foreground">Coming Soon</div>} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
