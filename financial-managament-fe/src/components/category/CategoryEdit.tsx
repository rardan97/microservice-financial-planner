import React, { useCallback, useEffect, useState } from "react";
import { cn } from "@/lib/utils"
// import { useMediaQuery } from "@/hooks/use-media-query"
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
import { editCategories, getCategoryValueById } from "@/api/CategoryApi";
import type { Category } from "@/interface/Category.interface";
import { Alert, AlertDescription, AlertTitle } from "../ui/alert";
import { AlertCircleIcon } from "lucide-react";


type CategoryEditProps = {
    onSuccess: () => void;
    idCat: number;
};


interface Errors {
    categoryName: string;
    categoryType: string;
}

export default function CategoryEdit({onSuccess, idCat} : CategoryEditProps) {
    const { isOpen, setIsOpen, openModal, closeModal } = useModal();

    const [categoryId, setCategoryId] = useState<number>();  
    const [categoryName, setCategoryName] = useState<string>("");
    const [categoryType, setCategoryType] = useState<string>("");
    const [errorsAll, setErrorsAll] = useState<string>("");

    
    

    // const [open, setOpen] = React.useState(false)
//   const isDesktop = useMediaQuery("(min-width: 768px)")

    const [errors, setErrors] = useState<Errors>({
        categoryName: '',
        categoryType: ''
    });

    const getCategory = useCallback(async (): Promise<void> => {
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
                setCategoryType(response.data.categoryType);
              }
        } catch (error) {
            console.log("Failed processing data", error);
            throw error;
        }
    }, [idCat]);
    
    useEffect(() => {
            if (isOpen) {
                getCategory();
            }
    }, [isOpen, getCategory]);


    
    function validateForm(): boolean{
        console.log("proccess validation");
        let valid = true;
        const errorsCopy = {... errors}
        if(categoryName.trim()){
            errorsCopy.categoryName = '';
        }else{
            errorsCopy.categoryName = 'category name is required';
            valid = false;
        }
    
        if(categoryType.trim()){
            errorsCopy.categoryType = '';
        }else{
            errorsCopy.categoryType = 'category type is required';
            valid = false;
        }
        setErrors(errorsCopy);
        return valid;
    }

    const handleSave = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const token = localStorage.getItem("accessToken");
        if (!token) {
            return;
        }
        if (validateForm()) {
            console.log("success validation");
            try {
                 if (categoryId === undefined) {
                    throw new Error("categoryId is undefined");
                }
                const newCategory: Category = {
                    categoryId,
                    categoryName,
                    categoryType,
                };

                
                const result = await editCategories(token, categoryId, newCategory);
                if(result){
                    console.log("success add data", result);
                    setCategoryName("");
                    setCategoryType("");
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
        }

        console.log("Saving changes...");
        closeModal();
    };


  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
         <DialogTrigger asChild>
           <Button variant="outline" onClick={openModal}>Edit</Button>
         </DialogTrigger>
         <DialogContent className="sm:max-w-[425px]" >
           <DialogHeader>
             <DialogTitle>Edit Category</DialogTitle>
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
                    />
                    {errors.categoryName && <p className="text-red-500 text-sm">{errors.categoryName}</p>}
                </div>
                <div className="grid gap-3">
                    <Label htmlFor="categoryType">Category Type</Label>
                    <Input 
                        id="categoryType" 
                        type="text" 
                        value={categoryType}
                        onChange={(e) => setCategoryType(e.target.value)}/>
                </div>
                <Button type="submit">Save changes</Button>
            </form>
         </DialogContent>
       </Dialog>
  )
}