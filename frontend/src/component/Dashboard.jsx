// src/component/Dashboard.jsx (Đã sửa cho Bootstrap)
import React, { useState } from "react";
import Sidebar, { menuItems } from "./Sidebar"; 
import DriverManagement from "./DriverManagement"; 
import CustomerManagement from "./CustomerManagement";
import TripManagement from "./TripManagement";

const DefaultPage = ({ label }) => (
    <div className="p-5 text-center bg-white rounded-3 shadow-sm">
        <h1 className="display-6">{label}</h1>
        <p className="lead mt-4">Đây là trang quản lý {label.toLowerCase().replace('quản lý ', '')}.</p>
    </div>
);

const componentMap = {
    home: () => <DefaultPage label="Trang Chủ" />,
    drivers: DriverManagement,      
    customers: CustomerManagement,  
    trips: TripManagement,          
    vehicles: () => <DefaultPage label="Phương tiện" />,
    'vehicle-types': () => <DefaultPage label="Loại phương tiện" />,
    'price-list': () => <DefaultPage label="Bảng giá" />,
    maintenance: () => <DefaultPage label="Bảo trì xe" />,
};

const Dashboard = () => {
    const [activePath, setActivePath] = useState(menuItems[0].path);
    const CurrentComponent = componentMap[activePath];

    return (
        // Sử dụng d-flex của Bootstrap
        <div className="d-flex">
            {/* Cột 1: Sidebar (Menu) */}
            <Sidebar 
                onMenuClick={setActivePath} 
                activePath={activePath}     
             
            />

            {/* Cột 2: Nội dung trang */}
            {/* flex-grow-1, bg-light, min-vh-100 */}
            <main className="flex-grow-1 p-4 overflow-y-auto min-vh-100 bg-light">
                {/* Tiêu đề trang (Bootstrap typography) */}
                <h1 className="h2 fw-bold mb-4 text-dark">
                    {menuItems.find(item => item.path === activePath)?.label}
                </h1>
                
                {/* Render Component tương ứng */}
                <CurrentComponent />
            </main>
        </div>
    );
};

export default Dashboard;