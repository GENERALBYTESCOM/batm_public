package com.generalbytes.batm.server.extensions.aml;

/**
 * Object implementing this interface should be returned by IAMlProvider when validateIdentity is called
 */
public interface IIdentityValidationResult {

    /**
     * The Score, the Identity is valid from.
     */
    double VALID_IDENTITY_SCORE_THRESHOLD = 0.8;

    /**
     * Contains unique id of a validation check.
     * @return
     */
    String getUid();

    /**
     * Can have value from 0.0-1.0 where 1.0 == 100% certainty that identity information is valid(correct).
     * Server will consider identity information valid when score is higher then 80% (0.8)
     * @return
     */
    double getScore();

    /**
     * Description can contain textual description of reason why identity information got low score
     * @return
     */
    String getDescription();
}
