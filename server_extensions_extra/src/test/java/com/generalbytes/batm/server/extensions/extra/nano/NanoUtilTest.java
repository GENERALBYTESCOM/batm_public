package com.generalbytes.batm.server.extensions.extra.nano;

import junit.framework.TestCase;
import org.junit.Test;
import uk.oczadly.karl.jnano.model.NanoAccount;

import static org.junit.Assert.*;

/**
 * @author Karl Oczadly
 */
public class NanoUtilTest {

    @Test
    public void testParseAddress() {
        NanoAccount account = NanoAccount.parse("nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz");

        assertEquals(account, NanoUtil.parseAddress( // Standard nano address
                "nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(account, NanoUtil.parseAddress( // Standard xrb address
                "xrb_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(account, NanoUtil.parseAddress( // URI
                "nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz"));
        assertEquals(account, NanoUtil.parseAddress( // URI with query
                "nano:nano_34qjpc8t1u6wnb584pc4iwsukwa8jhrobpx4oea5gbaitnqafm6qsgoacpiz?amount=420"));
    }

}