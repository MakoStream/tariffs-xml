package ua.lab2.tariffs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class TariffTagTest {

    @Test
    void mapsCamelCaseNamesToTags() {
        assertEquals(Optional.of(TariffTag.OPERATOR_NAME), TariffTag.of("OperatorName"));
        assertEquals(Optional.of(TariffTag.TARIFF), TariffTag.of("Tariff"));
    }

    @Test
    void unknownNameGivesEmpty() {
        assertTrue(TariffTag.of("Extra").isEmpty());
    }

    @Test
    void onlyValueElementsAreLeaves() {
        assertTrue(TariffTag.NAME.isLeaf());
        assertFalse(TariffTag.TARIFF.isLeaf());
        assertFalse(TariffTag.CALL_PRICES.isLeaf());
    }
}
