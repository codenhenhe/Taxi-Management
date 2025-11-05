import React, { useState } from "react";
import { MdMenuOpen, MdShapeLine } from "react-icons/md";
import { FaHome, FaCarSide } from "react-icons/fa";
import { LuClipboardList } from "react-icons/lu";
import { GrConfigure } from "react-icons/gr";
import { NavLink } from "react-router-dom";

const menuItems = [
  {
    icons: <FaHome size={25} />,
    label: "Trang chủ",
    path: "/",
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
    icons: <LuClipboardList size={25} />,
    label: "Bảng giá",
    path: "/price-list",
  },
  {
    icons: <GrConfigure size={25} />,
    label: "Bảo trì phương tiện",
    path: "/maintenance",
  },
];

const Sidebar = () => {
  const [open, setOpen] = useState(true);

  return (
    <nav
      className={`bg-blue-500 text-white shadow-md h-screen p-2 duration-500 flex flex-col ${
        open ? "w-60" : "w-16"
      }`}
    >
      <div className="px-3 py-2 h-20 flex justify-between items-center">
        <img
          src="./taxi.svg"
          alt="logo"
          className={`${open ? "w-7" : "w-0"} rounded-md`}
        />
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
                  `px-3 my-2 py-2 hover:bg-blue-800 rounded-md duration-300 cursor-pointer flex gap-4 items-center relative group ${
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
                  }  absolute left-20 shadow-md rounded-md whitespace-nowrap w-0 p-0 overflow-hidden group-hover:w-fit group-hover:p-2 group-hover:left-16`}
                >
                  {item.label}
                </p>
              </NavLink>
            </li>
          );
        })}
      </ul>
    </nav>
  );
};

export default Sidebar;
