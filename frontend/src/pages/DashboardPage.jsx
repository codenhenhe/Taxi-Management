// src/pages/DashboardPage.jsx
export default function DashboardPage() {
  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold">Tổng xe</h3>
          <p className="text-3xl font-bold text-blue-600">48</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold">Chuyến hôm nay</h3>
          <p className="text-3xl font-bold text-green-600">23</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-lg font-semibold">Doanh thu</h3>
          <p className="text-3xl font-bold text-red-600">12.5M</p>
        </div>
      </div>
    </div>
  );
}
