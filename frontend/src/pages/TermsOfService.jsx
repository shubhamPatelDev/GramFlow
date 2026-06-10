import { Card, CardContent } from "@/components/ui/card";
import { FileText, ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export default function TermsOfService() {
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
            <FileText className="h-8 w-8 text-primary" />
          </div>
          <h1 className="text-4xl font-headline font-bold">Terms of Service</h1>
          <p className="text-muted-foreground">Last Updated: October 2026</p>
        </div>

        <Card className="glass-card border-white/5 shadow-2xl">
          <CardContent className="p-8 prose prose-invert max-w-none text-muted-foreground">
            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">1. Acceptance of Terms</h2>
            <p>
              By accessing and using our automation platform, you agree to comply with and be bound by these Terms of Service. If you do not agree to these terms, please do not use our services.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">2. Acceptable Use Policy</h2>
            <p>
              You agree to use our automation tools strictly in compliance with Meta's Developer Policies. You must not use our platform to:
            </p>
            <ul className="list-disc pl-6 space-y-2 mt-4">
              <li>Send unsolicited spam messages to users.</li>
              <li>Circumvent Meta's 24-hour messaging window rules.</li>
              <li>Distribute malicious content, phishing links, or illegal material.</li>
            </ul>
            <p className="mt-4 text-destructive font-bold">
              Violation of this Acceptable Use Policy will result in immediate termination of your account without refund.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">3. Rate Limiting</h2>
            <p>
              To protect the integrity of our systems and ensure compliance with Meta's anti-spam regulations, our platform automatically rate-limits automated replies. We strictly enforce a limit on the number of automated Direct Messages sent per minute per account. If your account exceeds this limit, excess messages will be dropped.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">4. Disclaimers and Limitations of Liability</h2>
            <p>
              Our services are provided "as is" and "as available". We are not responsible for any actions taken by Meta, including the suspension or banning of your Instagram account or Facebook Page due to misuse of automation tools.
            </p>

            <h2 className="text-foreground text-2xl font-bold mt-8 mb-4">5. Modifications to the Service</h2>
            <p>
              We reserve the right to modify or discontinue, temporarily or permanently, the service with or without notice.
            </p>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
