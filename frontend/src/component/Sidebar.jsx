// src/component/Sidebar.jsx (Đã sửa cho Bootstrap)
import React, { useState } from "react";
import { MdMenuOpen, MdShapeLine } from "react-icons/md";
import { FaHome, FaCarSide, FaUserFriends, FaClipboardList, FaUsers, FaMapSigns } from "react-icons/fa";
import { LuClipboardList } from "react-icons/lu";
import { GrConfigure } from "react-icons/gr";

export const menuItems = [
    { icons: <FaHome size={25} />, label: "Trang chủ", path: "home" },
    { icons: <FaUsers size={25} />, label: "Quản lý TÀI XẾ", path: "drivers" },
    { icons: <FaUserFriends size={25} />, label: "Quản lý KHÁCH HÀNG", path: "customers" },
    { icons: <FaMapSigns size={25} />, label: "Quản lý CHUYẾN ĐI", path: "trips" },
    { icons: <FaCarSide size={25} />, label: "Phương tiện", path: "vehicles" },
    { icons: <MdShapeLine size={25} />, label: "Loại phương tiện", path: "vehicle-types" },
    { icons: <LuClipboardList size={25} />, label: "Bảng giá", path: "price-list" },
    { icons: <GrConfigure size={25} />, label: "Bảo trì xe", path: "maintenance" },
];

const Sidebar = ({ onMenuClick, activePath }) => {
    const [open, setOpen] = useState(true);

    // Sử dụng style cho chiều rộng và transition
    const navStyle = {
        width: open ? '230px' : '70px',
        transition: 'width 0.3s ease-in-out',
    };

    const logoStyle = {
        width: open ? 'auto' : '0',
        opacity: open ? 1 : 0,
        transition: 'opacity 0.2s, width 0.3s',
        maxWidth: '35px',
    };

    const textStyle = {
        opacity: open ? 1 : 0,
        width: open ? 'auto' : '0',
        whiteSpace: 'nowrap',
        overflow: 'hidden',
        transition: 'opacity 0.2s, width 0.3s',
    };

    return (
        <nav
            className="bg-primary text-white shadow min-vh-100 p-2 d-flex flex-column"
            style={navStyle}
        >
            <div className="px-2 py-2 mb-3 d-flex justify-content-between align-items-center">
                <img
                    src="./taxi.svg"
                    alt="logo"
                    className="rounded-3"
                    style={logoStyle}
                />
                <div
                    className="p-1 rounded-3"
                    style={{ cursor: 'pointer' }}
                    onClick={() => setOpen(!open)}
                >
                    <MdMenuOpen size={30} />
                </div>
            </div>

            <ul className="nav nav-pills flex-column">
                {menuItems.map((item) => (
                    <li className="nav-item my-1" key={item.path}>
                        <a
                            href="#"
                            className={`nav-link text-white d-flex align-items-center gap-3 p-2 ${
                                activePath === item.path ? "active" : ""
                            }`}
                            onClick={(e) => {
                                e.preventDefault();
                                onMenuClick(item.path);
                            }}
                            title={!open ? item.label : ''} // Tooltip khi đóng
                        >
                            <div>{item.icons}</div>
                            <span style={textStyle}>{item.label}</span>
                        </a>
                    </li>
                ))}
            </ul>
        </nav>
    );
};

export default Sidebar;