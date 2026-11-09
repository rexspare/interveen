import Image from "next/image";
import Link from "next/link";

export function BrandLogo({
  size = 36,
  withWordmark = true,
}: {
  size?: number;
  withWordmark?: boolean;
}) {
  return (
    <Link href="/" className="inline-flex items-center gap-2.5">
      <Image
        src="/interveen-logo.png"
        alt="Interveen"
        width={size}
        height={size}
        className="rounded-lg"
        priority
      />
      {withWordmark ? (
        <span className="text-sm font-medium tracking-wide text-foreground">
          Interveen
        </span>
      ) : null}
    </Link>
  );
}
