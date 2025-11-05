// import React from "react";
// import { Outlet, useLoaderData } from "react-router-dom";

// const VehicleTypesLayout = () => {
//   const typeData = useLoaderData();
//   return (
//     <div>
//       <h2>Quản lý loại phương tiện</h2>
//       <main>
//         <Outlet context={{ typeData }} />
//       </main>
//     </div>
//   );
// };

// export default VehicleTypesLayout;

// layout/VehicleTypesLayout.jsx
import { Outlet, useLoaderData } from "react-router-dom";

export default function VehicleTypesLayout() {
  const typeData = useLoaderData();

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Quản lý loại phương tiện</h1>
        <p>Quản lý các loại phương tiện trong hệ thống</p>
      </div>

      <Outlet context={{ typeData }} />
    </div>
  );
}
