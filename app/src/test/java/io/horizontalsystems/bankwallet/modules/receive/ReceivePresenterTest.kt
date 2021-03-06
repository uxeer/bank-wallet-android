package io.horizontalsystems.bankwallet.modules.receive

import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.entities.coins.bitcoin.Bitcoin
import io.horizontalsystems.bankwallet.modules.receive.viewitems.AddressItem
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ReceivePresenterTest {

    private val interactor = Mockito.mock(ReceiveModule.IInteractor::class.java)
    private val view = Mockito.mock(ReceiveModule.IView::class.java)

    private var coin = Bitcoin()
    private var words = listOf("used", "ugly", "meat", "glad", "balance", "divorce", "inner", "artwork", "hire", "invest", "already", "piano")
    private var wordsHash = words.joinToString(" ")
    private var adapterId: String = "${wordsHash.hashCode()}-${coin.code}"
    private val coinAddress = "[coin_address]"
    private val addressItem = AddressItem(adapterId = adapterId, address = coinAddress, coin = coin)
    private val addresses = listOf(addressItem)

    private val presenter = ReceivePresenter(interactor)

    @Before
    fun setUp() {
        presenter.view = view
    }

    @Test
    fun viewDidLoad() {

        presenter.viewDidLoad()

        verify(interactor).getReceiveAddress()
    }

    @Test
    fun didReceiveAddress() {
        presenter.didReceiveAddresses(addresses)

        verify(view).showAddresses(addresses)
    }

    @Test
    fun didFailToReceiveAddress_general() {
        val exception = Exception()

        presenter.didFailToReceiveAddress(exception)

        verify(view).showError(R.string.error)
    }

//    @Test
//    fun didFailToReceiveAddress_unsupportedBlockchain() {
//        val exception = UnsupportedBlockchain("ETH")
//
//        presenter.didFailToReceiveAddress(exception)
//
//        verify(view).showError(R.string.error_unsupported_blockchain)
//    }

    @Test
    fun onCopyClick() {
        presenter.didReceiveAddresses(addresses)
        presenter.onCopyClick(any())

        verify(interactor).copyToClipboard(addressItem.address)
    }

    @Test
    fun didCopyToClipboard() {
        presenter.didCopyToClipboard()

        verify(view).showCopied()
    }

}
