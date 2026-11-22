package ua.lab2.tariffs.parser;

import java.io.InputStream;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import ua.lab2.tariffs.model.Tariff;

/** StAX (pull, cursor-based) implementation. */
public class StaxTariffParser extends AbstractTariffParser {

    @Override
    List<Tariff> doParse(InputStream in) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        XMLStreamReader reader = factory.createXMLStreamReader(in);
        TariffCollector collector = new TariffCollector();
        try {
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> collector.startElement(
                            reader.getLocalName(), name -> reader.getAttributeValue(null, name));
                    case XMLStreamConstants.CHARACTERS -> collector.characters(reader.getText());
                    case XMLStreamConstants.END_ELEMENT -> collector.endElement(reader.getLocalName());
                    default -> { }
                }
            }
        } finally {
            reader.close();
        }
        return collector.tariffs();
    }
}
