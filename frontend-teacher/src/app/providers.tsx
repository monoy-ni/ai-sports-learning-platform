import { createContext, ReactNode, useContext, useMemo, useState } from "react";
import { CurrentUser, login as loginRequest } from "../shared/api/authApi";

type AuthContextValue = {
  token: string | null;
  user: CurrentUser | null;
  login: (username: string, password: string) => Promise<CurrentUser>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AppProviders({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem("authToken"));
  const [user, setUser] = useState<CurrentUser | null>(() => {
    const raw = localStorage.getItem("currentUser");
    return raw ? (JSON.parse(raw) as CurrentUser) : null;
  });

  const value = useMemo<AuthContextValue>(
    () => ({
      token,
      user,
      async login(username, password) {
        const result = await loginRequest({ username, password });
        localStorage.setItem("authToken", result.token);
        localStorage.setItem("currentUser", JSON.stringify(result.user));
        setToken(result.token);
        setUser(result.user);
        return result.user;
      },
      logout() {
        localStorage.removeItem("authToken");
        localStorage.removeItem("currentUser");
        setToken(null);
        setUser(null);
      }
    }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AppProviders");
  }
  return context;
}

