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
