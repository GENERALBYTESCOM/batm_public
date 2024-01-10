/*************************************************************************************
 * Copyright (C) 2014-2023 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.quiz;

import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinition;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;

/**
 * An object representing the question and answer data in the {@link QuizResult}.
 */
public class QuestionAnswer {

    /**
     * ID of {@link CustomFieldDefinition} representing a question.
     * May be null if the Custom Field has been deleted on the server.
     */
    private Long customFieldDefinitionId;

    private String question;

    /**
     * ID of {@link CustomFieldDefinition.Element} representing an answer for a type {@link CustomFieldDefinitionType#DROPDOWN} and {@link CustomFieldDefinitionType#RADIO_BTN}.
     * May be null if it is not one of the above types or if the Custom Field Element has been deleted on the server.
     */
    private Long customFieldElementId;

    private String answer;

    public QuestionAnswer() {

    }

    public QuestionAnswer(Long customFieldDefinitionId, String question, Long customFieldElementId, String answer) {
        this.customFieldDefinitionId = customFieldDefinitionId;
        this.question = question;
        this.customFieldElementId = customFieldElementId;
        this.answer = answer;
    }

    public Long getCustomFieldDefinitionId() {
        return customFieldDefinitionId;
    }

    public void setCustomFieldDefinitionId(Long customFieldDefinitionId) {
        this.customFieldDefinitionId = customFieldDefinitionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getCustomFieldElementId() {
        return customFieldElementId;
    }

    public void setCustomFieldElementId(Long customFieldElementId) {
        this.customFieldElementId = customFieldElementId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
