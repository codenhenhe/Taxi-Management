import AppRoutes from "./routes";
import { Toaster } from "react-hot-toast";

export default function App() {
  return (
    <>
      <AppRoutes />;
      <Toaster
        position="bottom-right"
        toastOptions={{
          // Thời gian hiển thị mặc định
          duration: 3000,
        }}
      />
    </>
  );
}
