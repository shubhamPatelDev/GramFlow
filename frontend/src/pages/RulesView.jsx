import { useState, useMemo, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Search, Zap, MessageSquare, Trash2, Loader2, Image as ImageIcon, Video, Layers } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";
import { automationAPI, instagramAPI } from "@/lib/api";
import { useOutletContext } from "react-router-dom";
import toast from "react-hot-toast";

export default function RulesView() {
  const { isConnected, rules: initialRules, setRules, media: initialMedia, setMedia } = useOutletContext() || { isConnected: false };
  const [searchTerm, setSearchTerm] = useState("");
  const [isAdding, setIsAdding] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  
  // Local fallback if context isn't ready
  const rules = initialRules || [];
  const media = initialMedia || [];
  
  const [ruleFormData, setRuleFormData] = useState({ triggerKeyword: "", replyMessage: "", mediaId: "" });

  useEffect(() => {
    if (isConnected) {
      // Silent background revalidation
      Promise.all([
        automationAPI.getAutomations(),
        instagramAPI.getMedia().catch(() => [])
      ]).then(([fetchedRules, fetchedMedia]) => {
        if (setRules) setRules(fetchedRules);
        if (setMedia) setMedia(fetchedMedia || []);
      }).catch(() => {});
    }
  }, [isConnected, setRules, setMedia]);

  const filteredRules = useMemo(() => {
    return rules.filter(rule => 
      (rule.triggerKeyword && rule.triggerKeyword.toLowerCase().includes(searchTerm.toLowerCase()))
    );
  }, [rules, searchTerm]);

  const handleSaveRule = async () => {
    if (!ruleFormData.triggerKeyword || !ruleFormData.replyMessage || !ruleFormData.mediaId) return;

    setSubmitting(true);
    try {
      const newRule = await automationAPI.create(ruleFormData);
      setRules([...rules, newRule]);
      setRuleFormData({ triggerKeyword: "", replyMessage: "", mediaId: "" });
      setIsAdding(false);
      toast.success("Automation rule created!");
    } catch (error) {
      toast.error(error.response?.data?.message || "Failed to create rule");
    } finally {
      setSubmitting(false);
    }
  };

  const toggleRule = async (id) => {
    // Optimistic update for instant UI feedback
    setRules(rules.map(r => r.id === id ? { ...r, active: !r.active } : r));
    
    try {
      await automationAPI.toggle(id);
      toast.success(rules.find(r => r.id === id)?.active ? "Automation disabled" : "Automation enabled");
    } catch (error) {
      // Revert if API fails
      setRules(rules.map(r => r.id === id ? { ...r, active: !r.active } : r));
      toast.error(error.response?.data?.message || "Failed to toggle rule");
    }
  };

  const deleteRule = async (id) => {
    try {
      await automationAPI.delete(id);
      setRules(rules.filter(r => r.id !== id));
      toast.success("Automation deleted");
    } catch (error) {
      toast.error("Failed to delete rule");
    }
  };

  // Remove loading state return since we use global state now,
  // or fallback to empty array which renders instantly.

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-200">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-3xl font-headline font-bold text-foreground">Automation Rules</h2>
          <p className="text-muted-foreground mt-1 text-lg">Define keyword triggers and automated responses.</p>
        </div>
        
        {!isAdding ? (
          <Button onClick={() => setIsAdding(true)} className="bg-primary hover:bg-primary/90 glow-indigo gap-2 h-12 px-6 rounded-xl text-primary-foreground font-bold">
            <Plus size={20} />
            Create New Rule
          </Button>
        ) : null}
      </div>

      {isAdding && (
        <Card className="glass-card border-white/10 text-foreground">
          <CardContent className="space-y-6 py-6">
            <h3 className="text-xl font-bold font-headline">New Automation Rule</h3>
            
            <div className="space-y-2">
              <Label>Select Content</Label>
              {media.length === 0 ? (
                <div className="text-muted-foreground text-sm p-4 bg-secondary/50 rounded-xl border border-white/5">
                  No content found. Please make sure your Instagram account is connected and has posts or reels.
                </div>
              ) : (
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 max-h-64 overflow-y-auto p-2">
                  {media.map((post) => (
                    <div 
                      key={post.id} 
                      onClick={() => setRuleFormData({...ruleFormData, mediaId: post.id})}
                      className={cn(
                        "relative aspect-square rounded-xl overflow-hidden cursor-pointer border-2 transition-all",
                        ruleFormData.mediaId === post.id ? "border-primary glow-indigo scale-95" : "border-transparent hover:border-white/20"
                      )}
                    >
                      {post.mediaUrl ? (
                         <>
                           <img src={post.mediaUrl} alt="Content" className="w-full h-full object-cover" />
                           <div className="absolute top-2 right-2 bg-black/50 backdrop-blur-md p-1.5 rounded-lg text-white">
                             {post.mediaType === 'VIDEO' ? <Video size={14} /> : post.mediaType === 'CAROUSEL_ALBUM' ? <Layers size={14} /> : <ImageIcon size={14} />}
                           </div>
                         </>
                      ) : (
                         <div className="w-full h-full bg-secondary flex items-center justify-center"><ImageIcon size={32} className="text-muted-foreground" /></div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="trigger">Trigger Keyword</Label>
              <Input 
                id="trigger" 
                placeholder="e.g. link" 
                value={ruleFormData.triggerKeyword} 
                onChange={e => setRuleFormData({...ruleFormData, triggerKeyword: e.target.value})}
                className="bg-secondary/50 border-white/10"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="response">Response Message</Label>
              <Textarea 
                id="response" 
                placeholder="What should GramFlow say?" 
                value={ruleFormData.replyMessage} 
                onChange={e => setRuleFormData({...ruleFormData, replyMessage: e.target.value})}
                className="bg-secondary/50 border-white/10 min-h-[120px]"
              />
            </div>
            <div className="flex gap-4">
              <Button onClick={handleSaveRule} disabled={submitting || !ruleFormData.triggerKeyword || !ruleFormData.replyMessage || !ruleFormData.mediaId} className="bg-primary hover:bg-primary/90 glow-indigo text-white font-bold">
                {submitting ? <Loader2 className="animate-spin mr-2" size={16} /> : null}
                Create Rule
              </Button>
              <Button variant="outline" onClick={() => setIsAdding(false)}>Cancel</Button>
            </div>
          </CardContent>
        </Card>
      )}

      <div className="flex items-center gap-4 bg-card/50 p-4 rounded-xl border border-white/5 shadow-sm">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" size={18} />
          <Input 
            placeholder="Search rules..." 
            className="pl-10 bg-secondary/50 border-white/5 h-11 focus:ring-primary"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>
        <span className="h-11 px-4 flex items-center justify-center rounded-lg bg-secondary/50 text-muted-foreground font-medium text-sm">
          {filteredRules.length} Rules Found
        </span>
      </div>

      <div className="grid gap-6">
        {filteredRules.length === 0 && !isAdding && (
          <div className="text-center py-20 bg-secondary/10 rounded-3xl border border-dashed border-white/10">
            <Zap className="mx-auto mb-4 text-muted-foreground opacity-20" size={64} />
            <h3 className="text-xl font-headline font-bold mb-2">No Rules Found</h3>
            <p className="text-muted-foreground">Create your first automation rule to start saving time.</p>
          </div>
        )}
        {filteredRules.map((rule) => {
          const postMedia = media.find(m => m.id === rule.mediaId);
          return (
          <Card key={rule.id} className={cn(
            "glass-card transition-all duration-300 group overflow-hidden border-white/5",
            rule.active ? "border-primary/20" : "opacity-75 grayscale-[0.5]"
          )}>
            <CardContent className="p-0">
              <div className="flex flex-col md:flex-row md:items-stretch">
                <div className="p-6 flex-1 border-r border-white/5">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex items-center gap-4">
                      {postMedia?.mediaUrl ? (
                        <div className="w-14 h-14 rounded-xl overflow-hidden shrink-0 border border-white/10 shadow-sm relative">
                          <img src={postMedia.mediaUrl} className="w-full h-full object-cover" alt="Post" />
                          {rule.active && (
                            <div className="absolute inset-0 ring-2 ring-primary ring-inset rounded-xl" />
                          )}
                        </div>
                      ) : (
                        <div className={cn(
                          "p-3 rounded-xl shrink-0",
                          rule.active ? "bg-primary/10 text-primary glow-indigo" : "bg-muted text-muted-foreground"
                        )}>
                          <Zap size={24} className={rule.active ? "animate-pulse-glow" : ""} />
                        </div>
                      )}
                      <div>
                        <h3 className="text-lg font-headline font-bold truncate max-w-[200px] md:max-w-[300px]">
                          {postMedia?.caption ? postMedia.caption : "Connected Post"}
                        </h3>
                        <p className="text-xs text-primary uppercase tracking-widest font-bold mt-1">Automation Trigger</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-4">
                      <Switch 
                        checked={rule.active} 
                        onCheckedChange={() => toggleRule(rule.id)}
                        className="data-[state=checked]:bg-primary"
                      />
                      <Button variant="ghost" size="icon" className="hover:bg-destructive hover:text-destructive text-muted-foreground" onClick={() => deleteRule(rule.id)}>
                        <Trash2 size={20} />
                      </Button>
                    </div>
                  </div>
                  
                  <div className="space-y-4">
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-bold uppercase tracking-widest text-muted-foreground">Trigger:</span>
                      <span className="bg-accent text-accent-foreground font-headline font-bold px-4 py-1.5 rounded-lg text-sm">
                        {rule.triggerKeyword}
                      </span>
                    </div>
                    <div className="bg-secondary/40 p-5 rounded-2xl border border-white/5 flex gap-4 relative">
                      <div className="absolute top-0 right-0 p-3 opacity-10">
                        <MessageSquare size={40} />
                      </div>
                      <MessageSquare className="text-primary/60 shrink-0 mt-1" size={20} />
                      <p className="text-sm text-foreground leading-relaxed italic font-medium">"{rule.replyMessage}"</p>
                    </div>
                  </div>
                </div>
                
                <div className="bg-secondary/20 p-6 md:w-56 flex flex-col justify-center items-center gap-1 border-t md:border-t-0 border-white/5 text-center">
                  <p className="text-xs font-bold uppercase tracking-widest text-muted-foreground mb-1">Status</p>
                  <p className="text-4xl font-headline font-bold text-primary mb-1">{rule.active ? "ON" : "OFF"}</p>
                  <p className="text-[10px] text-muted-foreground uppercase font-bold tracking-tighter">Running</p>
                </div>
              </div>
            </CardContent>
          </Card>
          );
        })}
      </div>
    </div>
  );
}
