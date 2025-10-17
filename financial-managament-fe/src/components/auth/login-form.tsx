import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { AlertCircleIcon } from "lucide-react"
import {
  Alert,
  AlertDescription,
  AlertTitle,
} from "@/components/ui/alert"
import { loginAuth } from "@/api/AuthApi";

interface Errors {
    username: string;
    password: string;
}

interface UserInfo {
  username: string;
}

export function LoginForm() {

    const { login } = useAuth();
    const navigate = useNavigate();
    const [username, setUsername] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [errors, setErrors] = useState<Errors>({
        username: '',
        password: ''
    });
    const [errorsAll, setErrorsAll] = useState<string>("");

    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            navigate("/");
        }
    }, [navigate]);

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        setErrorsAll("");
        setIsLoading(true);
        if (validateForm()) {
            try {
                const result = await loginAuth({ username, password});
                
                if(result && result.data){

                    const userData: UserInfo = {
                        username: result.data.username,
                    };

                    localStorage.setItem("accessToken", result.data.token);
                    localStorage.setItem("refreshToken", result.data.refreshToken);
                    localStorage.setItem("user_data", JSON.stringify(userData));
                    login(result.data);
                    navigate("/");
                }else{
                    setErrorsAll("Login gagal. Cek email/password.");
                }
            } catch (err) {
                console.error("Gagal login", err);
                setErrorsAll("Login gagal. Cek email/password.");
            }finally {
                setIsLoading(false);
            }
        }else {
            setIsLoading(false);
        }
    }

    function validateForm(): boolean{
        console.log("proccess validation");
        let valid = true;
        const errorsCopy = {... errors}
        if(username.trim()){
            errorsCopy.username = '';
        }else{
            errorsCopy.username = 'username is required';
            valid = false;
        }

        if(password.trim()){
            errorsCopy.password = '';
        }else{
            errorsCopy.password = 'password is required';
            valid = false;
        }
        setErrors(errorsCopy);
        return valid;
    }

    return (
        <form className={cn("flex flex-col gap-6")} onSubmit={handleSubmit}>
            <div className="flex flex-col items-center gap-2 text-center">
                <h1 className="text-2xl font-bold">Login to your account</h1>
                <p className="text-muted-foreground text-sm text-balance">
                Enter your email below to login to your account
                </p>
            </div>
            {errorsAll && 
                <Alert variant="destructive">
                    <AlertCircleIcon className="text-red-500"/>
                    <AlertTitle>Login gagal</AlertTitle>
                    <AlertDescription>
                        <span>{errorsAll}</span>
                    </AlertDescription>
                </Alert>
            }
            <div className="grid gap-6">
                <div className="grid gap-3">
                    <Label htmlFor="username">Username</Label>
                    <Input 
                        id="username" 
                        type="text" 
                        value={username}
                        onChange={(e) => setUsername(e.target.value)} 
                        required 
                        autoFocus
                    />
                    {errors.username && <p className="text-red-500 text-sm">{errors.username}</p>}
                </div>
                <div className="grid gap-3">
                    <Label htmlFor="password">Password</Label>
                    <Input 
                        id="password" 
                        type="password" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)} 
                        required 
                    />
                    {errors.password && <p className="text-red-500 text-sm">{errors.password}</p>}
                </div>
                
                <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Logging in..." : "Login"}
                </Button>
                
            </div>
            <div className="text-center text-sm">
                Don&apos;t have an account?{" "}
                <Link to="/register" className="underline underline-offset-4">
                    Sign up
                </Link>
            </div>
        </form>
    )
}
