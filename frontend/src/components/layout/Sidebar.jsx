import React, { useState } from "react";
import { useAuthStore } from "../../store/authStore";
import {
  MdMenuOpen,
  MdShapeLine,
  MdOutlineAssignmentInd,
} from "react-icons/md";
import { FaHome, FaCarSide, FaUsers, FaChartBar } from "react-icons/fa";
import { FaUserTie } from "react-icons/fa6";
import { LuClipboardList } from "react-icons/lu";
import { GrConfigure } from "react-icons/gr";
import { NavLink, useNavigate } from "react-router-dom";
import { BiTrip } from "react-icons/bi";
import { FiLogOut } from "react-icons/fi";

const menuItems = [
  // {
  //   icons: <FaHome size={25} />,
  //   label: "Trang chủ",
  //   path: "/",
  // },
  {
    icons: <FaUserTie size={25} />,
    label: "Tài xế",
    path: "/drivers",
  },
  // {
  //   icons: <MdOutlineAssignmentInd size={25} />,
  //   label: "Phân công xe",
  //   path: "/dispatch",
  // },
  {
    icons: <FaUsers size={25} />,
    label: "Khách hàng",
    path: "/customers",
  },
  {
    icons: <FaCarSide size={25} />,
    label: "Phương tiện",
    path: "/vehicles",
  },
  {
    icons: <MdShapeLine size={25} />,
    label: "Loại phương tiện",
    path: "/vehicle-types",
  },
  {
    icons: <BiTrip size={25} />,
    label: "Chuyến đi",
    path: "/trips",
  },
  {
    icons: <LuClipboardList size={25} />,
    label: "Bảng giá",
    path: "/pricing",
  },
  {
    icons: <GrConfigure size={25} />,
    label: "Bảo trì phương tiện",
    path: "/maintenance",
  },
  {
    icons: <FaChartBar size={25} />,
    label: "Thống kê",
    path: "/statistic",
  },
];

const Sidebar = () => {
  const [open, setOpen] = useState(true);
  const navigate = useNavigate();
  const logout = useAuthStore((state) => state.logout);

  const handleLogout = (e) => {
    e.preventDefault();
    logout();
    navigate("/login");
  };

  return (
    <nav
      className={`bg-blue-500 text-white shadow-md h-full p-2 duration-500 flex flex-col ${
        open ? "w-60" : "w-17"
      }`}
    >
      <div className="px-3 py-2 h-20 flex justify-between items-center">
        <div className="flex justify-between items-center gap-2">
          <img
            src="./taxi.svg"
            alt="logo"
            className={`${open ? "w-7" : "w-0"} rounded-md`}
          />
          <p className={`${!open && "hidden"} duration-500 `}>TaxiWorld</p>
        </div>
        <MdMenuOpen
          size={30}
          className={`${
            !open && "rotate-180"
          } duration-300 cursor-pointer rounded-md hover:bg-blue-800`}
          onClick={() => setOpen(!open)}
        />
      </div>

      <ul>
        {menuItems.map((item, index) => {
          return (
            <li key={index}>
              <NavLink
                to={item.path}
                className={({ isActive }) =>
                  `px-3 my-1 py-2 hover:bg-blue-800 rounded-md duration-300 cursor-pointer flex gap-4 items-center relative group ${
                    isActive && "bg-blue-800"
                  }`
                }
              >
                <div>{item.icons}</div>
                <p
                  className={`${
                    open ? "opacity-100 w-auto" : "opacity-0 w-0 translate-x-24"
                  } whitespace-nowrap overflow-hidden transition-all duration-300`}
                >
                  {item.label}
                </p>
                <p
                  className={`${
                    open && "hidden"
                  }  absolute left-20 shadow-md rounded-md whitespace-nowrap w-0 p-0 z-100 overflow-hidden group-hover:bg-blue-950 group-hover:w-fit group-hover:p-2 group-hover:left-16`}
                >
                  {item.label}
                </p>
              </NavLink>
            </li>
          );
        })}
      </ul>

      {/* Footer: Logo + Admin */}
      <div
        className="border-t border-blue-600 p-2 mt-auto
"
      >
        <div className="flex items-center gap-3 min-w-0">
          <div className="shrink-0">
            <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-lg ring-2 ring-blue-400 transition-all duration-500">
              <span className="text-blue-600 font-bold text-lg">A</span>
            </div>
          </div>

          {/* Tên Admin*/}
          <div
            className={`flex flex-col transition-all duration-500 ease-in-out overflow-hidden ${
              open ? "opacity-100 w-auto max-w-xs" : "opacity-0 w-0"
            }`}
          >
            <span className="font-semibold text-sm whitespace-nowrap">
              Admin
            </span>
            <span className="text-xs text-blue-100 whitespace-nowrap">
              Quản trị viên
            </span>
          </div>

          {/* Nút Logout với hiệu ứng hover - Kiểu tooltip */}
          <div
            className={`${
              open ? "opacity-100 w-auto max-w-xs" : "opacity-0 w-0"
            } flex justify-center items-center ml-auto`}
          >
            <button
              onClick={handleLogout}
              className="group relative cursor-pointer flex items-center p-2 rounded-lg transition-all duration-300 hover:bg-blue-800 hover:text-white"
            >
              <FiLogOut size={20} className="shrink-0" />
              <span
                className="absolute left-full top-1/2 -translate-y-1/2 ml-2
                   p-2 rounded-lg bg-blue-800 text-white 
                   whitespace-nowrap 
                   opacity-0 scale-95 group-hover:opacity-100 group-hover:scale-100
                   transition-all duration-300 pointer-events-none"
              >
                Đăng xuất
              </span>
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Sidebar;
