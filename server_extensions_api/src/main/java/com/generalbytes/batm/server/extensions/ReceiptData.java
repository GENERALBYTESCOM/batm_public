package com.generalbytes.batm.server.extensions;

import java.util.Map;

/**
 * Represents data necessary to generate a receipt message.
 * This interface provides access to placeholder variables and embedded images
 * that may be used within a receipt.
 */
public interface ReceiptData {

    /**
     * Retrieves a map of placeholder variables for the receipt.
     * These variables contain values for all placeholders available for constructing
     * a receipt message.
     *
     * <p>Each entry in the map represents a placeholder's name and its corresponding value.
     * Placeholders are identified by their names without brackets. For example, a key
     * {@code "text.crypto.amount"} corresponds to the value for the placeholder
     * {@code {text.crypto.amount}} in the receipt template.</p>
     *
     * @return A map containing placeholder names as keys and their corresponding values.
     */
    Map<String, String> getVariables();

    /**
     * Retrieves an array of embedded images to be included in the receipt email.
     *
     * <p>This method is specifically intended for email receipts
     * ({@link ReceiptTransferMethod#EMAIL}), where embedded images are used.</p>
     *
     * @return An array of {@link IExtensionContext.EmbeddedEmailImage} objects,
     * representing the images to embed in the email.
     * @see IExtensionContext#sendHTMLMailAsync(String, String, String, String, String, IExtensionContext.EmbeddedEmailImage...)
     */
    IExtensionContext.EmbeddedEmailImage[] getEmbeddedEmailImages();

}
