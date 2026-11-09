import type { MetadataRoute } from "next";
import { site } from "@/lib/site";

export const dynamic = "force-static";

export default function sitemap(): MetadataRoute.Sitemap {
  return ["", "/privacy", "/terms", "/support"].map((path) => ({
    url: `${site.url}${path}`,
    lastModified: new Date("2026-09-28"),
  }));
}
