// src/utils/exportExcel.js
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

/**
 * Hàm xuất dữ liệu ra file Excel
 * @param {Array} data - Mảng dữ liệu JSON cần xuất
 * @param {String} fileName - Tên file (không cần đuôi .xlsx)
 * @param {String} sheetName - Tên sheet (mặc định là "Sheet1")
 */
export const exportToExcel = (data, fileName, sheetName = "Sheet1") => {
  try {
    // 1. Tạo WorkBook và WorkSheet
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.json_to_sheet(data);

    // 2. Thêm Sheet vào Book
    XLSX.utils.book_append_sheet(wb, ws, sheetName);

    // 3. Xuất file Buffer
    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });

    // 4. Tạo Blob và tải về
    const dataBlob = new Blob([excelBuffer], {
      type: "application/octet-stream",
    });
    saveAs(
      dataBlob,
      `${fileName}_${new Date().toISOString().slice(0, 10)}.xlsx`
    );

    return true;
  } catch (error) {
    console.error("Export Error:", error);
    return false;
  }
};
