import Link from "next/link";

export default function NotFound() {
  return (
    <div className="mx-auto max-w-xl px-6 py-24">
      <p className="text-sm text-quiet">404</p>
      <h1 className="mt-2 text-3xl font-semibold">This page is not here.</h1>
      <p className="mt-4 text-mist">The pause still is. Head back to Interveen.</p>
      <Link
        href="/"
        className="mt-8 inline-flex rounded-[20px] bg-emerald px-5 py-3 text-sm font-medium text-background"
      >
        Home
      </Link>
    </div>
  );
}
