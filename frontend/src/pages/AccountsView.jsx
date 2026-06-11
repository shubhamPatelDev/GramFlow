import { useState, useEffect } from "react";
import { Camera as Instagram, Zap, Link as LinkIcon, CheckCircle2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { instagramAPI } from "@/lib/api";
import { useOutletContext } from "react-router-dom";

export default function AccountsView() {
  const { account } = useOutletContext() || {};

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-200">
      <div>
        <h2 className="text-3xl font-headline font-bold text-foreground">Connected Account</h2>
        <p className="text-muted-foreground mt-1 text-lg">Manage your linked social profiles.</p>
      </div>

      {!account ? (
        <div className="text-center py-20 bg-secondary/10 rounded-3xl border border-dashed border-white/10">
          <LinkIcon className="mx-auto mb-4 text-muted-foreground opacity-20" size={64} />
          <h3 className="text-xl font-headline font-bold mb-2">No Account Connected</h3>
          <p className="text-muted-foreground">Click "Connect Instagram" in the top bar to link your account.</p>
        </div>
      ) : (
        <Card className="glass-card overflow-hidden border-primary/20 max-w-2xl">
          <CardContent className="p-0">
            <div className="flex flex-col md:flex-row md:items-center">
              <div className="p-8 flex items-center justify-center bg-secondary/30">
                {account.instagramProfilePicture ? (
                  <img src={account.instagramProfilePicture} alt="Profile" className="w-24 h-24 rounded-full border-4 border-primary/20" />
                ) : (
                  <div className="w-24 h-24 rounded-full bg-primary/10 flex items-center justify-center">
                    <Instagram className="text-primary" size={40} />
                  </div>
                )}
              </div>
              <div className="p-8 flex-1 space-y-4">
                <div className="flex items-center gap-3">
                  <h3 className="text-2xl font-headline font-bold">@{account.instagramUsername}</h3>
                  <CheckCircle2 className="text-green-500" size={24} />
                </div>
                
                <div className="space-y-2 text-sm">
                  <div className="flex items-center gap-2">
                    <span className="text-muted-foreground font-bold uppercase tracking-widest text-xs">Linked FB Page:</span>
                    <span className="bg-secondary/50 px-3 py-1 rounded-lg font-medium">{account.pageName}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-muted-foreground font-bold uppercase tracking-widest text-xs">Instagram ID:</span>
                    <span className="text-foreground font-mono">{account.id}</span>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
