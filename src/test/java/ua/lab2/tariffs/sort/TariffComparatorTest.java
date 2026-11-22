package ua.lab2.tariffs.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import ua.lab2.tariffs.model.Tariff;

class TariffComparatorTest {

    private static Tariff tariff(String name, String operator, String payroll, String sms) {
        Tariff tariff = new Tariff();
        tariff.setName(name);
        tariff.setOperatorName(operator);
        tariff.setPayroll(new BigDecimal(payroll));
        tariff.setSmsPrice(new BigDecimal(sms));
        return tariff;
    }

    private static List<String> names(TariffComparator order, Tariff... tariffs) {
        List<Tariff> list = new ArrayList<>(List.of(tariffs));
        list.sort(order);
        return list.stream().map(Tariff::getName).toList();
    }

    @Test
    void byNameIgnoresCase() {
        assertEquals(List.of("Alpha", "beta", "Carol"),
                names(TariffComparator.BY_NAME,
                        tariff("beta", "X", "1", "1"), tariff("Carol", "X", "1", "1"), tariff("Alpha", "X", "1", "1")));
    }

    @Test
    void byOperatorIgnoresCase() {
        assertEquals(List.of("a", "b", "c"),
                names(TariffComparator.BY_OPERATOR,
                        tariff("c", "Vodafone", "1", "1"), tariff("a", "kyivstar", "1", "1"),
                        tariff("b", "Lifecell", "1", "1")));
    }

    @Test
    void byPayrollComparesNumbersNotText() {
        assertEquals(List.of("cheap", "mid", "dear"),
                names(TariffComparator.BY_PAYROLL,
                        tariff("dear", "X", "100", "1"), tariff("cheap", "X", "9.50", "1"),
                        tariff("mid", "X", "20", "1")));
    }

    @Test
    void bySmsPrice() {
        assertEquals(List.of("free", "cheap", "dear"),
                names(TariffComparator.BY_SMS_PRICE,
                        tariff("dear", "X", "1", "2.00"), tariff("free", "X", "1", "0"),
                        tariff("cheap", "X", "1", "0.50")));
    }

    @Test
    void byOperatorThenPayroll() {
        assertEquals(List.of("k1", "k2", "v1", "v2"),
                names(TariffComparator.BY_OPERATOR_THEN_PAYROLL,
                        tariff("v2", "Vodafone", "300", "1"), tariff("k2", "Kyivstar", "200", "1"),
                        tariff("v1", "Vodafone", "100", "1"), tariff("k1", "Kyivstar", "50", "1")));
    }
}
