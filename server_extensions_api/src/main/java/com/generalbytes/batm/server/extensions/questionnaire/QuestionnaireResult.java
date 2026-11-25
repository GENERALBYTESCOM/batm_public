/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.questionnaire;

import java.util.List;

/**
 * An object representing data about questionnaire result.
 */
public class QuestionnaireResult {

    /**
     * ID of the questionnaire that was answered. Unlike the name, it does not change when the questionnaire is renamed.
     * May be null if the questionnaire has been deleted on the server.
     */
    private Long questionnaireId;

    private String questionnaireName;

    private List<QuestionAnswer> answers;

    public QuestionnaireResult() {

    }

    public QuestionnaireResult(String questionnaireName, List<QuestionAnswer> answers) {
        this(null, questionnaireName, answers);
    }

    public QuestionnaireResult(Long questionnaireId, String questionnaireName, List<QuestionAnswer> answers) {
        this.questionnaireId = questionnaireId;
        this.questionnaireName = questionnaireName;
        this.answers = answers;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireName() {
        return questionnaireName;
    }

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }
}
