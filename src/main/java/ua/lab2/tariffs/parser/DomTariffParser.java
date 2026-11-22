package ua.lab2.tariffs.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ua.lab2.tariffs.model.Tariff;

/** DOM (in-memory tree) implementation. */
public class DomTariffParser extends AbstractTariffParser {

    @Override
    List<Tariff> doParse(InputStream in) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        NodeList nodes = factory.newDocumentBuilder().parse(in)
                .getElementsByTagNameNS(TariffTag.NAMESPACE, "Tariff");
        List<Tariff> tariffs = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            TariffAssembler assembler = new TariffAssembler(
                    element.getAttribute("id"), element.getAttribute("status"));
            fill(assembler, element);
            tariffs.add(assembler.build());
        }
        return tariffs;
    }

    private void fill(TariffAssembler assembler, Element parent) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element element) {
                TariffTag.of(element.getLocalName()).ifPresent(tag -> {
                    if (tag.isLeaf()) {
                        assembler.accept(tag, element.getTextContent().trim());
                    } else {
                        fill(assembler, element);
                    }
                });
            }
        }
    }
}
