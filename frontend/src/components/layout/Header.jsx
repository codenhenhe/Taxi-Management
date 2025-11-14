import { useAuthStore } from "../../store/authStore";

export default function Header() {
  const user = useAuthStore((state) => state.user);

  return (
    <header className="bg-white shadow-sm px-6 py-4 flex justify-between items-center">
      <h1 className="text-xl font-semibold">Quản lý Taxi</h1>
      <div className="flex items-center space-x-3">
        <span className="text-sm">Xin chào, {user?.ho_ten || user?.email}</span>
      </div>
    </header>
  );
}
