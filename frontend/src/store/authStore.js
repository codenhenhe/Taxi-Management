// src/store/authStore.js
import { create } from "zustand";
import { persist } from "zustand/middleware";

// export const useAuthStore = create(
//   persist(
//     (set, get) => ({
//       user: null,
//       token: null,

//       login: (data) => {
//         set({
//           user: data.user || { email: data.email },
//           token: data.token,
//         });
//       },

//       logout: () => {
//         set({ user: null, token: null });
//       },

//       isAuthenticated: () => !!get().token,
//     }),
//     {
//       name: "taxi-admin-auth",
//     }
//   )
// );

// GIẢ LẬP USER (tạm thời)
export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: { email: "admin@taxi.com", ho_ten: "Quản trị viên" },
      token: "fake-token-123",

      login: (data) => set({ user: data.user, token: data.token }),
      logout: () => set({ user: null, token: null }),
      isAuthenticated: () => true, // Luôn trả về true
    }),
    {
      name: "taxi-admin-auth",
    }
  )
);
