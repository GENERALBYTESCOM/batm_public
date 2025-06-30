package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.Country;
import com.generalbytes.batm.server.extensions.CountryRegion;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class providing methods to parse and retrieve region codes based on state and country information.
 * This class simplifies mapping of state/province names to their corresponding ISO region codes.
 * It operates by comparing input state names against known region data for a specified country's ISO code.
 * If no match is found, the original state name is returned.
 */
@UtilityClass
public class CountryRegionNameParser {

    /**
     * Retrieves the region code (ISO) corresponding to a given state and 3-letter ISO country code.
     * If the country does not exist or has no regions, or the state does not match any known region,
     * the original state is returned.
     *
     * @param state           the name of the state or province for which the region code is to be retrieved
     * @param iso3CountryCode the 3-letter ISO country code of the associated country
     * @return the ISO region code if matched; otherwise, the original state name
     */
    public static String getRegionCodeFromCountry(String state, String iso3CountryCode) {
        Country country = Country.getByIso3(iso3CountryCode);
        if (country == null) {
            return state;
        }
        if (country.hasRegions()) {
            for (CountryRegion region : country.getRegions()) {
                if (StringUtils.compareIgnoreCase(region.getProvinceName(), state) == 0) {
                    return region.getIso();
                }
            }
        }
        return state;
    }
}
