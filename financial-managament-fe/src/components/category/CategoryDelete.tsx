import React, { useCallback, useEffect, useState } from "react";
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"

import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useModal } from "@/hooks/useModal";
import { delCategoryValueById, getCategoryValueById } from "@/api/CategoryApi";
import { Alert, AlertDescription, AlertTitle } from "../ui/alert";
import { AlertCircleIcon } from "lucide-react";


type CategoryDeleteProps = {
    onSuccess: () => void;
    idCat: number;
};

export default function CategoryDelete({onSuccess, idCat} : CategoryDeleteProps) {

    const { isOpen, setIsOpen, openModal, closeModal } = useModal();
    const [categoryId, setCategoryId] = useState<number>();  
    const [categoryName, setCategoryName] = useState<string>("");
    const [errorsAll, setErrorsAll] = useState<string>("");

    const getCategoryById = useCallback(async (): Promise<void> => {
            const token = localStorage.getItem("accessToken");
            if (!token){
                return;
            }
            try {
                const response = await getCategoryValueById(token, idCat);
                console.log("Success processing data");
                if(response && response.data){
                    setCategoryId(response.data.categoryId);
                    setCategoryName(response.data.categoryName);
                }
            } catch (error) {
                console.log("Failed processing data", error);
                throw error;
            }
        }, [idCat]);
        
        useEffect(() => {
                if (isOpen) {
                    getCategoryById();
                }
        }, [isOpen, getCategoryById]);

   
    const handleSave = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const token = localStorage.getItem("accessToken");
        if (!token) {
            return;
        }
       
        console.log("success validation");
        try {
            if (categoryId === undefined) {
                throw new Error("categoryId is undefined");
            }
            
            const result = await delCategoryValueById(token, categoryId);
            if(result){
                console.log("success add data", result);
                setCategoryName("");
                setErrorsAll("");
                closeModal();
                onSuccess();
            }else{
                setErrorsAll("Login gagal. Cek email/password.");
            }
        } catch (err) {
            console.error("Gagal login", err);
            setErrorsAll("Login gagal. Cek email/password.");
        }

        console.log("Saving changes...");
        closeModal();
    };


  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
            <Button variant="outline" onClick={openModal}>Delete</Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-[425px]" >
            <DialogHeader>
                <DialogTitle>Delete Category</DialogTitle>
                <DialogDescription>
                Make changes to your category here. Click save when you&apos;re
                done.
                </DialogDescription>
            </DialogHeader>
            <form className={cn("grid items-start gap-6")} onSubmit={handleSave}>
                {errorsAll && 
                    <Alert variant="destructive">
                        <AlertCircleIcon />
                        <AlertTitle>Unable to process your payment.</AlertTitle>
                        <AlertDescription>
                        <p>Please verify your billing information and try again.</p>
                        {errorsAll}
                        </AlertDescription>
                    </Alert>
                }

                <Input 
                    id="categoryId" 
                    type="hidden" 
                    value={categoryId ?? ''}
                    onChange={(e) => setCategoryId(Number(e.target.value))}
                />

                <div className="grid gap-3">
                    <Label htmlFor="categoryName">Category Name</Label>
                    <Input 
                        id="categoryName" 
                        type="text" 
                        value={categoryName}
                        onChange={(e) => setCategoryName(e.target.value)}
                        disabled 
                    />
                </div>
                <Button type="submit">Delete</Button>
            </form>
        </DialogContent>
    </Dialog>
  )
}