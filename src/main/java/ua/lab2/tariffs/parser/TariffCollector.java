package ua.lab2.tariffs.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/** Turns a stream of start/text/end events into tariffs; shared by the SAX and StAX parsers. */
final class TariffCollector {

    private final List<ua.lab2.tariffs.model.Tariff> tariffs = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private TariffAssembler current;

    void startElement(String localName, UnaryOperator<String> attribute) {
        text.setLength(0);
        TariffTag.of(localName).filter(tag -> tag == TariffTag.TARIFF).ifPresent(
                tag -> current = new TariffAssembler(attribute.apply("id"), attribute.apply("status")));
    }

    void characters(String chars) {
        text.append(chars);
    }

    void endElement(String localName) {
        TariffTag.of(localName).ifPresent(tag -> {
            if (tag.isLeaf()) {
                current.accept(tag, text.toString().trim());
            } else if (tag == TariffTag.TARIFF) {
                tariffs.add(current.build());
            }
        });
    }

    List<ua.lab2.tariffs.model.Tariff> tariffs() {
        return tariffs;
    }
}
