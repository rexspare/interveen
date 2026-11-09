import type { Metadata } from "next";
import { LegalArticle } from "@/components/LegalArticle";
import { site } from "@/lib/site";

export const metadata: Metadata = {
  title: "Privacy Policy",
  description:
    "How Interveen and SOLTO Apps handle information on the device and at interveen.soltoapps.top.",
};

function Mail() {
  return (
    <a className="text-emerald hover:underline" href={`mailto:${site.email}`}>
      {site.email}
    </a>
  );
}

export default function PrivacyPage() {
  return (
    <LegalArticle title="Privacy Policy" updated="8 November 2026">
      <p>
        This policy describes how SOLTO Apps (“we”, “us”) handles information when
        you use Interveen on Android or iPhone, and when you visit this website at{" "}
        <a className="text-emerald hover:underline" href={site.url}>
          interveen.soltoapps.top
        </a>
        . Interveen is designed so that browsing matches and your personal reminder
        stay on the phone you control.
      </p>

      <section>
        <h2>Who we are</h2>
        <p>
          Interveen is published by SOLTO Apps. Other SOLTO products may use other
          subdomains of soltoapps.top. This policy applies only to Interveen and
          this Interveen website, unless another SOLTO page says otherwise.
        </p>
        <p>
          Questions: <Mail />.
        </p>
      </section>

      <section>
        <h2>What Interveen processes on the device</h2>
        <p>
          Guard reads information that is already on the screen so it can decide
          whether to pause. That work happens locally. The app may process:
        </p>
        <ul>
          <li>
            Visible address-bar text, page titles, and other on-screen text in
            browsers and in selected apps such as Chrome, Safari, X, and Facebook
          </li>
          <li>
            URLs and hostnames compared against an on-device list of known adult
            destinations, adult top-level domains, path tokens, and keywords
          </li>
          <li>
            The reminder you create: a short video, a still image, or written
            text, plus a title
          </li>
          <li>
            Local counters such as times saved and a goal streak
          </li>
          <li>
            Permission and Guard on/off state, last match time, and similar
            settings stored in private app storage or an app group on iOS
          </li>
        </ul>
        <p>
          Interveen does not upload browsing history, matched URLs, screenshots,
          or your reminder media to SOLTO Apps.
        </p>
      </section>

      <section>
        <h2>What we do not collect</h2>
        <p>We do not operate an Interveen account, advertising network, or product analytics pixel in the app. We do not:</p>
        <ul>
          <li>Create a profile of the sites you visit</li>
          <li>Sell or rent personal data</li>
          <li>Share matched destinations with other apps, except as required by the operating system to show an overlay or a Screen Time shield</li>
          <li>Require you to sign in</li>
        </ul>
        <p>
          If you subscribe to Interveen Plus, Google Play processes the payment.
          SOLTO Apps does not receive your card number. Play shares the purchase
          state with the app so Plus can turn on. That check does not include the
          sites you visit.
        </p>
      </section>

      <section>
        <h2>Permissions and why they exist</h2>
        <p>You grant each permission yourself. You can revoke them in system settings. If you revoke them, Guard may stop working.</p>
        <ul>
          <li>
            <strong className="text-foreground">Android Accessibility.</strong> Used only
            to read on-screen text in browsers and selected apps so the matcher can
            run. It is not used to control other apps or to send text off the device.
          </li>
          <li>
            <strong className="text-foreground">Display over other apps.</strong> Used
            to show the full-screen reminder you chose after a match.
          </li>
          <li>
            <strong className="text-foreground">Notifications.</strong> Used to keep
            Guard running as a foreground service so the system is less likely to
            stop it, and to surface a quiet status.
          </li>
          <li>
            <strong className="text-foreground">Camera, microphone, and photos.</strong>{" "}
            Camera and microphone are used only when you record a reminder. On
            Android, existing photos and videos are chosen through the system
            photo picker; Interveen does not request broad photo or video library
            access. Media is copied into Interveen’s private storage on that
            device.
          </li>
          <li>
            <strong className="text-foreground">iPhone Screen Time / Family Controls.</strong>{" "}
            Used to apply Apple’s adult web filter and, when you pick sites, to
            attach those tokens to Screen Time. Shield actions cannot open the host
            app directly; they may set a local pending flag and a notification that
            opens Interveen.
          </li>
          <li>
            <strong className="text-foreground">Safari Interveen Blocker and Interveen Guard.</strong>{" "}
            The content blocker stops listed adult documents in Safari after you
            enable it. The optional web extension can show an in-page overlay if
            you allow it on websites.
          </li>
        </ul>
      </section>

      <section>
        <h2>This website</h2>
        <p>
          The site at interveen.soltoapps.top is a marketing and legal site. It
          does not set advertising cookies and does not run a third-party analytics
          or advertising pixel from SOLTO Apps.
        </p>
        <p>
          When your browser loads a page, the host that serves the site may
          receive standard technical data as part of HTTPS: IP address, date and
          time, requested URL, referrer, and user agent. We do not use those logs
          to identify you, and we do not combine them with app data. The hosting
          provider may retain server logs for security and operations for a limited
          period according to its own practices.
        </p>
        <p>
          If you email <Mail />, that message is stored in our inbox so we can
          reply. We keep correspondence only as long as needed to handle your
          request, unless a longer period is required by law.
        </p>
      </section>

      <section>
        <h2>Legal bases</h2>
        <p>
          Where data-protection law requires a legal basis, we rely on: (1) your
          consent for optional permissions such as camera, photos, Accessibility,
          Screen Time, and Safari extensions; (2) performance of the product you
          asked to run, for on-device matching and storing the reminder you create;
          and (3) legitimate interests in operating a secure website and answering
          support email, balanced against your rights.
        </p>
      </section>

      <section>
        <h2>Retention</h2>
        <p>
          On-device data remains until you delete it in the app, clear app storage,
          or uninstall Interveen. Website server logs, if any, are not used to
          build a browsing profile. Support emails are retained as described above.
        </p>
      </section>

      <section>
        <h2>Sharing</h2>
        <p>
          We do not sell personal information. We may disclose information if
          required by law, to protect rights or safety, or to a successor if SOLTO
          Apps transfers Interveen as a product — still without uploading your
          on-device match history, because that history is not in our possession.
        </p>
        <p>
          Apple, Google, and the browser vendors process whatever the operating
          system already processes when you grant Screen Time, Accessibility, or
          install from a store. Those companies have their own policies.
        </p>
      </section>

      <section>
        <h2>Children</h2>
        <p>
          Interveen is a personal tool for an adult who controls the device. It is
          not directed at children under 13, and it is not a parental-control
          product for monitoring another person’s phone. Do not use it to watch
          someone else’s device without their permission.
        </p>
      </section>

      <section>
        <h2>Your choices and rights</h2>
        <p>
          You can turn Guard off, revoke permissions, delete your reminder, or
          uninstall the app. Depending on where you live, you may have rights to
          access, correct, delete, or restrict personal data we hold — which for
          Interveen is typically limited to support email if you wrote to us. To
          exercise those rights, contact <Mail />. You may also complain to a
          supervisory authority in your country.
        </p>
      </section>

      <section>
        <h2>Security</h2>
        <p>
          Matching and media stay in the app sandbox (and, on iOS, an app group
          used by Interveen extensions). No system is perfect. Keep the phone
          locked and the OS updated. Sideloaded builds signed with a temporary
          keystore are for testing only.
        </p>
      </section>

      <section>
        <h2>Changes</h2>
        <p>
          We may update this policy. The “Last updated” date at the top will
          change. Continued use of Interveen or this site after an update means
          you have had an opportunity to read the new policy.
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
