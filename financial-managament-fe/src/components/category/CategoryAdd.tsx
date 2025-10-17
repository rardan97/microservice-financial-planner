import React, { useState } from "react";
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
import { addCategories } from "@/api/CategoryApi";
import type { CategoryDto } from "@/interface/Category.interface";
import { Alert, AlertDescription, AlertTitle } from "../ui/alert";
import { AlertCircleIcon } from "lucide-react";


interface Errors {
    categoryName: string;
    categoryType: string;
}

export default function CategoryAdd({ onSuccess }: { onSuccess: () => void }) {

    
    const [categoryName, setCategoryName] = useState<string>("");
    const [categoryType, setCategoryType] = useState<string>("");
    const [errorsAll, setErrorsAll] = useState<string>("");

    
    const { isOpen, setIsOpen, openModal, closeModal } = useModal();

    // const [open, setOpen] = React.useState(false)
//   const isDesktop = useMediaQuery("(min-width: 768px)")

    const [errors, setErrors] = useState<Errors>({
        categoryName: '',
        categoryType: ''
    });
    
    function validateForm(): boolean{
        console.log("proccess validation");
        let valid = true;
        const errorsCopy = {... errors}
        if(categoryName.trim()){
            errorsCopy.categoryName = '';
        }else{
            errorsCopy.categoryName = 'categoryName is required';
            valid = false;
        }
    
        if(categoryType.trim()){
            errorsCopy.categoryType = '';
        }else{
            errorsCopy.categoryType = 'categoryType is required';
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
                const newCategory: CategoryDto = {
                    categoryName,
                    categoryType,
                };
            
                const result = await addCategories(token, newCategory);
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
           <Button variant="outline" onClick={openModal}>Add Category</Button>
         </DialogTrigger>
         <DialogContent className="sm:max-w-[425px]" >
           <DialogHeader>
             <DialogTitle>Add Category</DialogTitle>
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
                        onChange={(e) => setCategoryType(e.target.value)}
                    />
                </div>
                <Button type="submit">Save changes</Button>
            </form>
         </DialogContent>
       </Dialog>
  )
}