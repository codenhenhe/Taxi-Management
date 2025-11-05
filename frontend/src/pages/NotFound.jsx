import React from "react";
import { useNavigate } from "react-router-dom";

const NotFound = () => {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col gap-2 items-center justify-center">
      <h1 className="font-bold">404 | Page Not Found</h1>
      <button
        onClick={() => navigate("/")}
        className="cursor-pointer bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full"
      >
        Về trang chủ
      </button>
    </div>
  );
};

export default NotFound;
