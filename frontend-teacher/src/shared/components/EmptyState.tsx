import { Inbox } from "lucide-react";

export function EmptyState({ title = "暂无数据", description = "完成对应操作后会在这里显示。" }) {
  return (
    <div className="state-box">
      <Inbox size={28} />
      <strong>{title}</strong>
      <span>{description}</span>
    </div>
  );
}

