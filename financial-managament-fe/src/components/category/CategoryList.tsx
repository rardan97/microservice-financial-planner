import { useCallback, useEffect, useRef, useState } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "../ui/table";
import { Card, CardAction, CardContent, CardHeader, CardTitle } from "../ui/card";
import type { Category } from "@/interface/Category.interface";
import { getListCategories } from "@/api/CategoryApi";
import CategoryAdd from "./CategoryAdd";
import CategoryEdit from "./CategoryEdit";
import CategoryDelete from "./CategoryDelete";
import { Pagination, PaginationContent, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from "../ui/pagination";


export default function CategoryList() {
    const hasFetched = useRef(false);
    const [categoryData, setCategoryData] = useState<Category[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 5;
    const totalPages = Math.max(1, Math.ceil(categoryData.length / itemsPerPage));
    
    const paginatedData = categoryData.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );
    
    const getListAllCategory = useCallback(async (): Promise<void> => {
        const token = localStorage.getItem("accessToken");
        console.log(token);
        if (!token){
            return;
        }
        try {
            const response = await getListCategories(token);
            if(response && response.data){
                console.log("Success processing data");
                setCategoryData(response.data);
            }
        } catch (error) {
            console.log("Failed processing data", error);
            throw error;
        }
    }, []);

     const handlePrevious = (e: React.MouseEvent) => {
        if (currentPage === 1) return;
        e.preventDefault();
        setCurrentPage((prev) => Math.max(prev - 1, 1));
    };

    const handleNext = (e: React.MouseEvent) => {
        if (currentPage === totalPages) return;
        e.preventDefault();
        setCurrentPage((prev) => Math.min(prev + 1, totalPages));
    };

    useEffect(() => {
        console.log(hasFetched);
        if (!hasFetched.current) {
            getListAllCategory();
            hasFetched.current = true; // Cegah request kedua
        }
    }, [getListAllCategory]);

    return (
        <>
            <div>
                <Card className="m-9 p-9">
                    <CardHeader>
                        <CardTitle>Data Product</CardTitle>
                        <CardAction><CategoryAdd onSuccess={getListAllCategory}/></CardAction>
                    </CardHeader>
                    <CardContent>
                        {/* DESKTOP TABLE */}
                        <div className="hidden lg:block">
                            <Table>
                            <TableHeader>
                                <TableRow>
                                <TableHead className="w-[100px]">Category ID</TableHead>
                                <TableHead>Category Name</TableHead>
                                <TableHead>Category Desk</TableHead>
                                <TableHead className="text-right">Action</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {paginatedData.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={7} className="text-center text-gray-500">
                                    No category found.
                                    </TableCell>
                                </TableRow>
                                ) : (
                                paginatedData.map((category) => (
                                    <TableRow key={category.categoryId}>
                                    <TableCell className="font-medium">{category.categoryId}</TableCell>
                                    <TableCell>{category.categoryName}</TableCell>
                                    <TableCell>{category.categoryType}</TableCell>
                                    <TableCell className="text-right">
                                        <div className="flex justify-end items-center gap-2">
                                        <CategoryEdit
                                            onSuccess={getListAllCategory}
                                            idCat={category.categoryId as number}
                                        />
                                        <CategoryDelete
                                            onSuccess={getListAllCategory}
                                            idCat={category.categoryId as number}
                                        />
                                        </div>
                                    </TableCell>
                                    </TableRow>
                                ))
                                )}
                            </TableBody>
                            </Table>
                        </div>

                        {/* MOBILE CARD VERSION */}
                        <div className="lg:hidden space-y-4">
                            {paginatedData.length === 0 ? (
                            <p className="text-center text-gray-500">No products found.</p>
                            ) : (
                            paginatedData.map((category) => (
                                <div key={category.categoryId} className="border rounded p-4 shadow">
                                <div className="mb-2">
                                    <strong>Product ID:</strong> {category.categoryId}
                                </div>
                                <div className="mb-2">
                                    <strong>Product Name:</strong> {category.categoryName}
                                </div>
                                <div className="mb-2">
                                    <strong>Description:</strong> {category.categoryType}
                                </div>
                                <div className="flex justify-end gap-2">
                                    <CategoryEdit
                                    onSuccess={getListAllCategory}
                                    idCat={category.categoryId as number}
                                    />
                                    <CategoryDelete
                                    onSuccess={getListAllCategory}
                                    idCat={category.categoryId as number}
                                    />
                                </div>
                                </div>
                            ))
                            )}
                        </div>
                        <div className="mt-4">
                            <Pagination>
                                <PaginationContent>
                                    <PaginationItem>
                                        <PaginationPrevious 
                                            href="#" 
                                            onClick={handlePrevious}
                                            aria-disabled={currentPage === 1}
                                            className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
                                            />
                                    </PaginationItem>
                                    {Array.from({ length: totalPages }, (_, index) => (
                                        <PaginationItem key={index}>
                                            <PaginationLink 
                                                href="#"
                                                isActive={currentPage === index + 1}
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    setCurrentPage(index + 1);
                                                }}
                                            >
                                            {index + 1}
                                            </PaginationLink>
                                        </PaginationItem>
                                    ))}
                                    <PaginationItem>
                                        <PaginationNext 
                                            href="#" 
                                            onClick={handleNext}
                                            aria-disabled={currentPage === totalPages}
                                            className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
                                            />
                                    </PaginationItem>
                                </PaginationContent>
                            </Pagination>
                            <p className="text-sm text-gray-500 text-center mt-2">
                            Page {currentPage} of {totalPages}
                            </p>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </>
    );
}