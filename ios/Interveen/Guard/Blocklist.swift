import Foundation

enum DefaultBlocklist {
    static let domains: Set<String> = [
        "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com", "redtube.com",
        "youporn.com", "tube8.com", "spankbang.com", "xhamster.desi", "chaturbate.com",
        "stripchat.com", "livejasmin.com", "bongacams.com", "onlyfans.com", "manyvids.com",
        "fansly.com", "porntrex.com", "hqporner.com", "eporner.com", "tnaflix.com",
        "beeg.com", "rule34.xxx", "nhentai.net", "hanime.tv", "javlibrary.com",
        "jable.tv", "missav.com", "javbus.com", "javdb.com", "thisvid.com",
        "motherless.com", "imagefap.com", "pornpics.com", "erome.com", "fapello.com",
        "coomer.su", "kemono.su", "thothub.tv", "xxx.com", "sex.com", "porn.com",
        "wow.xxx", "redgifs.com", "youjizz.com", "e621.net", "gelbooru.com",
        "f95zone.to", "literotica.com", "pornhd.com", "xhamster.one", "91porn.com",
    ]

    static let adultTlds: Set<String> = ["xxx", "sex", "porn", "adult"]

    static let hostTokens: Set<String> = [
        "porn", "porno", "xxx", "hentai", "nsfw", "xvideos", "xnxx", "pornhub",
        "onlyfans", "spankbang", "chaturbate", "redtube", "youporn",
        "rule34", "redgifs", "bokep",
    ]

    static let keywords: Set<String> = [
        "porn", "porno", "pornhub", "xvideos", "xnxx", "xhamster", "onlyfans", "only fans",
        "fansly", "nsfw", "18+", "xxx", "hentai", "rule34", "jav ", "erotic",
        "adult video", "sex video", "nude video", "nude pics", "nude pic", "nudes",
        "lewd", "redgifs", "chaturbate", "stripchat", "brazzers",
        "video porno", "videos porno", "vídeo porno", "filme porno", "film porno",
        "filmes porno", "pornografia", "pornographie", "pornografie",
        "contenido adulto", "conteúdo adulto", "contenido xxx", "conteúdo xxx",
        "nopor", "pornô", "pornos", "sexvideo", "nacktbilder", "nacktvideo",
        "porno video", "pornovideo", "sites porno", "video sesso", "sesso video",
        "filmy porno", "seksvideo", "porrfilm", "porr video", "nakenvideo",
        "seksivideo", "aikuisviihde", "pornó", "szexvideó", "filme xxx", "video xxx",
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
    ]
}
