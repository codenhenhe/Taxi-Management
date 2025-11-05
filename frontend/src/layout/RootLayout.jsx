import Reat from "react";
import Sidebar from "../components/Sidebar";
import { Outlet } from "react-router-dom";

const RootLayout = () => {
  return (
    <div className="flex min-h-screen">
      <Sidebar />
      <main className="flex-1 p-6 flex items-center justify-center">
        <Outlet />
      </main>
    </div>
  );
};

export default RootLayout;
