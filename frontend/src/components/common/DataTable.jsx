// src/components/common/DataTable.jsx
export default function DataTable({
  data,
  columns,
  loading,
  error,
  onRowClick,
}) {
  if (loading) return <div className="p-8 text-center">Đang tải...</div>;
  if (error) return <div className="p-8 text-center text-red-600">{error}</div>;
  if (!data || data.length === 0)
    return (
      <div className="p-8 text-center text-gray-500">Không có dữ liệu.</div>
    );

  return (
    <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
      <h3 className="text-lg font-semibold text-blue-600 p-4 border-b bg-blue-50">
        DANH SÁCH
      </h3>

      <div className="overflow-x-auto overflow-y-auto max-h-[600px]">
        <table className="w-full">
          <thead className="text-sm text-center">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.key}
                  className="sticky top-0 px-4 py-2 bg-cyan-100"
                >
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y text-center">
            {data.map((item) => (
              <tr
                key={item[columns[0]?.key] || item.id}
                className="hover:bg-gray-200 duration-150 cursor-pointer"
                onClick={() => onRowClick(item)}
              >
                {columns.map((col) => (
                  <td key={col.key} className="px-4 py-2">
                    {col.render ? col.render(item) : item[col.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
