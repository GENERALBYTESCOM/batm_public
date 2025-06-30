package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;

import java.util.Map;

@Getter
public class QuestionnaireItems extends JsonObject {
    private Map<String, QuestionnaireAnswer> items;
}
