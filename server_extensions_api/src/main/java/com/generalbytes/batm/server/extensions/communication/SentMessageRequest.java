package com.generalbytes.batm.server.extensions.communication;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents a request to send a message.
 * <p>
 * A provider that only needs to send plain text can rely on {@link #getMessageText()} alone —
 * together with {@link #getCredentials()} and {@link #getPhoneNumber()}, this is the go-to
 * combination for the vast majority of providers; no further handling is required.
 * <p>
 * Providers that support pre-approved message templates (e.g. WhatsApp Business API) instead
 * resolve {@link #getTemplateContent()} against {@link #getPlaceholders()} themselves. Note that
 * {@link #getTemplateContent()} contains placeholders wrapped in braces, e.g.
 * {@code "Your code is {text.otp}"}, while {@link #getPlaceholders()} keys are given WITHOUT
 * braces, e.g. {@code "text.otp" -> "482917"}. A naive {@code templateContent.replace(key, value)}
 * will not match — the resolved value would stay stuck between {@code "{...}"}. Wrap the key back
 * in braces first:
 * <pre>{@code
 * String resolved = request.getTemplateContent();
 * for (Map.Entry<String, String> e : request.getPlaceholders().entrySet()) {
 *     resolved = resolved.replace("{" + e.getKey() + "}", e.getValue());
 * }
 * // resolved now equals request.getMessageText()
 * }</pre>
 */
@Data
@NoArgsConstructor
public class SentMessageRequest {

    @Nonnull
    private String credentials;

    @Nonnull
    private String phoneNumber;

    /**
     * CAS custom string key, e.g. {@code "sms_otp"}. Nullable for ad-hoc messages.
     * Identifies which template {@link #getTemplateContent()} was resolved from.
     */
    @Nullable
    private String templateName;

    /**
     * Unresolved template content, with placeholders such as {@code {text.otp}}.
     * This is the custom string configured for {@link #getTemplateName()}, resolved for
     * {@link #getCustomerLanguage()} (or the organization's default language if not set/found).
     */
    @Nullable
    private String templateContent;

    /**
     * Resolved variable values keyed by placeholder name, WITHOUT surrounding braces —
     * e.g. {@code {"text.otp": "482917"}} resolves the {@code {text.otp}} placeholder in
     * {@link #getTemplateContent()}. See the class-level javadoc for a worked example.
     */
    @Nullable
    private Map<String, String> placeholders;

    /**
     * Customer's language. May be null.
     * <p>
     * Determines which localized version of {@link #getTemplateContent()} is returned for the
     * template identified by {@link #getTemplateName()}.
     * <p>
     * Format follows {@code com.generalbytes.batm.server.Language}, e.g. {@code "en"} or
     * {@code "de_CH"} (underscore before the region).
     */
    @Nullable
    private String customerLanguage;

    /**
     * Fully resolved message text — {@link #getTemplateContent()} with {@link #getPlaceholders()}
     * substituted in, or plain ad-hoc text when there's no template. This is the go-to value: for
     * ordinary message sending, {@link #getCredentials()}, {@link #getPhoneNumber()} and this
     * field are all that's needed.
     */
    @Nonnull
    private String messageText;

}
