/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.watchlists;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.watchlist.WatchListMatch;
import com.generalbytes.batm.server.extensions.watchlist.WatchListQuery;
import com.generalbytes.batm.server.extensions.watchlist.WatchListResult;

import java.util.List;
import java.util.Map;

public class WatchlistExtension extends AbstractExtension implements ITransactionListener {


    @Override
    public String getName() {
        return "BATM Extension that check user against watchlist.";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        ctx.addTransactionListener(this);
    }

    @Override
    public boolean isTransactionPreparationApproved(ITransactionPreparation preparation) {
        return isTransactionApprovedInner(preparation.getIdentityPublicId());
    }

    @Override
    public boolean isTransactionApproved(ITransactionRequest transactionRequest) {
        return isTransactionApprovedInner(transactionRequest.getIdentityPublicId());
    }

    @Override
    public OutputQueueInsertConfig overrideOutputQueueInsertConfig(ITransactionQueueRequest transactionQueueRequest, OutputQueueInsertConfig outputQueueInsertConfig) {
        return null;
    }

    private boolean isTransactionApprovedInner(String identityPublicId) {
        if (identityPublicId == null) {
            return true;
        }

        IIdentity identity = ctx.findIdentityByIdentityId(identityPublicId);
        if (identity.getIdentityPieces() == null || identity.getIdentityPieces().isEmpty()) {
            return true;
        }

        IIdentityPiece personalInfo = null;
        for (IIdentityPiece identityPiece : identity.getIdentityPieces()) {
            if (IIdentityPiece.TYPE_PERSONAL_INFORMATION == identityPiece.getPieceType()) {
                personalInfo = identityPiece;
                break;
            }
        }
        if (personalInfo == null) {
            return true;
        }

        WatchListQuery query = new WatchListQuery(personalInfo.getFirstname(), personalInfo.getLastname());
        WatchListResult result = ctx.searchWatchList(query);

        if (WatchListResult.RESULT_TYPE_WATCHLIST_SEARCHED != result.getResultType() || result.getMatches().size() == 0) {
            return true;
        }

        return !checkWatchListResult(result);
    }

    private boolean checkWatchListResult(WatchListResult result) {
        List<WatchListMatch> matches = result.getMatches();
        boolean banned = false;
        for (WatchListMatch match : matches) {
            if (match.getScore() == 100) { //both, first name and last name matched.
                banned = true;
                break;
            }
        }
        return banned;
    }

    @Override
    public Map<String, String> onTransactionCreated(ITransactionDetails transactionDetails) {
        return null;
    }

    @Override
    public Map<String, String> onTransactionUpdated(ITransactionDetails transactionDetails) {
        return null;
    }
}
