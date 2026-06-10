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
          scope: "instagram_basic,instagram_manage_comments,instagram_manage_messages,pages_show_list,pages_read_engagement,business_management",
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
                <Outlet context={{ isConnected, user, account, stats, setStats, rules, setRules, media, setMedia }} />
              )}
            </div>
          </main>
        </SidebarInset>
      </div>
    </SidebarProvider>
  );
}
