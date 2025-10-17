import CategoryList from "@/components/category/CategoryList";
import { Card } from "@/components/ui/card";

export default function Category() {
    return (
        <>
            <Card className="m-9 p-9">
                <CategoryList />
            </Card>
        </>
    );
}
