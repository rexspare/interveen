import SwiftUI
import WebKit

struct SettingsView: View {
    @AppStorage(InterveenDefaults.Key.languageTag, store: InterveenDefaults.suite) private var languageTag = ""

    private let languages: [(tag: String, name: String)] = [
        ("en", "English"),
        ("ar", "العربية"),
        ("ur", "اردو"),
        ("hi", "हिन्दी"),
        ("es", "Español"),
        ("fr", "Français"),
        ("de", "Deutsch"),
        ("pt-BR", "Português (Brasil)"),
        ("it", "Italiano"),
        ("tr", "Türkçe"),
        ("id", "Bahasa Indonesia"),
        ("ja", "日本語"),
        ("ko", "한국어"),
        ("zh-Hans", "简体中文"),
        ("zh-Hant", "繁體中文"),
        ("ru", "Русский"),
        ("nl", "Nederlands"),
        ("vi", "Tiếng Việt"),
        ("pl", "Polski"),
        ("sv", "Svenska"),
        ("th", "ไทย"),
        ("uk", "Українська"),
        ("he", "עברית"),
    ]

    private var selectedLanguageName: String {
        if languageTag.isEmpty {
            return String(localized: "settings_language_system")
        }
        return languages.first(where: { $0.tag == languageTag })?.name ?? languageTag
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 22) {
                section(title: String(localized: "settings_section_language")) {
                    Text("settings_language_body")
                        .font(.inter(16))
                        .foregroundStyle(Palette.softMist)
                    languageMenu
                }

                section(title: String(localized: "settings_section_legal")) {
                    NavigationLink {
                        LegalDocumentView(document: .website)
                    } label: {
                        linkRow(title: String(localized: "settings_website"))
                    }
                    .buttonStyle(.plain)

                    NavigationLink {
                        LegalDocumentView(document: .privacy)
                    } label: {
                        linkRow(title: String(localized: "settings_privacy"))
                    }
                    .buttonStyle(.plain)

                    NavigationLink {
                        LegalDocumentView(document: .terms)
                    } label: {
                        linkRow(title: String(localized: "settings_terms"))
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(24)
        }
        .background(Palette.slateCharcoal.ignoresSafeArea())
        .navigationTitle(String(localized: "settings_title"))
        .navigationBarTitleDisplayMode(.large)
    }

    private var languageMenu: some View {
        Menu {
            Button(String(localized: "settings_language_system")) {
                languageTag = ""
            }
            ForEach(languages, id: \.tag) { item in
                Button(item.name) {
                    languageTag = item.tag
                }
            }
        } label: {
            HStack {
                Text(selectedLanguageName)
                    .font(.inter(16, weight: .medium))
                    .foregroundStyle(Palette.crispWhite)
                Spacer()
                Image(systemName: "chevron.down")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(Palette.softMist)
            }
            .padding(20)
            .background(Palette.coolGray, in: RoundedRectangle(cornerRadius: InterveenMetrics.cardRadius, style: .continuous))
        }
    }

    private func section<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(title.uppercased())
                .font(.system(size: 12, weight: .medium))
                .foregroundStyle(Palette.quietSlate)
            content()
        }
    }

    private func linkRow(title: String) -> some View {
        HStack {
            Text(title)
                .font(.inter(16, weight: .medium))
                .foregroundStyle(Palette.crispWhite)
            Spacer()
            Image(systemName: "chevron.right")
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(Palette.softMist)
        }
        .padding(20)
        .background(Palette.coolGray, in: RoundedRectangle(cornerRadius: InterveenMetrics.cardRadius, style: .continuous))
    }
}

enum LegalDocumentKind {
    case website
    case privacy
    case terms

    var titleKey: LocalizedStringKey {
        switch self {
        case .website: return "settings_website"
        case .privacy: return "settings_privacy"
        case .terms: return "settings_terms"
        }
    }

    var url: URL {
        switch self {
        case .website: return URL(string: "https://interveen.soltoapps.top/")!
        case .privacy: return URL(string: "https://interveen.soltoapps.top/privacy")!
        case .terms: return URL(string: "https://interveen.soltoapps.top/terms")!
        }
    }
}

struct LegalDocumentView: View {
    let document: LegalDocumentKind

    var body: some View {
        LegalWebView(url: document.url)
            .background(Palette.slateCharcoal.ignoresSafeArea())
            .navigationTitle(document.titleKey)
            .navigationBarTitleDisplayMode(.inline)
    }
}

private struct LegalWebView: UIViewRepresentable {
    let url: URL

    func makeUIView(context: Context) -> WKWebView {
        let view = WKWebView(frame: .zero)
        view.isOpaque = false
        view.backgroundColor = UIColor(red: 15 / 255, green: 23 / 255, blue: 42 / 255, alpha: 1)
        view.scrollView.backgroundColor = view.backgroundColor
        return view
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        if uiView.url != url {
            uiView.load(URLRequest(url: url))
        }
    }
}
