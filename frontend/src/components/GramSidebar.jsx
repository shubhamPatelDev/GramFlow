import { LayoutDashboard, Zap, CreditCard, UserCircle, Settings, LogOut, Camera as Instagram, LifeBuoy } from "lucide-react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { cn } from "@/lib/utils";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
  SidebarMenuButton,
  SidebarGroup,
  SidebarGroupLabel,
  SidebarGroupContent,
} from "@/components/ui/sidebar";

const mainNav = [
  { name: "Insights", icon: LayoutDashboard, href: "/dashboard/insights" },
  { name: "Automation Rules", icon: Zap, href: "/dashboard/rules" },
  { name: "Accounts", icon: Instagram, href: "/dashboard/accounts" },
];

const secondaryNav = [
  { name: "Subscription", icon: CreditCard, href: "/dashboard/billing" },
  { name: "Settings", icon: Settings, href: "/dashboard/settings" },
  { name: "Support", icon: LifeBuoy, href: "/dashboard/support" },
];

export function GramSidebar() {
  const location = useLocation();
  const navigate = useNavigate();
  const pathname = location.pathname;

  const handleSignOut = async () => {
    // In production, clear tokens here
    navigate("/");
  };

  return (
    <Sidebar variant="sidebar" className="border-r border-white/5 bg-card/30">
      <SidebarHeader className="p-6 border-b border-white/5">
        <Link to="/dashboard" className="flex items-center gap-2 group">
          <div className="w-10 h-10 rounded-xl bg-primary flex items-center justify-center glow-indigo group-hover:scale-105 transition-transform">
            <Zap className="text-primary-foreground fill-current" size={24} />
          </div>
          <span className="text-xl font-headline font-bold tracking-tighter text-foreground">GramFlow</span>
        </Link>
      </SidebarHeader>
      
      <SidebarContent className="px-4 py-6">
        <SidebarGroup>
          <SidebarGroupLabel className="text-muted-foreground uppercase text-[10px] tracking-widest font-bold mb-4">Core</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {mainNav.map((item) => (
                <SidebarMenuItem key={item.name}>
                  <SidebarMenuButton 
                    asChild 
                    isActive={pathname === item.href}
                    className={cn(
                      "flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200",
                      pathname === item.href 
                        ? "bg-primary text-primary-foreground glow-indigo" 
                        : "text-muted-foreground hover:text-foreground hover:bg-secondary"
                    )}
                  >
                    <Link to={item.href}>
                      <item.icon size={20} />
                      <span className="font-medium">{item.name}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup className="mt-8">
          <SidebarGroupLabel className="text-muted-foreground uppercase text-[10px] tracking-widest font-bold mb-4">Management</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {secondaryNav.map((item) => (
                <SidebarMenuItem key={item.name}>
                  <SidebarMenuButton 
                    asChild 
                    isActive={pathname === item.href}
                    className={cn(
                      "flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200",
                      pathname === item.href 
                        ? "bg-primary text-primary-foreground glow-indigo" 
                        : "text-muted-foreground hover:text-foreground hover:bg-secondary"
                    )}
                  >
                    <Link to={item.href}>
                      <item.icon size={20} />
                      <span className="font-medium">{item.name}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="p-6 mt-auto">
        <button 
          onClick={handleSignOut}
          className="flex items-center gap-3 px-4 py-3 w-full rounded-lg text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors duration-200"
        >
          <LogOut size={20} />
          <span className="font-medium">Sign Out</span>
        </button>
      </SidebarFooter>
    </Sidebar>
  );
}
