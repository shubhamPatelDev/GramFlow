import { useEffect } from "react";
import { useOutletContext } from "react-router-dom";
import { TrendingUp, Zap, MessageSquare } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { automationAPI } from "@/lib/api";

export default function InsightsView() {
  const { user, stats, setStats, media } = useOutletContext();

  useEffect(() => {
    // Silent background revalidation (Stale-While-Revalidate)
    automationAPI.getStats().then(data => {
      if (setStats) setStats(data);
    }).catch(() => {});
  }, [setStats]);

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-200">
      <div>
        <h2 className="text-3xl font-headline font-bold text-foreground">Insights</h2>
        <p className="text-muted-foreground mt-1 text-lg">Your automation performance at a glance.</p>
      </div>

      <div className="grid md:grid-cols-3 gap-6">
        <Card className="glass-card border-white/5">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-bold uppercase tracking-widest text-muted-foreground flex items-center gap-2">
              <Zap size={16} className="text-primary"/> Total Automations
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-5xl font-headline font-bold">{stats?.totalAutomations || 0}</p>
          </CardContent>
        </Card>
        
        <Card className="glass-card border-white/5">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-bold uppercase tracking-widest text-muted-foreground flex items-center gap-2">
              <TrendingUp size={16} className="text-green-500"/> Active Rules
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-5xl font-headline font-bold text-green-400">{stats?.activeAutomations || 0}</p>
          </CardContent>
        </Card>

        <Card className="glass-card border-white/5 bg-primary/5">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-bold uppercase tracking-widest text-primary flex items-center gap-2">
              <MessageSquare size={16} className="text-primary"/> Auto-Replies Sent
            </CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-5xl font-headline font-bold text-primary">{stats?.totalReplies || 0}</p>
          </CardContent>
        </Card>
      </div>

      <div className="mt-12 space-y-4">
        <h3 className="text-2xl font-headline font-bold text-foreground flex items-center gap-2">
          <Zap className="text-primary" /> Advanced Insights
        </h3>
        
        <div className="relative rounded-3xl border border-white/5 bg-card/30 overflow-hidden">
          {/* Lock Overlay for Free Users */}
          {user?.subscriptionTier !== 'PAID' && (
            <div className="absolute inset-0 z-10 backdrop-blur-md bg-background/40 flex flex-col items-center justify-center text-center p-6 space-y-4">
              <div className="w-16 h-16 rounded-full bg-primary/20 flex items-center justify-center mb-2">
                <svg className="w-8 h-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
              </div>
              <h4 className="text-2xl font-headline font-bold">Pro Insights Locked</h4>
              <p className="text-muted-foreground max-w-md">Upgrade to Pro to see which keywords convert best and which posts drive the most DMs.</p>
              <button 
                onClick={() => window.location.href = '/dashboard/billing'}
                className="mt-4 px-8 py-3 rounded-xl bg-primary text-white font-bold glow-indigo"
              >
                Upgrade to Pro
              </button>
            </div>
          )}

          <div className="grid md:grid-cols-2 gap-6 p-8 relative">
            <div className="space-y-2">
              <p className="text-sm font-bold uppercase tracking-widest text-muted-foreground">Top Performing Keyword</p>
              <p className="text-4xl font-headline font-bold text-foreground">
                {stats?.topPerformingRule || "None"}
              </p>
              <p className="text-sm text-muted-foreground">Highest converting trigger across all active rules</p>
            </div>
            
            <div className="space-y-2">
              <p className="text-sm font-bold uppercase tracking-widest text-muted-foreground">Highest Engagement Post</p>
              {stats?.highestEngagementPost && stats.highestEngagementPost !== "None" && stats.highestEngagementPost !== "Any Post" ? (() => {
                const topPost = media?.find(m => m.id === stats.highestEngagementPost);
                return topPost?.mediaUrl ? (
                  <div className="flex items-center gap-4 py-2">
                    <img src={topPost.mediaUrl} alt="Top Post" className="w-16 h-16 rounded-xl object-cover border border-white/10 shadow-lg" />
                    <p className="text-lg font-headline font-bold text-foreground truncate max-w-[200px]">{topPost.caption || "Instagram Content"}</p>
                  </div>
                ) : (
                  <p className="text-4xl font-headline font-bold text-foreground">{stats.highestEngagementPost}</p>
                );
              })() : (
                <p className="text-4xl font-headline font-bold text-foreground">None</p>
              )}
              <p className="text-sm text-muted-foreground mt-2">Media driving the most automated responses</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
