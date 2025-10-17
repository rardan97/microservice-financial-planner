
import { REST_API_BASE_URL_CATEGORY } from "@/config";
import type { ApiResponse } from "@/interface/ApiResponse.interface";
import type { Category, CategoryDto } from "@/interface/Category.interface";
import axios from "axios";

export const api = axios.create({
    baseURL: REST_API_BASE_URL_CATEGORY,
    withCredentials: true
});

export async function getListCategories(token: string) : Promise<ApiResponse<Category[]> | null>{
    console.log("Data Token : "+token);
    try{
        const response = await api.get<ApiResponse<Category[]>>(`/getCategoryAll`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    }catch(error){
        console.error("Error during user fetch:", error);
        throw new Error("Failed to fetch users");
    }
}

export async function getCategoryValueById(token: string, id : number) : Promise<ApiResponse<Category> | null>{
    console.log("check token :"+token);
    console.log("check id :"+id);
    try{
        const response = await api.get<ApiResponse<Category>>(`/getCategoryById/${id}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });
        return response.data;
    }catch(error){
        console.error("Error during user fetch:", error);
        throw new Error("Failed to fetch users");
    }
}

export async function addCategories(token: string, data: CategoryDto) : Promise<Category>{
    console.log("token :"+token);
    console.log("data :"+data);
    try{
        console.log("Final URL:", api.defaults.baseURL + "/createCategory");
        const response = await api.post<Category>('/createCategory', data, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            }, 
        });
        return response.data;
    }catch(error){
        console.error("Error during user fetch:", error);
        throw new Error("Failed to fetch users");
    }
}

export async function editCategories(token: string, id : number, data: Category) : Promise<Category>{
    try{
        const response = await api.put<Category>(`/updateCategory/${id}`, data, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            }, 
        });
        return response.data;
    }catch(error){
        console.error("Error during user fetch:", error);
        throw new Error("Failed to fetch users");
    }
}

export async function delCategoryValueById(token: string, id : number) : Promise<string>{
    try{
        const response = await api.delete<string>(`/deleteCategory/${id}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        });
        console.log("test delete");
        console.log(response);
        return response.data;
    }catch(error){
        console.error("Error during user fetch:", error);
        throw new Error("Failed to fetch users");
    }
}