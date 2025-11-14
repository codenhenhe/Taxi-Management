import { create } from "zustand";
import { persist } from "zustand/middleware";
// import jwtDecode from 'jwt-decode'; // Có thể dùng nếu cần trích xuất thông tin từ token

export const useAuthStore = create(
  persist(
    (set, get) => ({
      // 1. Chỉ lưu token và đặt user là null (hoặc đối tượng rỗng)
      user: null,
      token: null,

      login: (data) => {
        // Back-end chỉ trả về {token: "..."}
        if (data.token) {
          // Lựa chọn 1: Chỉ lưu token và giữ user là null (Nếu không cần hiển thị tên)
          set({ token: data.token, user: null });

          // Lựa chọn 2: (Khuyến nghị) Trích xuất thông tin cơ bản từ token nếu nó chứa payload
          /*
            try {
                const decodedToken = jwtDecode(data.token);
                set({ 
                    token: data.token, 
                    user: { username: decodedToken.sub, roles: decodedToken.roles } // Lấy thông tin từ payload JWT
                });
            } catch (error) {
                 set({ token: data.token, user: null }); // Vẫn lưu token nếu giải mã lỗi
            }
            */
        }
      },

      logout: () => {
        set({ user: null, token: null });
      },

      // 2. Xác định trạng thái đăng nhập chỉ dựa vào sự tồn tại của token
      isAuthenticated: () => !!get().token,
    }),
    {
      name: "taxi-admin-auth", // Tên key trong localStorage
    }
  )
);
