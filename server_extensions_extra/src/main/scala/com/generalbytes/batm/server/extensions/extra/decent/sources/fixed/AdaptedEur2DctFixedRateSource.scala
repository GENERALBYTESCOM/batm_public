package com.generalbytes.batm.server.extensions.extra.decent.sources.fixed

import com.generalbytes.batm.common.Alias.{ApplicativeErr, Interpreter}
import com.generalbytes.batm.common.adapters.RateSourceAdapter

class AdaptedEur2DctFixedRateSource[F[_] : Interpreter : ApplicativeErr] extends RateSourceAdapter[F](new EurToDctFixedRateSource(EurToDctFixedRateSource.defaultRate))
