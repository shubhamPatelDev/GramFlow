import { createContext, useContext, useState, useEffect } from "react";
import { authAPI } from "@/lib/api";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem("jwt_token"));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Basic validation to keep user session alive
    if (token) {
      // Decode JWT token for user info or just set generic user for now
      setUser({ id: "1", name: "GramFlow User" });
    }
    setLoading(false);
  }, [token]);

  const loginWithFirebaseToken = async (firebaseToken) => {
    // Send the token to the backend's new firebase-login endpoint
    const data = await authAPI.firebaseLogin(firebaseToken);
    localStorage.setItem("jwt_token", data.token);
    setToken(data.token);
    setUser(data.user || { id: "1", name: "GramFlow User" });
    return data;
  };

  const logout = () => {
    localStorage.removeItem("jwt_token");
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, loginWithFirebaseToken, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
