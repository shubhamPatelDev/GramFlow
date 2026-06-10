import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Zap, Check, Loader2 } from "lucide-react";
import { authAPI, paymentsAPI } from "@/lib/api";
import { useOutletContext } from "react-router-dom";

export default function BillingView() {
  const { user: profile } = useOutletContext();
  const [upgrading, setUpgrading] = useState(false);

  const handleCheckout = async () => {
    setUpgrading(true);
    try {
      const sub = await paymentsAPI.createSubscription();

      const options = {
        key: import.meta.env.VITE_RAZORPAY_KEY_ID,
        name: "Commentor",
        description: "Upgrade to Pro Monthly",
        subscription_id: sub.subscriptionId,
        handler: async function (response) {
          try {
            await paymentsAPI.verifySubscription({
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySubscriptionId: response.razorpay_subscription_id,
              razorpaySignature: response.razorpay_signature
            });
            alert("Successfully subscribed to PRO!");
            window.location.reload(); // Refresh to get new user state
          } catch (verifyErr) {
            alert("Subscription verification failed");
          }
        },
        prefill: {
          email: profile?.email || "",
          name: profile?.name || ""
        },
        theme: {
          color: "#4f46e5"
        }
      };

      const rzp = new window.Razorpay(options);
      rzp.on('payment.failed', function (response){
        alert("Payment Failed: " + response.error.description);
      });
      rzp.open();

    } catch (e) {
      alert("Failed to initiate checkout");
    } finally {
      setUpgrading(false);
    }
  };

  const isPro = profile?.subscriptionTier === "PAID";
  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-200 max-w-4xl mx-auto">
      <div>
        <h2 className="text-3xl font-headline font-bold text-foreground">Subscription</h2>
        <p className="text-muted-foreground mt-1 text-lg">Manage your billing and plan.</p>
      </div>
      
      <div className="grid md:grid-cols-2 gap-8">
        <Card className={`glass-card border-white/5 opacity-80 ${!isPro ? "ring-2 ring-primary/50" : ""}`}>
          <CardHeader>
            <CardTitle className="text-2xl font-headline">Free Plan</CardTitle>
            <CardDescription>Perfect for testing the waters.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <p className="text-4xl font-bold font-headline">₹0 <span className="text-sm text-muted-foreground">/mo</span></p>
            <ul className="space-y-3">
              <li className="flex gap-2 items-center text-sm"><Check size={16} className="text-primary"/> 1 Connected Account</li>
              <li className="flex gap-2 items-center text-sm"><Check size={16} className="text-primary"/> 1 Automation Rule</li>
              <li className="flex gap-2 items-center text-sm"><Check size={16} className="text-primary"/> Up to 50 Auto-Replies/mo</li>
            </ul>
            <Button variant="outline" className="w-full pointer-events-none" disabled>
              {!isPro ? "Current Plan" : "Free Plan"}
            </Button>
          </CardContent>
        </Card>

        <Card className={`glass-card border-primary/20 relative overflow-hidden ${isPro ? "ring-2 ring-primary glow-indigo" : ""}`}>
          <div className="absolute top-0 right-0 bg-primary text-primary-foreground text-[10px] font-bold uppercase tracking-widest px-3 py-1 rounded-bl-lg">
            Recommended
          </div>
          <CardHeader>
            <CardTitle className="text-2xl font-headline flex items-center gap-2">
              <Zap className="text-primary" /> Pro Plan
            </CardTitle>
            <CardDescription>Scale your Instagram engagement.</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <p className="text-4xl font-bold font-headline">₹299 <span className="text-sm text-muted-foreground">/mo</span></p>
            <ul className="space-y-3">
              <li className="flex gap-2 items-center text-sm"><Check size={16} className="text-primary"/> Unlimited Accounts</li>
              <li className="flex gap-2 items-center text-sm"><Check size={16} className="text-primary"/> Unlimited Automation Rules</li>
              <li className="flex gap-2 items-center text-sm"><Check size={16} className="text-primary"/> Priority Email Support</li>
            </ul>
            {isPro ? (
              <div className="space-y-4">
                <Button className="w-full bg-primary/20 text-primary pointer-events-none font-bold" disabled>
                  Active Plan
                </Button>
                {profile?.subscriptionStatus && (
                  <p className="text-center text-sm text-muted-foreground">
                    Status: <span className="capitalize">{profile.subscriptionStatus}</span>
                  </p>
                )}
              </div>
            ) : (
              <Button 
                className="w-full bg-primary hover:bg-primary/90 glow-indigo text-white font-bold" 
                onClick={handleCheckout}
                disabled={upgrading}
              >
                {upgrading ? "Upgrading..." : "Upgrade to Pro"}
              </Button>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
