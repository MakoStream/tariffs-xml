package ua.lab2.tariffs.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ua.lab2.tariffs.TestData;

class XslTransformerTest {

    private static Element root(Path xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(xml.toFile()).getDocumentElement();
    }

    private static List<Element> children(Element parent) {
        List<Element> result = new ArrayList<>();
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element element) {
                result.add(element);
            }
        }
        return result;
    }

    @Test
    void groupsTariffsByOperator(@TempDir Path dir) throws Exception {
        Path output = dir.resolve("nested/folder/grouped.xml");

        new XslTransformer(TestData.stylesheet()).transform(TestData.sampleXml(), output);

        Element root = root(output);
        assertEquals("Tariffs", root.getLocalName());
        List<Element> groups = children(root);
        assertEquals(List.of("Kyivstar", "Vodafone", "lifecell"), groups.stream().map(Element::getLocalName).toList());
        for (Element group : groups) {
            assertEquals(2, children(group).size());
        }
        assertEquals(List.of("tariff-1", "tariff-2"),
                children(groups.get(0)).stream().map(e -> e.getAttribute("id")).toList());
    }

    @Test
    void keepsTariffContentUnchanged(@TempDir Path dir) throws Exception {
        Path output = dir.resolve("grouped.xml");

        new XslTransformer(TestData.stylesheet()).transform(TestData.sampleXml(), output);

        Element tariff = children(children(root(output)).get(0)).get(0);
        assertEquals("ACTIVE", tariff.getAttribute("status"));
        assertEquals("Простий", children(tariff).get(0).getTextContent());
        assertEquals(6, children(tariff).size());
    }

    @Test
    void replacesSpacesInOperatorNames(@TempDir Path dir) throws Exception {
        Path input = TestData.write(dir, "in.xml",
                TestData.document(TestData.tariff("tariff-1", "Big Op", "10.00", "PER_MINUTE")));
        Path output = dir.resolve("out.xml");

        new XslTransformer(TestData.stylesheet()).transform(input, output);

        assertEquals("Big_Op", children(root(output)).get(0).getLocalName());
        assertTrue(Files.size(output) > 0);
    }

    @Test
    void failsForMissingInput(@TempDir Path dir) throws Exception {
        XslTransformer transformer = new XslTransformer(TestData.stylesheet());

        assertThrows(XmlProcessingException.class,
                () -> transformer.transform(dir.resolve("missing.xml"), dir.resolve("out.xml")));
    }

    @Test
    void failsForBrokenStylesheet(@TempDir Path dir) throws Exception {
        Path broken = TestData.write(dir, "broken.xsl", "<notAStylesheet/>");

        assertThrows(XmlProcessingException.class, () -> new XslTransformer(broken.toUri().toURL()));
    }
}
