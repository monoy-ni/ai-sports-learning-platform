import { AlertCircle } from "lucide-react";

export function ErrorState({ message }: { message: string }) {
  return (
    <div className="state-box danger">
      <AlertCircle size={28} />
      <strong>加载失败</strong>
      <span>{message}</span>
    </div>
  );
}

