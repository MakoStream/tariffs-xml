package ua.lab2.tariffs.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ua.lab2.tariffs.model.Tariff;

/** Template for all parsers: opening the file, logging and error translation live here once. */
abstract class AbstractTariffParser implements TariffParser {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final List<Tariff> parse(Path xmlFile) throws TariffParseException {
        log.info("Parsing {} with {}", xmlFile, getClass().getSimpleName());
        try (InputStream in = Files.newInputStream(xmlFile)) {
            List<Tariff> tariffs = doParse(in);
            log.info("Parsed {} tariffs", tariffs.size());
            return tariffs;
        } catch (IOException | SAXException | ParserConfigurationException | XMLStreamException
                 | IllegalArgumentException e) {
            log.error("Failed to parse {}: {}", xmlFile, e.getMessage());
            throw new TariffParseException("Cannot parse " + xmlFile, e);
        }
    }

    abstract List<Tariff> doParse(InputStream in)
            throws IOException, SAXException, ParserConfigurationException, XMLStreamException;
}
