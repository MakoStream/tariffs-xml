package ua.lab2.tariffs.parser;

import java.nio.file.Path;
import java.util.List;
import ua.lab2.tariffs.model.Tariff;

/** Reads tariffs from an XML file. */
public interface TariffParser {

    /**
     * Parses the given XML file.
     *
     * @param xmlFile path to the XML document
     * @return tariffs in document order
     * @throws TariffParseException if the file cannot be read or parsed
     */
    List<Tariff> parse(Path xmlFile) throws TariffParseException;
}
