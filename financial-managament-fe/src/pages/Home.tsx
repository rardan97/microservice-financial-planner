import { ChartAreaInteractive } from "@/components/home/ChartAreaInteractive";
import { SectionCards } from "@/components/home/SectionCards";
import { Card } from "@/components/ui/card";



export default function Home() {
    return (
        <>
            <Card className="m-9 p-9">
                <div className="flex flex-1 flex-col">
                    <div className="@container/main flex flex-1 flex-col gap-2">
                        <div className="flex flex-col gap-4 py-4 md:gap-6 md:py-6">
                        <SectionCards />
                        <div className="px-4 lg:px-6">
                            <ChartAreaInteractive />
                        </div>
                        {/* <DataTable data={data} /> */}
                        </div>
                    </div>
                    </div>
                <div>
                    <h1>Hello</h1>
                </div>
            </Card>
            
        </>
    );
}
