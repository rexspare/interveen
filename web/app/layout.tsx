import type { Metadata } from "next";
import { Inter } from "next/font/google";
import { SiteFooter } from "@/components/SiteFooter";
import { SiteHeader } from "@/components/SiteHeader";
import { site } from "@/lib/site";
import "./globals.css";

const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  metadataBase: new URL(site.url),
  title: {
    default: "Interveen — Stay with what matters",
    template: "%s · Interveen",
  },
  description:
    "Interveen is a quiet pause between impulse and action. When adult content opens, the session closes and your personal goal comes forward. Matching stays on the device.",
  icons: {
    icon: [
      { url: "/favicon.ico", sizes: "48x48" },
      { url: "/icon.png", type: "image/png", sizes: "512x512" },
    ],
    apple: { url: "/apple-icon.png", sizes: "180x180" },
  },
  openGraph: {
    title: "Interveen — Stay with what matters",
    description:
      "A native focus app that interrupts adult-content browsing and shows the reminder you chose.",
    type: "website",
    url: site.url,
    siteName: site.name,
    images: [
      {
        url: "/interveen-logo.png",
        width: 512,
        height: 512,
        alt: "Interveen",
      },
    ],
  },
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="en" className={`${inter.variable} h-full antialiased`}>
      <body className="flex min-h-full flex-col bg-background font-sans text-foreground">
        <SiteHeader />
        <main className="flex-1">{children}</main>
        <SiteFooter />
      </body>
    </html>
  );
}
