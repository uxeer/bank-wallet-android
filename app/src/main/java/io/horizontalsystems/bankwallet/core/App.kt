package io.horizontalsystems.bankwallet.core

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.squareup.leakcanary.LeakCanary
import io.horizontalsystems.bankwallet.core.factories.AdapterFactory
import io.horizontalsystems.bankwallet.core.managers.*
import io.horizontalsystems.bankwallet.core.security.EncryptionManager
import io.horizontalsystems.bitcoinkit.BitcoinKit
import io.horizontalsystems.ethereumkit.EthereumKit
import java.util.*

class App : Application() {

    companion object {

        lateinit var preferences: SharedPreferences

        lateinit var secureStorage: ISecuredStorage
        lateinit var localStorage: ILocalStorage
        lateinit var encryptionManager: EncryptionManager
        lateinit var wordsManager: WordsManager
        lateinit var randomManager: IRandomProvider
        lateinit var networkManager: INetworkManager
        lateinit var currencyManager: ICurrencyManager
        lateinit var backgroundManager: BackgroundManager
        lateinit var languageManager: ILanguageManager
        lateinit var systemInfoManager: ISystemInfoManager
        lateinit var pinManager: IPinManager
        lateinit var lockManager: ILockManager
        lateinit var appConfigProvider: IAppConfigProvider
        lateinit var walletManager: IWalletManager
        lateinit var coinManager: CoinManager

        lateinit var rateSyncer: RateSyncer
        lateinit var rateManager: RateManager
        lateinit var periodicTimer: PeriodicTimer
        lateinit var networkAvailabilityManager: NetworkAvailabilityManager
        lateinit var stubStorage: StubStorage
        lateinit var transactionRateSyncer: ITransactionRateSyncer
        lateinit var transactionManager: TransactionManager

        val testMode = true

        lateinit var instance: App
            private set

        var lastExitDate: Long = 0
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        // Initialize BitcoinKit
        BitcoinKit.init(this)

        // Initialize EthereumKit
        EthereumKit.init(this)

        instance = this
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val fallbackLanguage = Locale("en")

        backgroundManager = BackgroundManager(this)
        encryptionManager = EncryptionManager()
        secureStorage = SecuredStorageManager(encryptionManager)
        localStorage = LocalStorageManager()
        wordsManager = WordsManager(localStorage, secureStorage)
        randomManager = RandomProvider()
        networkManager = NetworkManager()
        systemInfoManager = SystemInfoManager()
        pinManager = PinManager(secureStorage)
        lockManager = LockManager(secureStorage, wordsManager)
        appConfigProvider = AppConfigProvider()
        languageManager = LanguageManager(localStorage, appConfigProvider, fallbackLanguage)
        currencyManager = CurrencyManager(localStorage, appConfigProvider)
        walletManager = WalletManager(AdapterFactory())
        coinManager = CoinManager(wordsManager, walletManager, appConfigProvider)

        networkAvailabilityManager = NetworkAvailabilityManager()
        periodicTimer = PeriodicTimer(delay = 3 * 60 * 1000)
        rateSyncer = RateSyncer(networkManager, periodicTimer)
        stubStorage = StubStorage()
        rateManager = RateManager(stubStorage, rateSyncer, walletManager, currencyManager, networkAvailabilityManager, periodicTimer)

        transactionRateSyncer = TransactionRateSyncer(stubStorage, networkManager)
        transactionManager = TransactionManager(stubStorage, transactionRateSyncer, walletManager, currencyManager, wordsManager)

    }

}
