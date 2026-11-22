package ua.lab2.tariffs.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import ua.lab2.tariffs.model.CallPrices;
import ua.lab2.tariffs.model.Parameters;
import ua.lab2.tariffs.model.Status;
import ua.lab2.tariffs.model.Tarification;
import ua.lab2.tariffs.model.Tariff;

/** Builds one {@link Tariff} from tag/text pairs; the only place that knows the field mapping. */
final class TariffAssembler {

    private final Tariff tariff = new Tariff();
    private final CallPrices callPrices = new CallPrices();
    private final Parameters parameters = new Parameters();

    TariffAssembler(String id, String status) {
        tariff.setId(id);
        tariff.setStatus(Status.fromValue(status));
        tariff.setCallPrices(callPrices);
        tariff.setParameters(parameters);
    }

    void accept(TariffTag tag, String text) {
        switch (tag) {
            case NAME -> tariff.setName(text);
            case OPERATOR_NAME -> tariff.setOperatorName(text);
            case PAYROLL -> tariff.setPayroll(new BigDecimal(text));
            case INSIDE -> callPrices.setInside(new BigDecimal(text));
            case OUTSIDE -> callPrices.setOutside(new BigDecimal(text));
            case LANDLINE -> callPrices.setLandline(new BigDecimal(text));
            case SMS_PRICE -> tariff.setSmsPrice(new BigDecimal(text));
            case FAVORITE_NUMBERS -> parameters.setFavoriteNumbers(new BigInteger(text));
            case TARIFICATION -> parameters.setTarification(Tarification.fromValue(text));
            case CONNECTION_FEE -> parameters.setConnectionFee(new BigDecimal(text));
            default -> throw new IllegalArgumentException("Tag has no text value: " + tag);
        }
    }

    Tariff build() {
        return tariff;
    }
}
