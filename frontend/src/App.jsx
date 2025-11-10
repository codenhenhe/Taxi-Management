<<<<<<< HEAD
// import { useState } from "react";
// import reactLogo from "./assets/react.svg";
// import viteLogo from "/vite.svg";
// import "./App.css";
import Sidebar from "./components/Sidebar.jsx";
import {
  Route,
  createBrowserRouter,
  createRoutesFromElements,
  RouterProvider,
} from "react-router-dom";
import VehicleTypes, { VehicleTypesLoader } from "./pages/VehicleTypes.jsx";
import RootLayout from "./layout/RootLayout.jsx";
import Home from "./pages/Home.jsx";
import NotFound from "./pages/NotFound.jsx";
import VehicleTypesLayout from "./layout/VehicleTypesLayout.jsx";

const App = () => {
  const router = createBrowserRouter(
    createRoutesFromElements(
      <Route path="/" element={<RootLayout />}>
        <Route index element={<Home />} />

        <Route path="vehicles" element={<VehicleTypes />} />

        <Route
          path="vehicle-types"
          element={<VehicleTypesLayout />}
          loader={VehicleTypesLoader}
        >
          <Route index element={<VehicleTypes />} />
        </Route>

        <Route path="price-list" element={<VehicleTypes />} />
        <Route path="maintenance" element={<VehicleTypes />} />
        <Route path="*" element={<NotFound />} />
      </Route>
    )
  );

  return <RouterProvider router={router} />;
};

export default App;
=======
// src/App.jsx
import React from 'react';
import Dashboard from './component/Dashboard'; // Giả sử Dashboard.jsx nằm trong components

function App() {
  return (
    // Xóa hết nội dung cũ và thay bằng Dashboard
    <Dashboard />
  );
}

export default App;
>>>>>>> ecbde1844b625dd610b480ad957a790f9b080f64
