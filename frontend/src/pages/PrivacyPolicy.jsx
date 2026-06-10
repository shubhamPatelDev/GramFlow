import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ShieldCheck, ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function PrivacyPolicy() {
  return (
    <div className="min-h-screen bg-background py-16 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto space-y-8">
        <Link to="/">
          <Button variant="ghost" className="mb-4">
            <ArrowLeft className="mr-2 h-4 w-4" /> Back to Home
          </Button>
        </Link>
        <div className="text-center space-y-4">
          <div className="mx-auto w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center glow-indigo">
            <ShieldCheck className="h-8 w-8 text-primary" />
          </div>
          <h1 className="text-4xl font-headline font-bold">Privacy Policy</h1>
          <p className="text-muted-foreground">Last Updated: October 2026</p>
        </div>

        <Card className="glass-card border-white/5 shadow-2xl">
          <CardContent className="p-8 prose prose-invert max-w-none text-muted-foreground">
            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">1. Information We Collect</h2>
            <p>
              When you connect your Meta (Facebook/Instagram) account to our platform, we request access to your Instagram profile, comments, and messages. We collect and store:
            </p>
            <ul className="list-disc pl-6 space-y-2 mt-4">
              <li>Your Instagram Username and Profile ID</li>
              <li>Authentication Tokens required to send automated messages on your behalf</li>
              <li>The comments and messages triggered by your automation rules</li>
            </ul>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">2. How We Use Your Information</h2>
            <p>
              Your data is strictly used to provide the automation services you configure. We use the Meta Graph API to read comments on your posts and send Direct Messages on your behalf. We do not use your data for advertising, nor do we sell it to third parties.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">3. Data Retention and Deletion</h2>
            <p>
              We retain your data only as long as you maintain an active account with us. You have the right to request deletion of your data at any time. 
              If you deauthorize our application from your Instagram settings, our systems will automatically receive a webhook from Meta and permanently purge your access tokens and account data from our servers.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">4. Security</h2>
            <p>
              We implement industry-standard encryption to protect your access tokens and personal information. All communications between our servers and Meta's API are secured via HTTPS.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">5. Contact Us</h2>
            <p>
              If you have any questions about this Privacy Policy or wish to manually request data deletion, please contact our support team at privacy@commentor.example.com.
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
