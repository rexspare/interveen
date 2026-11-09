import Link from "next/link";
import { site } from "@/lib/site";

export function SiteFooter() {
  return (
    <footer className="mt-auto border-t border-white/5">
      <div className="mx-auto flex w-full max-w-5xl flex-col gap-4 px-6 py-10 text-sm text-quiet sm:flex-row sm:items-center sm:justify-between">
        <p>
          {site.publisher}. Interveen at{" "}
          <a href={site.url} className="hover:text-foreground">
            interveen.soltoapps.top
          </a>
        </p>
        <div className="flex flex-wrap gap-5">
          <Link href="/privacy" className="hover:text-foreground">
            Privacy Policy
          </Link>
          <Link href="/terms" className="hover:text-foreground">
            Terms
          </Link>
          <Link href="/support" className="hover:text-foreground">
            Support
          </Link>
          <a href={`mailto:${site.email}`} className="hover:text-foreground">
            {site.email}
          </a>
        </div>
      </div>
    </footer>
  );
}
