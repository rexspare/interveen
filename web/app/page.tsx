import Image from "next/image";
import { OverlayPreview } from "@/components/OverlayPreview";

const steps = [
  {
    title: "Guard watches this device",
    body: "On Android and iPhone, Interveen notices adult destinations in browsers and in apps like X and Facebook. Ordinary feeds stay open.",
  },
  {
    title: "The session collapses",
    body: "The browsing window is closed. There is no lecture. Just a pause.",
  },
  {
    title: "Your goal comes forward",
    body: "A video, a still, or a sentence you recorded plays full screen. You tap I Remember My Goal, and you continue.",
  },
];

const principles = [
  {
    title: "On this phone only",
    body: "Address-bar and on-screen text are matched locally. History, media, and matches are not uploaded.",
  },
  {
    title: "You choose the reminder",
    body: "Record a short clip, pick a still, or write the thing you want to protect. It stays in private app storage.",
  },
  {
    title: "No accounts",
    body: "There is no Interveen login, analytics network, or ad profile. Guard is a tool you control.",
  },
];

export default function Home() {
  return (
    <div>
      <section className="mx-auto grid w-full max-w-5xl items-center gap-12 px-6 py-20 lg:grid-cols-2 lg:py-28">
        <div>
          <Image
            src="/interveen-logo.png"
            alt="Interveen"
            width={88}
            height={88}
            className="rounded-[20px]"
            priority
          />
          <h1 className="mt-6 max-w-xl text-4xl font-semibold tracking-tight sm:text-5xl">
            Stay with what matters.
          </h1>
          <p className="mt-5 max-w-lg text-lg leading-8 text-mist">
            Interveen is a quiet pause between impulse and action. When a blocked
            destination opens, the session closes and the reminder you chose comes
            forward.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <span className="rounded-[20px] bg-surface px-5 py-3 text-sm text-mist">
              Android
            </span>
            <span className="rounded-[20px] bg-surface px-5 py-3 text-sm text-mist">
              iPhone
            </span>
          </div>
        </div>
        <OverlayPreview />
      </section>

      <section className="border-t border-white/5">
        <div className="mx-auto w-full max-w-5xl px-6 py-20">
          <h2 className="text-3xl font-semibold tracking-tight">How Guard works</h2>
          <p className="mt-3 max-w-2xl text-mist">
            Matching never leaves the phone. Nothing is sold. The overlay is yours.
          </p>
          <div className="mt-12 grid gap-5 md:grid-cols-3">
            {steps.map((step, index) => (
              <div
                key={step.title}
                className="rounded-[20px] bg-surface p-6"
              >
                <p className="text-sm font-medium text-emerald">{index + 1}</p>
                <h3 className="mt-3 text-lg font-medium">{step.title}</h3>
                <p className="mt-3 text-sm leading-6 text-mist">{step.body}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="border-t border-white/5">
        <div className="mx-auto w-full max-w-5xl px-6 py-20">
          <h2 className="text-3xl font-semibold tracking-tight">Built to stay private</h2>
          <div className="mt-12 grid gap-5 md:grid-cols-3">
            {principles.map((item) => (
              <div key={item.title} className="rounded-[20px] bg-surface p-6">
                <h3 className="text-lg font-medium">{item.title}</h3>
                <p className="mt-3 text-sm leading-6 text-mist">{item.body}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
