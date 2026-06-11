import { Outlet } from "react-router-dom";
import { SidebarProvider, SidebarInset } from "@/components/ui/sidebar";
import { GramSidebar } from "@/components/GramSidebar";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Camera as Instagram } from "lucide-react";
import { instagramAPI, authAPI, automationAPI } from "@/lib/api";

export default function DashboardLayout() {
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isConnecting, setIsConnecting] = useState(false);
  const [account, setAccount] = useState(null);
  const [stats, setStats] = useState(null);
  const [rules, setRules] = useState(null);
  const [media, setMedia] = useState(null);

  useEffect(() => {
    const initDashboard = async () => {
      try {
        const [profileData, accountData] = await Promise.all([
          authAPI.getMe(),
          instagramAPI.getAccount().catch(() => null)
        ]);
        setUser(profileData);
        if (accountData) {
          setAccount(accountData);
          setIsConnected(true);
          const [statsData, rulesData, mediaData] = await Promise.all([
            automationAPI.getStats().catch(() => null),
            automationAPI.getAutomations().catch(() => []),
            instagramAPI.getMedia().catch(() => [])
          ]);
          setStats(statsData);
          setRules(rulesData);
          setMedia(mediaData);
        }
      } catch (error) {
        // Suppress
      } finally {
        setLoading(false);
      }
    };
    initDashboard();

    if (!window.FB) {
      window.fbAsyncInit = function() {
        window.FB.init({
          appId      : import.meta.env.VITE_META_APP_ID,
          cookie     : true,
          xfbml      : true,
          version    : 'v20.0'
        });
      };
      
      (function(d, s, id){
         var js, fjs = d.getElementsByTagName(s)[0];
         if (d.getElementById(id)) {return;}
         js = d.createElement(s); js.id = id;
         js.src = "https://connect.facebook.net/en_US/sdk.js";
         fjs.parentNode.insertBefore(js, fjs);
       }(document, 'script', 'facebook-jssdk'));
    }
  }, []);

  const handleInstagramConnect = () => {
    setIsConnecting(true);

    if (!window.FB) {
      alert("Facebook SDK not loaded yet. Please wait or check adblocker.");
      setIsConnecting(false);
      return;
    }

    try {
      window.FB.login(
        function (response) {
          if (response.authResponse) {
            const sendToBackend = async () => {
              try {
                await instagramAPI.connect(response.authResponse.accessToken);
                setIsConnected(true);
                setIsConnecting(false);
              } catch (error) {
                setIsConnecting(false);
                const backendMsg = error.response?.data?.message || "Failed to connect Instagram account to our servers. Check console.";
                setTimeout(() => alert("Connection Failed:\n\n" + backendMsg), 100);
              }
            };
            sendToBackend();

          } else {
            setIsConnecting(false);
          }
        },
        {
          scope: "instagram_basic,instagram_manage_comments,instagram_manage_messages,pages_show_list,pages_read_engagement,business_management,pages_manage_metadata",
        }
      );
    } catch (err) {
      setIsConnecting(false);
    }
  };

  if (!user && !loading) return null;

  return (
    <SidebarProvider defaultOpen={true}>
      <div className="flex w-full min-h-screen">
        <GramSidebar />
        <SidebarInset className="bg-background flex flex-col flex-1 w-full relative overflow-hidden">
          <header className="h-16 border-b flex items-center px-8 bg-background/50 backdrop-blur-md sticky top-0 z-10 shrink-0">
            <div className="flex items-center gap-4">
               <h1 className="text-xl font-headline font-bold tracking-tight">Dashboard</h1>
            </div>
            <div className="ml-auto flex items-center gap-4">
              {loading ? (
                <div className="w-32 h-9 bg-secondary animate-pulse rounded-xl"></div>
              ) : !isConnected ? (
                <Button 
                  onClick={handleInstagramConnect} 
                  disabled={isConnecting}
                  className="bg-gradient-to-r from-purple-500 via-pink-500 to-orange-500 text-white font-bold h-9 px-4 glow-indigo border-0"
                >
                  <Instagram size={16} className="mr-2" />
                  {isConnecting ? "Connecting..." : "Connect Instagram"}
                </Button>
              ) : (
                <div className="flex items-center gap-2 text-sm bg-primary/10 text-primary px-3 py-1.5 rounded-full border border-primary/20">
                  <Instagram size={14} />
                  <span className="font-bold">Instagram Connected</span>
                </div>
              )}
            </div>
          </header>
          <main className="flex-1 p-8 overflow-y-auto">
            <div className="max-w-6xl mx-auto w-full h-full">
              {loading ? (
                <div className="h-full flex items-center justify-center">
                  <span className="w-8 h-8 rounded-full border-4 border-primary border-t-transparent animate-spin"></span>
                </div>
              ) : !user ? null : (
                <div className="flex flex-col h-full gap-4">
                  {(() => {
                    if (!user) return null;
                    const now = new Date();
                    let warningMsg = null;
                    let isExpired = false;
                    
                    if (!user.subscriptionTier || user.subscriptionTier === 'FREE') {
                       // Check trialEndsAt (returned as array or string from backend)
                       if (user.trialEndsAt) {
                         const trialEnd = new Date(user.trialEndsAt[0], user.trialEndsAt[1]-1, user.trialEndsAt[2], user.trialEndsAt[3], user.trialEndsAt[4]);
                         if (now > trialEnd) {
                            warningMsg = "Your 10-day Free Trial has expired. Your automations are currently halted.";
                            isExpired = true;
                         } else {
                            const daysLeft = Math.ceil((trialEnd - now) / (1000 * 60 * 60 * 24));
                            if (daysLeft <= 3) {
                              warningMsg = `Your Free Trial expires in ${daysLeft} days. Upgrade to keep your automations running!`;
                            }
                         }
                     } else if (user.createdAt) {
                         // Fallback
                         const created = new Date(user.createdAt[0], user.createdAt[1]-1, user.createdAt[2], user.createdAt[3], user.createdAt[4]);
                         const trialEnd = new Date(created.getTime() + 10 * 24 * 60 * 60 * 1000);
                         if (now > trialEnd) {
                            warningMsg = "Your 10-day Free Trial has expired. Your automations are currently halted.";
                            isExpired = true;
                         }
                       }
                       
                       if (!isExpired && user.monthlyRepliesCount >= 50) {
                           warningMsg = "You've reached your free tier limit of 50 auto-replies this month. Your automations are currently halted.";
                           isExpired = true;
                       }
                    } else {
                       if (user.subscriptionValidUntil) {
                         const validUntil = new Date(user.subscriptionValidUntil[0], user.subscriptionValidUntil[1]-1, user.subscriptionValidUntil[2], user.subscriptionValidUntil[3], user.subscriptionValidUntil[4]);
                         if (now > validUntil) {
                            warningMsg = "Your Pro Subscription has expired. Your automations are currently halted.";
                            isExpired = true;
                         }
                       }
                    }

                    if (!warningMsg) return null;

                    return (
                      <div className={`p-4 rounded-xl border flex items-center justify-between ${isExpired ? 'bg-destructive/10 border-destructive text-destructive' : 'bg-orange-500/10 border-orange-500/50 text-orange-500'}`}>
                        <div className="flex items-center gap-3">
                           <span className="font-bold">{warningMsg}</span>
                        </div>
                        <Button variant={isExpired ? "destructive" : "outline"} onClick={() => window.location.href = '/dashboard/billing'} className={isExpired ? "glow-destructive" : ""}>
                          Upgrade to Pro
                        </Button>
                      </div>
                    );
                  })()}
                  <Outlet context={{ isConnected, user, account, stats, setStats, rules, setRules, media, setMedia }} />
                </div>
              )}
            </div>
          </main>
        </SidebarInset>
      </div>
    </SidebarProvider>
  );
}
