import { Navigate } from "react-router-dom";
import type { JSX } from "react";
import { useAuth } from "@/context/AuthContext";



interface PrivateRouteProps {
  children: JSX.Element;
}

const PrivateRoute = ({ children }: PrivateRouteProps) => {
  const { isAuthenticated } = useAuth();

  return isAuthenticated ? children : <Navigate to="/login" />;
};

export default PrivateRoute;