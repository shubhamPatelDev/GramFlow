import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { LifeBuoy, Loader2, CheckCircle2 } from "lucide-react";
import { useOutletContext } from "react-router-dom";
import { supportAPI } from "@/lib/api";

export default function SupportView() {
  const { user } = useOutletContext();
  const [formData, setFormData] = useState({ subject: "", message: "" });
  const [submitting, setSubmitting] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.subject || !formData.message) return;

    setSubmitting(true);
    try {
      await supportAPI.createTicket(formData);
      setSuccess(true);
      setFormData({ subject: "", message: "" });
      setTimeout(() => setSuccess(false), 5000);
    } catch (error) {
      alert("Failed to submit support ticket. Please try again later.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-200 max-w-3xl mx-auto">
      <div>
        <h2 className="text-3xl font-headline font-bold text-foreground">Support Center</h2>
        <p className="text-muted-foreground mt-1 text-lg">Need help? We're here for you.</p>
      </div>

      <Card className="glass-card border-white/5">
        <CardHeader>
          <CardTitle className="text-2xl font-headline flex items-center gap-2">
            <LifeBuoy className="text-primary" /> Contact Us
          </CardTitle>
          <CardDescription>
            Submit a ticket and our team will get back to you within 24 hours.
          </CardDescription>
        </CardHeader>
        <CardContent>
          {success ? (
            <div className="bg-green-500/10 border border-green-500/20 text-green-500 p-6 rounded-2xl flex flex-col items-center justify-center text-center space-y-4 animate-in zoom-in duration-300">
              <CheckCircle2 size={48} />
              <div>
                <h3 className="text-xl font-bold font-headline">Message Sent!</h3>
                <p className="text-sm opacity-80 mt-1">We've received your ticket and will respond to you shortly.</p>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-6">

              <div className="space-y-2">
                <Label htmlFor="subject">Subject</Label>
                <Input 
                  id="subject"
                  placeholder="e.g., Billing Issue, Feature Request" 
                  value={formData.subject}
                  onChange={(e) => setFormData({ ...formData, subject: e.target.value })}
                  className="bg-secondary/50 border-white/10 focus:ring-primary"
                  required
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="message">Message</Label>
                <Textarea 
                  id="message"
                  placeholder="Describe your issue or question in detail..." 
                  value={formData.message}
                  onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                  className="bg-secondary/50 border-white/10 min-h-[150px] focus:ring-primary"
                  required
                />
              </div>

              <Button 
                type="submit" 
                disabled={submitting || !formData.subject || !formData.message}
                className="w-full h-12 bg-primary hover:bg-primary/90 glow-indigo text-white font-bold text-lg"
              >
                {submitting ? (
                  <><Loader2 className="mr-2 h-5 w-5 animate-spin" /> Sending...</>
                ) : (
                  "Submit Ticket"
                )}
              </Button>
            </form>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
