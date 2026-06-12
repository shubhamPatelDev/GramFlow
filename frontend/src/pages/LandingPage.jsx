import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { Zap, Camera as Instagram, BarChart3, ShieldCheck, ArrowRight, MessageSquare, Target } from "lucide-react";

export default function LandingPage() {
  return (
    <div className="flex flex-col min-h-screen bg-background">
      {/* Navigation */}
      <nav className="h-20 border-b border-white/5 flex items-center px-6 md:px-12 bg-background/50 backdrop-blur-md sticky top-0 z-50">
        <div className="flex items-center gap-2">
          <div className="w-10 h-10 rounded-xl bg-primary flex items-center justify-center glow-indigo">
            <Zap className="text-primary-foreground fill-current" size={24} />
          </div>
          <span className="text-2xl font-headline font-bold tracking-tighter">GramFlow</span>
        </div>
        <div className="ml-auto flex items-center gap-6">
          <a href="#pricing" className="hidden md:flex text-sm font-bold text-muted-foreground hover:text-foreground transition-colors">
            Pricing
          </a>
          <Link to="/login">
            <Button variant="ghost" className="hidden md:flex">Log In</Button>
          </Link>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="py-20 px-6 md:px-12 flex flex-col items-center text-center space-y-8">

        <h1 className="text-5xl md:text-7xl font-headline font-bold max-w-4xl tracking-tight leading-tight">
          Automate Your Instagram <span className="text-primary">Engagement</span> Like Never Before
        </h1>
        <p className="text-xl text-muted-foreground max-w-2xl mx-auto leading-relaxed">
          GramFlow helps agencies and creators scale their reach with intelligent keyword triggers, automated DM replies, and deep audience insights.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 pt-4">
          <Link to="/login">
            <Button size="lg" className="h-14 px-8 rounded-2xl bg-primary text-lg glow-indigo group">
              Start Free Trial
              <ArrowRight className="ml-2 group-hover:translate-x-1 transition-transform" />
            </Button>
          </Link>
        </div>


      </section>

      {/* Features */}
      <section className="py-24 bg-secondary/30">
        <div className="px-6 md:px-12 max-w-6xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-12">
            <div className="space-y-4">
              <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
                <MessageSquare size={24} />
              </div>
              <h3 className="text-2xl font-headline font-bold">Smart Auto-Replies</h3>
              <p className="text-muted-foreground">Set up keyword-based triggers to automatically respond to DMs and comments in seconds.</p>
            </div>
            <div className="space-y-4">
              <div className="w-12 h-12 rounded-xl bg-accent/10 flex items-center justify-center text-accent">
                <Target size={24} />
              </div>
              <h3 className="text-2xl font-headline font-bold">Seamless Meta Integration</h3>
              <p className="text-muted-foreground">Connect your Instagram Professional account securely using the official Meta Graph API in one click.</p>
            </div>
            <div className="space-y-4">
              <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center text-primary">
                <BarChart3 size={24} />
              </div>
              <h3 className="text-2xl font-headline font-bold">Deep Analytics</h3>
              <p className="text-muted-foreground">Track engagement rates, trigger performance, and audience growth with beautiful, real-time charts.</p>
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="py-24 border-t border-white/5 relative overflow-hidden">
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-primary/20 rounded-full blur-[120px] -z-10 opacity-50"></div>
        <div className="px-6 md:px-12 max-w-6xl mx-auto space-y-16 text-center">
          <div className="space-y-4">
            <h2 className="text-4xl md:text-5xl font-headline font-bold">Ready to put your DMs on autopilot ?</h2>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">Start automating for free, upgrade when you need more power.</p>
          </div>

          <div className="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto text-left">
            {/* Free Tier */}
            <div className="glass-card border-white/5 rounded-3xl p-8 space-y-8 flex flex-col">
              <div>
                <h3 className="text-2xl font-bold font-headline text-foreground">Free Plan</h3>
                <div className="mt-4 flex items-baseline gap-2">
                  <span className="text-5xl font-headline font-bold tracking-tight">₹0</span>
                  <span className="text-muted-foreground">/ forever</span>
                </div>
              </div>
              <ul className="space-y-4 flex-1">
                <li className="flex items-center gap-3"><ShieldCheck className="text-primary" size={20}/> <span>1 Active Automation Rule</span></li>
                <li className="flex items-center gap-3"><ShieldCheck className="text-primary" size={20}/> <span>Basic Analytics</span></li>
                <li className="flex items-center gap-3"><ShieldCheck className="text-primary" size={20}/> <span>Up to 50 Auto-Replies/mo</span></li>
              </ul>
              <Link to="/login" className="block w-full">
                <Button variant="outline" className="w-full h-14 rounded-xl border-white/10 hover:bg-white/5 text-lg font-bold">Get Started Free</Button>
              </Link>
            </div>

            {/* Pro Tier */}
            <div className="glass-card border-primary/30 rounded-3xl p-8 space-y-8 flex flex-col relative overflow-hidden">
              <div className="absolute inset-0 bg-gradient-to-br from-primary/10 via-transparent to-transparent"></div>
              <div className="absolute top-0 right-8 transform -translate-y-1/2">
                <span className="bg-primary text-primary-foreground text-xs font-bold px-3 py-1 rounded-full tracking-widest uppercase shadow-lg glow-indigo">Most Popular</span>
              </div>
              <div className="relative">
                <h3 className="text-2xl font-bold font-headline text-primary">Pro Plan</h3>
                <div className="mt-4 flex items-baseline gap-2">
                  <span className="text-5xl font-headline font-bold tracking-tight">₹299</span>
                  <span className="text-muted-foreground">/ month</span>
                </div>
              </div>
              <ul className="space-y-4 flex-1 relative">
                <li className="flex items-center gap-3"><Zap className="text-primary" size={20}/> <span className="font-bold">Unlimited Automation Rules</span></li>
                <li className="flex items-center gap-3"><BarChart3 className="text-primary" size={20}/> <span className="font-bold">Advanced Insights & Leaderboards</span></li>
                <li className="flex items-center gap-3"><MessageSquare className="text-primary" size={20}/> <span>Priority Email Support</span></li>
              </ul>
              <Link to="/login" className="block w-full relative">
                <Button className="w-full h-14 rounded-xl bg-primary hover:bg-primary/90 text-white text-lg font-bold glow-indigo">Upgrade to Pro</Button>
              </Link>
            </div>
          </div>
        </div>
      </section>

      <footer className="mt-auto py-12 border-t border-white/5 px-6 md:px-12">
        <div className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-center gap-8">
          <div className="flex flex-col items-center md:items-start gap-2 opacity-50">
            <div className="flex items-center gap-2">
              <Zap size={20} />
              <span className="font-headline font-bold">GramFlow</span>
            </div>
            <span className="text-xs">© 2026 Shubham Prakash Patel. All rights reserved.</span>
          </div>
          <div className="flex gap-8 text-sm text-muted-foreground">
            <Link to="/privacy-policy" className="hover:text-primary transition-colors">Privacy Policy</Link>
            <Link to="/terms" className="hover:text-primary transition-colors">Terms of Service</Link>
          </div>
        </div>
      </footer>
    </div>
  );
}
