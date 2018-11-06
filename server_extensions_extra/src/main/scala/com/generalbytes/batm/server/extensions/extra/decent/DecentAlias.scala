package com.generalbytes.batm.server.extensions.extra.decent

import com.generalbytes.batm.common.Currency.DCT
import com.generalbytes.batm.common.Wallet

object DecentAlias {
  type DCTWallet[F[_]] = Wallet[F, DCT]
}
