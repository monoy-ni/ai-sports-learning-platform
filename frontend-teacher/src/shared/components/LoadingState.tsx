export function LoadingState({ label = "正在加载" }: { label?: string }) {
  return (
    <div className="state-box">
      <div className="loader" />
      <strong>{label}</strong>
    </div>
  );
}

