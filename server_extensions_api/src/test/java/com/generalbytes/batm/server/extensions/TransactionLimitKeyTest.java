package com.generalbytes.batm.server.extensions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionLimitKeyTest {

    @ParameterizedTest
    @CsvSource({
            "PER_TRANSACTION, cptr",
            "PER_HOUR, cph",
            "PER_DAY, cpd",
            "PER_WEEK, cpw",
            "PER_MONTH, cpm",
            "PER_3_MONTHS, cp3m",
            "PER_6_MONTHS, cp6m",
            "PER_12_MONTHS, cp12m",
            "PER_QUARTER, cpq",
            "PER_YEAR, cpy",
            "TOTAL, ct"
    })
    void abbreviationAndKeys(TransactionLimitKey key, String abbreviation) {
        assertThat(key.getAbbreviation()).isEqualTo(abbreviation);
        assertThat(key.key()).isEqualTo(abbreviation);
        assertThat(key.buyKey()).isEqualTo("buy_" + abbreviation);
        assertThat(key.sellKey()).isEqualTo("sell_" + abbreviation);
    }

    @Test
    void abbreviationsAreUnique() {
        Set<String> abbreviations = Arrays.stream(TransactionLimitKey.values())
                .map(TransactionLimitKey::getAbbreviation)
                .collect(Collectors.toCollection(HashSet::new));

        assertThat(abbreviations).hasSize(TransactionLimitKey.values().length);
    }
}
