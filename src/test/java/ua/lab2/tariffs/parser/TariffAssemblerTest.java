package ua.lab2.tariffs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import ua.lab2.tariffs.model.Status;
import ua.lab2.tariffs.model.Tarification;
import ua.lab2.tariffs.model.Tariff;

class TariffAssemblerTest {

    @Test
    void buildsTariffFromTagValues() {
        TariffAssembler assembler = new TariffAssembler("tariff-9", "ARCHIVED");
        assembler.accept(TariffTag.NAME, "Test");
        assembler.accept(TariffTag.OPERATOR_NAME, "Op");
        assembler.accept(TariffTag.PAYROLL, "100.50");
        assembler.accept(TariffTag.INSIDE, "1.10");
        assembler.accept(TariffTag.OUTSIDE, "2.20");
        assembler.accept(TariffTag.LANDLINE, "3.30");
        assembler.accept(TariffTag.SMS_PRICE, "0.40");
        assembler.accept(TariffTag.FAVORITE_NUMBERS, "3");
        assembler.accept(TariffTag.TARIFICATION, "TWELVE_SECOND");
        assembler.accept(TariffTag.CONNECTION_FEE, "15.00");

        Tariff tariff = assembler.build();

        assertEquals("tariff-9", tariff.getId());
        assertEquals(Status.ARCHIVED, tariff.getStatus());
        assertEquals("Test", tariff.getName());
        assertEquals("Op", tariff.getOperatorName());
        assertEquals(new BigDecimal("100.50"), tariff.getPayroll());
        assertEquals(new BigDecimal("1.10"), tariff.getCallPrices().getInside());
        assertEquals(new BigDecimal("2.20"), tariff.getCallPrices().getOutside());
        assertEquals(new BigDecimal("3.30"), tariff.getCallPrices().getLandline());
        assertEquals(new BigDecimal("0.40"), tariff.getSmsPrice());
        assertEquals(BigInteger.valueOf(3), tariff.getParameters().getFavoriteNumbers());
        assertEquals(Tarification.TWELVE_SECOND, tariff.getParameters().getTarification());
        assertEquals(new BigDecimal("15.00"), tariff.getParameters().getConnectionFee());
    }

    @Test
    void rejectsUnknownStatus() {
        assertThrows(IllegalArgumentException.class, () -> new TariffAssembler("tariff-1", "BROKEN"));
    }

    @Test
    void rejectsTagWithoutTextValue() {
        TariffAssembler assembler = new TariffAssembler("tariff-1", "ACTIVE");
        assertThrows(IllegalArgumentException.class, () -> assembler.accept(TariffTag.TARIFF, "x"));
    }
}
