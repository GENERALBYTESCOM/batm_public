package com.generalbytes.batm.server.extensions.extra.examples.identity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.generalbytes.batm.server.extensions.customfields.CustomField;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinition;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityListener;
import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityApplicant;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionAvailability;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class ExampleIdentityListener implements IIdentityListener {
    Logger log = LoggerFactory.getLogger(ExampleIdentityListener.class);

    @Override
    public void onVerificationResult(String rawPayload, ApplicantCheckResult result) {
        WebhookData data = parsePayload(rawPayload);
        IExtensionContext ctx = IdentityExampleExtension.getExtensionContext();
        IdentityApplicant applicant = ctx.findIdentityVerificationApplicant(result.getIdentityApplicantId());
        IIdentity identity = applicant.getIdentity();

        // get custom fields of the identity and print the values
        for (CustomField customField : ctx.getIdentityCustomFields(identity.getPublicId())) {
            log.info("Identity {} custom field name {}, type {} = {}",
                identity.getPublicId(),
                customField.getDefinition().getName(),
                customField.getDefinition().getType(),
                getValueText(customField));
        }

        // get all identity-type custom fields configured for the organization of this identity (field definitions, not values)
        Collection<CustomFieldDefinition> customFieldDefinitions = ctx.getCustomFieldDefinitions(
            identity.getOrganization().getId(),
            CustomFieldDefinitionAvailability.IDENTITY);

        log.info("Identity custom fields defined: {}",
            customFieldDefinitions.stream()
                .map(d -> d.getName() + " - " + d.getType())
                .collect(Collectors.joining(", ")));

        // Set a custom field value:
        // we need to find the right custom field definition first
        CustomFieldDefinition decisionTimeCustomFieldDefinition = findCustomFieldDefinition(
            customFieldDefinitions,
            "decision_time",
            CustomFieldDefinitionType.SINGLE_LINE);

        // set the string value for the selected custom field and the identity
        ctx.setIdentityCustomField(
            identity.getPublicId(),
            decisionTimeCustomFieldDefinition.getId(),
            CustomFieldValue.string(data.verification.decisionTime));

        // select another custom field definition, this time a dropdown type
        CustomFieldDefinition documentCountryCustomFieldDefinition = findCustomFieldDefinition(
            customFieldDefinitions,
            "document_country",
            CustomFieldDefinitionType.DROPDOWN);

        // Dropdown (and radio button) typed custom fields have a list of possible options to chose from.
        // All the possible options are available using the getElements() method.
        // Try to find an option that has the same text as the document country received in the data from the verification provider
        documentCountryCustomFieldDefinition.getElements().stream()
            .filter(element -> Objects.equals(element.getValue(), data.verification.document.country))
            .findAny()
            // if we found an option (element) with the right value, we will set it to be the custom field's value for this identity
            .ifPresent(element ->
                ctx.setIdentityCustomField(
                    identity.getPublicId(),
                    documentCountryCustomFieldDefinition.getId(),
                    CustomFieldValue.choice(element.getId())));
    }

    private CustomFieldDefinition findCustomFieldDefinition(Collection<CustomFieldDefinition> customFieldDefinitions,
                                                            String name,
                                                            CustomFieldDefinitionType type) {
        return customFieldDefinitions.stream()
            .filter(d -> name.equals(d.getName()) && type == d.getType())
            .findAny()
            .orElseThrow(() -> new RuntimeException("custom field definition not found"));
    }

    private WebhookData parsePayload(String rawPayload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return mapper.readValue(rawPayload, WebhookData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static class WebhookData {
        public Verification verification;

        public static class Verification {
            public String decisionTime;
            public Document document;

            public static class Document {
                public String country;
            }
        }
    }

    private String getValueText(CustomField field) {
        switch (field.getDefinition().getType()) {
            case SINGLE_LINE:
            case PARAGRAPH:
                return field.getValue().getStringValue();

            case CHECKBOX:
                return Objects.toString(field.getValue().getBooleanValue());

            case RADIO_BTN:
            case DROPDOWN:
                return field.getDefinition().getElements().stream()
                    .filter(element -> Objects.equals(element.getId(), field.getValue().getChoiceId()))
                    .findAny()
                    .map(CustomFieldDefinition.Element::getValue)
                    .orElse("(?)");

            case DOCUMENT:
            case IMAGE:
                return "(file)";

            default:
                throw new IllegalArgumentException("unexpected definition type");
        }
    }
}
