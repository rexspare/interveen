export function OverlayPreview() {
  return (
    <div className="relative mx-auto h-[420px] w-full max-w-sm">
      <div className="absolute inset-0 rounded-[28px] bg-emerald/15 blur-3xl" />
      <div className="relative flex h-full items-center justify-center gap-3">
        <div className="h-[280px] w-[132px] rounded-[22px] bg-surface-raised p-3">
          <div className="flex items-center gap-2">
            <span className="h-2 w-2 rounded-full bg-red-400/80" />
            <span className="h-2 flex-1 rounded-full bg-quiet/30" />
          </div>
          <div className="mt-3 h-[210px] rounded-[12px] bg-background" />
        </div>
        <span className="text-emerald">→</span>
        <div className="flex h-[280px] w-[148px] flex-col rounded-[22px] bg-emerald/20 p-4">
          <div className="flex-1 rounded-[14px] bg-emerald/35" />
          <div className="mt-3 h-2 w-3/4 rounded-full bg-emerald" />
          <div className="mt-2 h-2 w-1/2 rounded-full bg-foreground/30" />
        </div>
      </div>
    </div>
  );
}
