import * as React from "react"
import {
  ChartNoAxesCombinedIcon,
  Home,
  Inbox,
  Soup,
  UtensilsCrossed,
} from "lucide-react"

import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"
import { Link } from "react-router-dom"


const items = [
  {
    title: "Home",
    url: "/",
    icon: Home,
  },
  {
    title: "Transaksi",
    url: "/transaksi",
    icon: ChartNoAxesCombinedIcon,
  },
  {
    title: "Category",
    url: "/category",
    icon: Inbox,
  },
  {
    title: "Product",
    url: "/product",
    icon: Soup,
  }
]

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar className="border-r-0" {...props}>
       <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton size="lg" asChild>
              <a href="#">
                <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-10 items-center justify-center rounded-lg">
                  <UtensilsCrossed className="size-7" />
                </div>
                <div className="flex flex-col gap-2 leading-none">
                  <span className="font-medium">FOOD-POS</span>
                </div>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
     <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu className="flex flex-col gap-5 pl-3">
              {items.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton asChild>
                    <Link to={item.url}>
                      <item.icon style={{ width: 21, height: 21 }}/>
                      <span>{item.title}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  )
}
