import type { Metadata } from "next";
import Link from "next/link";
import { LegalArticle } from "@/components/LegalArticle";
import { site } from "@/lib/site";

export const metadata: Metadata = {
  title: "Support",
  description: "How to set up Interveen Guard and how to reach SOLTO Apps.",
};

export default function SupportPage() {
  return (
    <LegalArticle title="Support" updated="28 September 2026">
      <p>
        Interveen is a personal Guard on the phone you control. If something is
        not pausing, it is usually a permission that is still off.
      </p>

      <section>
        <h2>Android</h2>
        <p>
          Turn on Accessibility for Interveen Guard and allow display over other
          apps. Keep the quiet notification on so the system does not put Guard to
          sleep. Test in Chrome or the Google app, and in X or Facebook with a
          clearly adult caption or destination.
        </p>
      </section>

      <section>
        <h2>iPhone</h2>
        <p>
          Screen Time Allow is not Safari blocking. Open Settings → Apps → Safari
          → Extensions and turn on Interveen Blocker, then test wow.xxx in Safari.
          The Google app cannot be filtered on iOS. Chrome only follows Apple’s
          adult catalog.
        </p>
      </section>

      <section>
        <h2>Privacy and terms</h2>
        <ul>
          <li>
            <Link className="text-emerald hover:underline" href="/privacy">
              Privacy Policy
            </Link>
          </li>
          <li>
            <Link className="text-emerald hover:underline" href="/terms">
              Terms and Conditions
            </Link>
          </li>
        </ul>
      </section>

      <section>
        <h2>Contact</h2>
        <p>
          SOLTO Apps — Interveen
          <br />
          <a className="text-emerald hover:underline" href={site.url}>
            {site.url.replace("https://", "")}
          </a>
          <br />
          <a className="text-emerald hover:underline" href={`mailto:${site.email}`}>
            {site.email}
          </a>
        </p>
      </section>
    </LegalArticle>
  );
}
