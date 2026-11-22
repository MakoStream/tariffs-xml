package ua.lab2.tariffs.sort;

import java.util.Comparator;
import ua.lab2.tariffs.model.Tariff;

/** Ready-made orderings for tariffs; use as {@code list.sort(TariffComparator.BY_PAYROLL)}. */
public enum TariffComparator implements Comparator<Tariff> {
    BY_NAME(Comparator.comparing(Tariff::getName, String.CASE_INSENSITIVE_ORDER)),
    BY_OPERATOR(Comparator.comparing(Tariff::getOperatorName, String.CASE_INSENSITIVE_ORDER)),
    BY_PAYROLL(Comparator.comparing(Tariff::getPayroll)),
    BY_SMS_PRICE(Comparator.comparing(Tariff::getSmsPrice)),
    BY_OPERATOR_THEN_PAYROLL(
            Comparator.comparing(Tariff::getOperatorName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Tariff::getPayroll));

    private final Comparator<Tariff> delegate;

    TariffComparator(Comparator<Tariff> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(Tariff a, Tariff b) {
        return delegate.compare(a, b);
    }
}
