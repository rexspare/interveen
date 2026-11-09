import type { ReactNode } from "react";

export function LegalArticle({
  title,
  updated,
  children,
}: {
  title: string;
  updated: string;
  children: ReactNode;
}) {
  return (
    <article className="mx-auto w-full max-w-2xl px-6 py-16">
      <p className="text-sm text-quiet">Legal</p>
      <h1 className="mt-2 text-4xl font-semibold tracking-tight">{title}</h1>
      <p className="mt-3 text-sm text-quiet">Last updated: {updated}</p>
      <div className="legal-body mt-10 space-y-8 text-base leading-7 text-mist [&_h2]:text-lg [&_h2]:font-semibold [&_h2]:text-foreground [&_p]:text-mist [&_ul]:list-disc [&_ul]:space-y-2 [&_ul]:pl-5 [&_strong]:font-medium [&_strong]:text-foreground">
        {children}
      </div>
    </article>
  );
}
