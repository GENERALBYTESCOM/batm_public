package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.extra.nano.NanoCurrencySpecification;
import com.generalbytes.batm.server.extensions.extra.nano.NanoExtension;
import org.junit.Test;
import uk.oczadly.karl.jnano.model.NanoAccount;

import static org.junit.Assert.assertEquals;

/**
 * @author Karl Oczadly
 */
public class NanoCurrencySpecificationTest {

    static final NanoCurrencySpecification SPEC = NanoExtension.CURRENCY_SPEC;
    static final NanoAccount ACCOUNT = NanoAccount.parse(
            "nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz");


    @Test
    public void testParseAddress() {
        assertEquals(ACCOUNT, SPEC.parseAddress( // Standard nano address
            "nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(ACCOUNT, SPEC.parseAddress( // Standard xrb address
            "xrb_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(ACCOUNT, SPEC.parseAddress( // URI
            "nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(ACCOUNT, SPEC.parseAddress( // URI with query
            "nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz?amount=420"));
    }

    @Test
    public void testUriEncode() {
        assertEquals("nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz",
                SPEC.toUriAddress(ACCOUNT));
    }

}