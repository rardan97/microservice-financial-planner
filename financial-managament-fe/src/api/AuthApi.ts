import { REST_API_BASE_URL_AUTH } from "@/config";
import type { ApiResponse } from "@/interface/ApiResponse.interface";
import type { LoginReq, LoginRes } from "@/interface/Login.interface";
import type { RegisterReq, RegisterRes } from "@/interface/Register.interface";
import axios from "axios";

export const api = axios.create({
    baseURL: REST_API_BASE_URL_AUTH,
    withCredentials: true
});


export async function registerAuth(data: RegisterReq): Promise<ApiResponse<RegisterRes> | null > {
    console.log("data :"+data);
    try{
        const response = await api.post<ApiResponse<RegisterRes>>(`/registration`, data);
        console.log(response);
        return response.data;
    }catch (error){
        console.error("Sign Up failed:", error);
        throw new Error("Sign Up failed");
    }
}

export async function loginAuth(data: LoginReq): Promise<ApiResponse<LoginRes> | null> {
  try {
    const response = await api.post<ApiResponse<LoginRes>>(`/login`, data);
    return response.data;
  } catch (error) {
    console.error("Login failed:", error);
    return null;
  }
}

