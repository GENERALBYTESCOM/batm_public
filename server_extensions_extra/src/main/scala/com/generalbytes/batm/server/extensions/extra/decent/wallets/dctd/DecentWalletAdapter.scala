package com.generalbytes.batm.server.extensions.extra.decent.wallets.dctd

import cats.Applicative
import com.generalbytes.batm.common.Alias.Interpreter
import com.generalbytes.batm.common.Currency.DCT
import com.generalbytes.batm.common.adapters.WalletAdapter
import com.generalbytes.batm.server.extensions.extra.decent.DecentAlias.DCTWallet

class DecentWalletAdapter[F[_] : Interpreter : Applicative](client: DCTWallet[F]) extends WalletAdapter[F, DCT](client)
