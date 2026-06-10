import { UserCircle, Mail, LogOut, ShieldCheck } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { useAuth } from "@/contexts/AuthContext";
import { useNavigate, useOutletContext } from "react-router-dom";

export default function SettingsView() {
  const { user: profile } = useOutletContext();
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };


  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-200 max-w-4xl mx-auto">
      <div>
        <h2 className="text-3xl font-headline font-bold text-foreground">Settings & Profile</h2>
        <p className="text-muted-foreground mt-1 text-lg">Manage your account preferences and view your details.</p>
      </div>

      <div className="grid md:grid-cols-2 gap-8">
        <Card className="glass-card border-primary/20">
          <CardHeader>
            <CardTitle className="text-2xl font-headline flex items-center gap-2">
              <UserCircle className="text-primary" /> Profile Details
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="space-y-4">
              <div className="flex items-center gap-3 bg-secondary/30 p-4 rounded-xl border border-white/5">
                <Mail className="text-muted-foreground" size={20} />
                <div>
                  <p className="text-xs text-muted-foreground font-bold uppercase tracking-widest">Email Address</p>
                  <p className="font-medium text-foreground">{profile?.email}</p>
                </div>
              </div>
              <div className="flex items-center gap-3 bg-secondary/30 p-4 rounded-xl border border-white/5">
                <ShieldCheck className="text-muted-foreground" size={20} />
                <div>
                  <p className="text-xs text-muted-foreground font-bold uppercase tracking-widest">Subscription Tier</p>
                  <p className="font-medium text-primary font-bold">{profile?.subscriptionTier}</p>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="glass-card border-destructive/20 bg-destructive/5">
          <CardHeader>
            <CardTitle className="text-2xl font-headline text-destructive flex items-center gap-2">
              Danger Zone
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-6">
            <p className="text-sm text-muted-foreground">Sign out of your account on this device.</p>
            <button 
              onClick={handleLogout}
              className="flex items-center gap-2 px-4 py-2 bg-destructive/10 text-destructive hover:bg-destructive hover:text-white rounded-lg transition-colors font-bold"
            >
              <LogOut size={16} /> Sign Out
            </button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
