import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Zap, AlertCircle } from "lucide-react";
import { useState, useEffect } from "react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { useAuth } from "@/contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import { auth, googleProvider, firebaseAPI } from "@/lib/firebase";
import { signInWithPopup } from "firebase/auth";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [errorMessage, setErrorMessage] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const { loginWithFirebaseToken } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const checkEmailLink = async () => {
      try {
        const user = await firebaseAPI.verifyEmailLink();
        if (user) {
          setIsLoading(true);
          const token = await user.getIdToken();
          await loginWithFirebaseToken(token);
          navigate("/dashboard");
        }
      } catch {
        setErrorMessage("Failed to verify email link. It may have expired.");
      } finally {
        setIsLoading(false);
      }
    };
    checkEmailLink();
  }, [loginWithFirebaseToken, navigate]);

  const handleEmailLink = async (e) => {
    e.preventDefault();
    if (!email) return;
    setIsLoading(true);
    setErrorMessage(null);
    setSuccessMessage(null);
    try {
      await firebaseAPI.sendLoginLink(email);
      setSuccessMessage("A magic sign-in link has been sent to your email! Click it to continue.");
    } catch (error) {
      setErrorMessage(error.message || "Failed to send magic link. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const result = await signInWithPopup(auth, googleProvider);
      const token = await result.user.getIdToken();
      await loginWithFirebaseToken(token);
      navigate("/dashboard");
    } catch (error) {
      setErrorMessage(error.message || "Google Sign-In failed. Please try again.");
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-6">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,_var(--tw-gradient-stops))] from-primary/10 via-background to-background -z-10"></div>
      
      <Card className="max-w-md w-full glass-card border-white/5 shadow-2xl">
        <CardHeader className="text-center space-y-4">
          <div className="flex justify-center">
            <div className="w-12 h-12 rounded-2xl bg-primary flex items-center justify-center glow-indigo">
              <Zap className="text-primary-foreground fill-current" size={28} />
            </div>
          </div>
          <div className="space-y-2">
            <CardTitle className="text-3xl font-headline font-bold">Welcome to GramFlow</CardTitle>
            <CardDescription className="text-lg">
              Sign in or create an account to start automating.
            </CardDescription>
          </div>
        </CardHeader>
        <CardContent className="p-8 space-y-6">
          {errorMessage && (
            <Alert variant="destructive" className="bg-destructive/10 border-destructive/20 text-destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertTitle>Error</AlertTitle>
              <AlertDescription className="text-xs">
                {errorMessage}
              </AlertDescription>
            </Alert>
          )}

          {successMessage && (
            <Alert className="bg-primary/10 border-primary/20 text-primary">
              <AlertTitle>Success</AlertTitle>
              <AlertDescription className="text-xs">
                {successMessage}
              </AlertDescription>
            </Alert>
          )}

          <div className="space-y-8">
            <Button 
              variant="outline" 
              onClick={handleGoogleLogin} 
              disabled={isLoading}
              className="w-full h-14 rounded-xl border-white/10 hover:bg-white/5 font-semibold text-lg"
            >
              <svg className="w-6 h-6 mr-3" viewBox="0 0 24 24">
                <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
              </svg>
              Continue with Google
            </Button>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t border-white/10" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-background px-2 text-muted-foreground font-bold tracking-widest">
                  Or use Magic Link
                </span>
              </div>
            </div>

          <form onSubmit={handleEmailLink} className="space-y-4">
            <div className="space-y-2">
              <label className="text-sm font-medium">Email Address</label>
              <input 
                type="email" 
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-black/20 border border-white/10 rounded-lg p-3 text-white focus:ring-2 focus:ring-primary focus:outline-none"
                placeholder="you@example.com"
              />
            </div>
            
            <Button 
              type="submit"
              disabled={isLoading || !email}
              className="w-full h-12 rounded-xl bg-primary hover:bg-primary/90 text-white font-bold mt-4"
            >
              {isLoading ? "Processing..." : "Send Magic Link"}
            </Button>
          </form>
          </div>

          <p className="text-center text-xs text-muted-foreground leading-relaxed mt-6">
            By continuing, you agree to our Terms of Service and Privacy Policy.
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
