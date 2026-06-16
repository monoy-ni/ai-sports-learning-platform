import { LucideIcon, LogOut, ShieldCheck } from "lucide-react";
import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../app/providers";

type NavItem = {
  to: string;
  label: string;
  icon: LucideIcon;
};

type AppShellProps = {
  title: string;
  subtitle: string;
  navItems: NavItem[];
  role: "STUDENT" | "TEACHER";
  brandIcon?: LucideIcon;
  children: React.ReactNode;
};

export function AppShell({ title, subtitle, navItems, role, brandIcon: BrandIcon = ShieldCheck, children }: AppShellProps) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">
            <BrandIcon size={22} />
          </div>
          <div>
            <strong>AI 体育</strong>
            <span>{role === "STUDENT" ? "Student" : "Teacher"}</span>
          </div>
        </div>
        <nav className="nav-list">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink key={item.to} to={item.to} className={({ isActive }) => `nav-item ${isActive ? "active" : ""}`}>
                <Icon size={18} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
        <button
          className="ghost-button"
          type="button"
          onClick={() => {
            logout();
            navigate("/login");
          }}
        >
          <LogOut size={17} />
          退出登录
        </button>
      </aside>
      <div className="workspace">
        <header className="topbar">
          <div>
            <h1>{title}</h1>
            <p>{subtitle}</p>
          </div>
          <div className="user-chip">
            <span>{user?.displayName ?? "本地演示用户"}</span>
            <strong>{user?.role ?? role}</strong>
          </div>
        </header>
        <main className="content">{children}</main>
      </div>
    </div>
  );
}

