package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;

import java.util.Map;

@Getter
public class Questionnaire extends JsonObject {
    private String id;
    private Map<String, QuestionnaireItems> sections;
}
