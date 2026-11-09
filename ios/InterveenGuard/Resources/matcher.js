const INTERVEEN_DOMAINS = [
  "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com", "redtube.com",
  "youporn.com", "tube8.com", "spankbang.com", "xhamster.desi", "chaturbate.com",
  "stripchat.com", "livejasmin.com", "bongacams.com", "onlyfans.com", "manyvids.com",
  "fansly.com", "porntrex.com", "hqporner.com", "eporner.com", "tnaflix.com",
  "beeg.com", "rule34.xxx", "nhentai.net", "hanime.tv", "javlibrary.com",
  "jable.tv", "missav.com", "javbus.com", "javdb.com", "thisvid.com", "motherless.com",
  "imagefap.com", "pornpics.com", "erome.com", "fapello.com", "coomer.su", "kemono.su",
  "thothub.tv", "xxx.com", "sex.com", "porn.com", "wow.xxx",
  "redgifs.com", "youjizz.com", "e621.net", "gelbooru.com", "f95zone.to",
  "literotica.com", "pornhd.com", "xhamster.one", "91porn.com",
];
const INTERVEEN_KEYWORDS = [
  "porn", "porno", "pornhub", "xvideos", "xnxx", "xhamster", "onlyfans", "only fans",
  "fansly", "nsfw", "18+", "xxx", "hentai", "rule34", "jav ", "erotic",
  "adult video", "sex video", "nude video", "nude pics", "nude pic", "nudes", "lewd",
  "redgifs", "chaturbate", "stripchat", "brazzers",
  "video porno", "videos porno", "vídeo porno", "filme porno", "film porno", "filmes porno",
  "pornografia", "pornographie", "pornografie", "contenido adulto", "conteúdo adulto",
  "contenido xxx", "conteúdo xxx", "nopor", "pornô", "pornos", "sexvideo",
  "nacktbilder", "nacktvideo", "porno video", "pornovideo", "sites porno",
  "video sesso", "sesso video", "filmy porno", "seksvideo", "porrfilm", "porr video",
  "nakenvideo", "seksivideo", "aikuisviihde", "pornó", "szexvideó", "filme xxx", "video xxx",
  "phim sex", "phim 18+", "clip nóng", "bokep", "film dewasa", "video bokep",
  "bold movie", "pelikulang bold",
  "порно", "порн", "порно видео", "порно відео", "секс видео", "секс відео",
  "эротика", "еротика", "xxx видео", "порновидео", "голые фото", "голе відео",
  "πορνό", "πορνο", "σεξ βίντεο", "ερωτικό βίντεο", "porno izle", "seks videosu",
  "yetişkin video", "sikiş",
  "إباحي", "اباحي", "إباحية", "اباحية", "سكس", "بورن", "فيديو سكس",
  "افلام اباحية", "أفلام إباحية", "پورن", "پورنو", "سکسی", "فیلم سکسی",
  "فيلم سوپر", "فیلم سوپر", "فحش", "سیکس ویڈیو", "سیکس وڈیو", "پورن ویڈیو",
  "פורנו", "סרט פורנו", "סרטי פורנו", "סקס וידאו",
  "पोर्न", "पॉर्न", "सेक्स वीडियो", "अश्लील", "नंगा वीडियो", "xxx वीडियो",
  "পর্ন", "সেক্স ভিডিও", "অশ্লীল", "போர்ன்", "செக்ஸ் வீடியோ", "పోర్న్",
  "సెక్స్ వీడియో", "પોર્ન", "સેક્સ વિડિયો", "सेक्स व्हिडिओ", "ਪੋਰਨ", "ਸੈਕਸ ਵੀਡੀਓ",
  "色情", "黄片", "黃片", "色情片", "色情视频", "色情視頻", "成人视频", "成人視頻",
  "成人影片", "毛片", "a片", "av片", "エロ", "エロ動画", "エロい", "ポルノ",
  "アダルト動画", "無修正", "야동", "포르노", "성인동영상", "성인물", "에로영화", "에로 동영상",
  "หนังโป๊", "คลิปโป๊", "หนังx", "video za ngono", "picha za uchi",
];
const INTERVEEN_TLDS = ["xxx", "sex", "porn", "adult"];
const INTERVEEN_HOST_TOKENS = [
  "porn", "porno", "xxx", "hentai", "nsfw", "xvideos", "xnxx", "pornhub",
  "onlyfans", "spankbang", "chaturbate", "redtube", "youporn", "rule34", "redgifs", "bokep",
];
const INTERVEEN_MIXED_HOSTS = [
  "x.com", "twitter.com", "reddit.com", "tumblr.com", "instagram.com",
  "tiktok.com", "facebook.com", "bsky.app", "youtube.com", "google.com",
  "bing.com", "baidu.com", "yandex.ru", "yandex.com", "naver.com",
];

function interveenNormalizeHost(value) {
  return String(value || "")
    .toLowerCase()
    .replace(/^www\./, "")
    .replace(/\.+$/, "");
}

function interveenExtractHosts(raw) {
  const hosts = new Set();
  const re = /(?:https?:\/\/)?((?:[a-z0-9-]+\.)+[a-z]{2,})(\/[^\s]*)?/gi;
  let match;
  while ((match = re.exec(raw))) {
    const host = interveenNormalizeHost(match[1]);
    if (host.includes(".")) hosts.add(host);
  }
  return hosts;
}

function interveenHostLooksAdult(host) {
  return String(host || "").split(".").some((label) => {
    if (label === "jav" || (label.startsWith("jav") && !label.startsWith("java"))) return true;
    return INTERVEEN_HOST_TOKENS.some((token) => label.includes(token));
  });
}

function interveenIsMixedHost(host) {
  const normalized = interveenNormalizeHost(host);
  return INTERVEEN_MIXED_HOSTS.some((item) => normalized === item || normalized.endsWith("." + item));
}

function interveenNormalizeText(raw) {
  const value = String(raw || "");
  let decoded = value;
  try {
    decoded = value.replace(/(?:%[0-9A-Fa-f]{2})+/g, (match) => {
      try {
        return decodeURIComponent(match);
      } catch (error) {
        return match;
      }
    });
  } catch (error) {
    decoded = value;
  }
  decoded = decoded.replace(/(?<=[\p{L}\p{N}])\+(?=[\p{L}\p{N}])/gu, " ");
  return decoded.normalize("NFKC").toLowerCase().trim();
}

function interveenTooShort(haystack) {
  if (haystack.length < 2) return true;
  if (haystack.length >= 3) return false;
  return /^[\x00-\x7F]*$/.test(haystack);
}

function interveenEvaluate(raw) {
  const haystack = interveenNormalizeText(raw);
  if (interveenTooShort(haystack)) return { matched: false, evidence: "" };
  for (const host of interveenExtractHosts(haystack)) {
    if (interveenIsMixedHost(host)) continue;
    const domain = INTERVEEN_DOMAINS.find((item) => host === item || host.endsWith("." + item));
    if (domain) return { matched: true, evidence: domain };
    const tld = host.split(".").pop();
    if (INTERVEEN_TLDS.includes(tld)) return { matched: true, evidence: host };
    if (interveenHostLooksAdult(host)) return { matched: true, evidence: host };
  }
  for (const keyword of INTERVEEN_KEYWORDS) {
    const needle = interveenNormalizeText(keyword);
    if (needle.length >= 2 && haystack.includes(needle)) {
      return { matched: true, evidence: keyword.trim() };
    }
  }
  return { matched: false, evidence: "" };
}
