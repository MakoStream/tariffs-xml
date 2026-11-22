package ua.lab2.tariffs.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ua.lab2.tariffs.model.Tariff;

/** SAX (push, event-based) implementation. */
public class SaxTariffParser extends AbstractTariffParser {

    @Override
    List<Tariff> doParse(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        TariffCollector collector = new TariffCollector();
        factory.newSAXParser().parse(in, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                collector.startElement(localName, attributes::getValue);
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                collector.characters(new String(ch, start, length));
            }

            @Override
            public void endElement(String uri, String localName, String qName) {
                collector.endElement(localName);
            }
        });
        return collector.tariffs();
    }
}
