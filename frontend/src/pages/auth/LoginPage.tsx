import { Activity, Dumbbell } from "lucide-react";
import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../app/providers";

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [username, setUsername] = useState("student001");
  const [password, setPassword] = useState("password123");
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);
    try {
      const user = await login(username, password);
      navigate(user.role === "TEACHER" ? "/teacher/dashboard" : "/student/dashboard");
    } catch (err) {
      setError(err instanceof Error ? err.message : "登录失败");
    }
  }

  return (
    <div className="login-card">
      <div className="login-heading">
        <div className="brand-mark large">
          <Dumbbell size={28} />
        </div>
        <div>
          <p>校园运动健康平台</p>
          <h1>AI 体育学习平台</h1>
        </div>
      </div>
      <form onSubmit={handleSubmit} className="form-grid">
        <label>
          用户名
          <input value={username} onChange={(event) => setUsername(event.target.value)} />
        </label>
        <label>
          密码
          <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        </label>
        {error && <div className="form-error">{error}</div>}
        <button className="primary-button" type="submit">
          <Activity size={18} />
          进入工作台
        </button>
      </form>
      <div className="demo-accounts">
        <span>学生：student001 / password123</span>
        <span>教师：teacher001 / password123</span>
      </div>
    </div>
  );
}

