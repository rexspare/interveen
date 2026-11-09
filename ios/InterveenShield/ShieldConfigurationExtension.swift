import ManagedSettings
import ManagedSettingsUI
import UIKit

final class ShieldConfigurationExtension: ShieldConfigurationDataSource {
    override func configuration(shielding application: Application) -> ShieldConfiguration {
        makeConfiguration()
    }

    override func configuration(shielding application: Application, in category: ActivityCategory) -> ShieldConfiguration {
        makeConfiguration()
    }

    override func configuration(shielding webDomain: WebDomain) -> ShieldConfiguration {
        makeConfiguration()
    }

    override func configuration(shielding webDomain: WebDomain, in category: ActivityCategory) -> ShieldConfiguration {
        makeConfiguration()
    }

    private func makeConfiguration() -> ShieldConfiguration {
        let charcoal = UIColor(red: 15 / 255, green: 23 / 255, blue: 42 / 255, alpha: 1)
        let mist = UIColor(red: 203 / 255, green: 213 / 255, blue: 225 / 255, alpha: 1)
        let white = UIColor(red: 248 / 255, green: 250 / 255, blue: 252 / 255, alpha: 1)
        let emerald = UIColor(red: 16 / 255, green: 185 / 255, blue: 129 / 255, alpha: 1)
        let ink = UIColor(red: 6 / 255, green: 40 / 255, blue: 28 / 255, alpha: 1)

        return ShieldConfiguration(
            backgroundBlurStyle: .dark,
            backgroundColor: charcoal,
            icon: UIImage(systemName: "leaf.fill"),
            title: .init(text: GuardStore.displayTitle, color: white),
            subtitle: .init(text: GuardStore.displayBody, color: mist),
            primaryButtonLabel: .init(text: "Open Goal Reminder", color: ink),
            primaryButtonBackgroundColor: emerald,
            secondaryButtonLabel: nil
        )
    }
}
