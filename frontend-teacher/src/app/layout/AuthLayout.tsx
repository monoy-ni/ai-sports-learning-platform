import { Outlet } from "react-router-dom";

export function AuthLayout() {
  return (
    <main className="auth-shell">
      <section className="auth-panel">
        <Outlet />
      </section>
    </main>
  );
}

