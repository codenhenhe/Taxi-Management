import { Routes, Route } from "react-router-dom";
import AuthLayout from "../layouts/AuthLayout";
import AdminLayout from "../layouts/AdminLayout";
import LoginPage from "../pages/LoginPage";
import DashboardPage from "../pages/DashboardPage";
import NotFoundPage from "../pages/NotFoundPage";
import ProtectedRoute from "./ProtectedRoute";
import VehicleTypesPage from "../pages/VehicleTypesPage";
import DriversPage from "../pages/DriversPage";
import VehiclesPage from "../pages/VehiclesPage";
import TripsPage from "../pages/TripsPage";
import CustomersPage from "../pages/CustomersPage";
import DispatchPage from "../pages/DispatchPage";
import MaintenancePage from "../pages/MaintenancePage";
import PricingPage from "../pages/PricingPage";

export default function AppRoutes() {
  return (
    <Routes>
      {/* Trang công khai */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
      </Route>

      {/* Trang admin - cần login */}
      <Route element={<ProtectedRoute />}>
        <Route element={<AdminLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/vehicle-types" element={<VehicleTypesPage />} />
          <Route path="/drivers" element={<DriversPage />} />
          <Route path="/vehicles" element={<VehiclesPage />} />
          <Route path="/pricing" element={<PricingPage />} />
          <Route path="/maintenance" element={<MaintenancePage />} />
          <Route path="/customers" element={<CustomersPage />} />
          <Route path="/dispatch" element={<DispatchPage />} />
          <Route path="/trips" element={<TripsPage />} />
        </Route>
      </Route>

      {/* 404 */}
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
