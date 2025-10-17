import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import {jwtDecode} from "jwt-decode";
import type { LoginRes } from "@/interface/Login.interface";

interface JwtPayload {
  sub: string;
  iat: number;
  exp: number;
}

// Struktur info user dari respons login
interface UserInfo {
  userId: number;
  username: string;
}

interface AuthContextType {
  token: string | null;
  refreshToken: string | null;
  user: UserInfo | null;
  isAuthenticated: boolean;
  login: (authData: LoginRes) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  
    const [token, setToken] = useState<string | null>(() => localStorage.getItem("accessToken"));
    const [refreshToken, setRefreshToken] = useState<string | null>(() => localStorage.getItem("refreshToken"));
    
    const [user, setUser] = useState<UserInfo | null>(() => {
        const stored = localStorage.getItem("user_data");
        return stored ? JSON.parse(stored) : null;
    });

    // Fungsi logout
    const logout = () => {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        localStorage.removeItem("user_data");
        setToken(null);
        setRefreshToken(null);
        setUser(null);
    };

    // Cek token expired dan logout otomatis
    useEffect(() => {
        if (token) {
        try {
            const decoded = jwtDecode<JwtPayload>(token);
            const isExpired = decoded.exp * 1000 < Date.now();
            if (isExpired) {
            logout();
            }
        } catch (e) {
            console.error("Invalid token", e);
            logout();
        }
        }
    }, [token]);

    // Sinkronisasi localStorage dengan state jika terjadi perubahan di luar React (optional)
    useEffect(() => {
            function syncStorage() {
              const localToken = localStorage.getItem("accessToken");
              const localRefresh = localStorage.getItem("refreshToken");
              const localUser = localStorage.getItem("user_data");
              if (localToken !== token) setToken(localToken);
              if (localRefresh !== refreshToken) setRefreshToken(localRefresh);
              if (localUser !== JSON.stringify(user)) setUser(localUser ? JSON.parse(localUser) : null);
            }
            window.addEventListener("storage", syncStorage);
            return () => window.removeEventListener("storage", syncStorage);
    }, [token, refreshToken, user]);

    const login = (authData: LoginRes) => {
            const { token, refreshToken, userId, username } = authData;

            const userInfo: UserInfo = {
            userId,
            username,
            };

            localStorage.setItem("accessToken", token);
            localStorage.setItem("refreshToken", refreshToken);
            localStorage.setItem("user_data", JSON.stringify(userInfo));

            setToken(token);
            setRefreshToken(refreshToken);
            setUser(userInfo);
    };

    const isAuthenticated = !!token && !!user;

    return (
        <AuthContext.Provider
        value={{ token, refreshToken, user, isAuthenticated, login, logout }}
        >
        {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};