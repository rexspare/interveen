import type { Metadata } from "next";
import { LegalArticle } from "@/components/LegalArticle";
import { site } from "@/lib/site";

export const metadata: Metadata = {
  title: "Terms and Conditions",
  description:
    "Terms for using Interveen and the Interveen site on soltoapps.top.",
};

function Mail() {
  return (
    <a className="text-emerald hover:underline" href={`mailto:${site.email}`}>
      {site.email}
    </a>
  );
}

export default function TermsPage() {
  return (
    <LegalArticle title="Terms and Conditions" updated="8 November 2026">
      <p>
        These terms are an agreement between you and SOLTO Apps for Interveen and
        for this website at{" "}
        <a className="text-emerald hover:underline" href={site.url}>
          interveen.soltoapps.top
        </a>
        . By installing Interveen, using Guard, or browsing this site, you agree
        to them. If you do not agree, uninstall the app and do not use the site.
      </p>

      <section>
        <h2>Who we are</h2>
        <p>
          Interveen is published by SOLTO Apps. Related products may appear on
          other subdomains of soltoapps.top. These terms cover Interveen only.
          Contact: <Mail />.
        </p>
      </section>

      <section>
        <h2>What Interveen is — and is not</h2>
        <p>
          Interveen is a personal focus tool. On a device you control, it tries to
          notice adult destinations or adult on-screen text, collapse that
          session, and show a reminder you created (video, image, or text).
        </p>
        <p>Interveen is not:</p>
        <ul>
          <li>Medical care, therapy, counselling, or a recovery programme</li>
          <li>A guarantee that adult content cannot be reached another way</li>
          <li>A parental-control or employee-monitoring product</li>
          <li>
            A network filter that can see or stop traffic the operating system
            does not expose
          </li>
        </ul>
        <p>
          Apple, Google, and browser vendors can change how address bars, WebViews,
          Accessibility, Screen Time, and Safari extensions work. Guard may miss
          content, including image-only posts, some Incognito windows, and apps we
          do not watch.
        </p>
      </section>

      <section>
        <h2>Eligibility and your device</h2>
        <p>
          You must be old enough to enter a contract in your country and you must
          use Interveen only on a device you are authorised to configure. You
          grant Accessibility, overlay, notifications, Screen Time, and Safari
          permissions yourself.
        </p>
        <p>
          You must not use Interveen to monitor someone else’s device without
          their clear permission. Doing so may violate these terms and applicable
          law.
        </p>
      </section>

      <section>
        <h2>Licence</h2>
        <p>
          We grant you a personal, non-exclusive, non-transferable licence to
          install Interveen on devices you control, for your own use, as
          distributed by SOLTO Apps or an official store listing. You may not
          reverse engineer the app except where the law allows, rent it, or
          misrepresent it as someone else’s product.
        </p>
      </section>

      <section>
        <h2>Your content</h2>
        <p>
          You keep rights in the reminder you record or pick. You grant Interveen
          permission to store and display that reminder on the same device for the
          purpose of the overlay. Do not store content you are not allowed to use.
          We do not claim ownership of your clips, photos, or written intention.
        </p>
      </section>

      <section>
        <h2>Acceptable use</h2>
        <ul>
          <li>Do not interfere with Guard in order to harm another person</li>
          <li>Do not attempt to extract another user’s data through the app</li>
          <li>
            Do not scrape, overload, or attack interveen.soltoapps.top or other
            SOLTO Apps hosts
          </li>
          <li>
            Do not use Interveen in any way that is illegal where you use it
          </li>
        </ul>
      </section>

      <section>
        <h2>Stores, payment, and this website</h2>
        <p>
          If Interveen is offered on Google Play or the App Store, those stores’
          terms also apply. Interveen includes every feature for 7 days. After
          that, a subscription is required. It is billed by the store. Prices
          shown in the app are the store’s price for your country. You can cancel
          in the store’s subscription settings. Visiting interveen.soltoapps.top
          does not create a paid subscription unless a store listing or in-app
          purchase you complete says otherwise.
        </p>
      </section>

      <section>
        <h2>Availability and changes</h2>
        <p>
          We may update Interveen, these terms, or this site. Guard can pause if
          the system stops a service, if you revoke a permission, or if a browser
          hides the address bar. We may discontinue features. We will update the
          date at the top of these terms when they change. Continued use after a
          change is acceptance of the revised terms.
        </p>
      </section>

      <section>
        <h2>Disclaimer</h2>
        <p>
          Interveen is provided “as is”. To the fullest extent permitted by law,
          SOLTO Apps disclaims warranties of merchantability, fitness for a
          particular purpose, and uninterrupted or error-free operation. We do not
          warrant that every adult destination will be paused.
        </p>
      </section>

      <section>
        <h2>Limitation of liability</h2>
        <p>
          To the fullest extent permitted by law, SOLTO Apps is not liable for
          indirect, incidental, special, consequential, or punitive damages, or
          for lost data, missed interruptions, or content you still reach. Our
          total liability for claims relating to Interveen or this site is limited
          to the amount you paid us for Interveen in the three months before the
          claim, or zero if you paid nothing.
        </p>
        <p>
          Nothing in these terms excludes liability that cannot be excluded under
          applicable law, including liability for death or personal injury caused
          by negligence, or for fraud, where such a rule applies.
        </p>
      </section>

      <section>
        <h2>Termination</h2>
        <p>
          You may stop using Interveen at any time by uninstalling it and revoking
          permissions. We may stop offering the app or this site. Provisions that
          by their nature should survive (including disclaimers and liability
          limits) remain in effect.
        </p>
      </section>

      <section>
        <h2>Law</h2>
        <p>
          These terms are governed by the laws applicable to SOLTO Apps, excluding
          conflict-of-law rules. Courts in that jurisdiction may hear disputes,
          except where consumer law in your country gives you a mandatory local
          forum.
        </p>
      </section>

      <section>
        <h2>Contact</h2>
        <p>
          SOLTO Apps — Interveen
          <br />
          Website:{" "}
          <a className="text-emerald hover:underline" href={site.url}>
            {site.url.replace("https://", "")}
          </a>
          <br />
          Email: <Mail />
        </p>
      </section>
    </LegalArticle>
  );
}
