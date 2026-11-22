package ua.lab2.tariffs.parser;

import java.util.Locale;
import java.util.Optional;

/** Element names of the tariffs document, shared by all parsers. */
enum TariffTag {
    TARIFFS(false),
    TARIFF(false),
    NAME(true),
    OPERATOR_NAME(true),
    PAYROLL(true),
    CALL_PRICES(false),
    INSIDE(true),
    OUTSIDE(true),
    LANDLINE(true),
    SMS_PRICE(true),
    PARAMETERS(false),
    FAVORITE_NUMBERS(true),
    TARIFICATION(true),
    CONNECTION_FEE(true);

    static final String NAMESPACE = "http://lab2.ua/tariffs";

    private final boolean leaf;

    TariffTag(boolean leaf) {
        this.leaf = leaf;
    }

    /** Whether the element carries a text value. */
    boolean isLeaf() {
        return leaf;
    }

    /** Maps an XML local name such as {@code OperatorName} to a tag; unknown names give empty. */
    static Optional<TariffTag> of(String localName) {
        String constant = localName.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase(Locale.ROOT);
        try {
            return Optional.of(valueOf(constant));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
