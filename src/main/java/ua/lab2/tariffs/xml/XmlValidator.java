package ua.lab2.tariffs.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Validates XML documents against an XSD schema and reports every violation found. */
public class XmlValidator {

    private static final Logger LOG = LoggerFactory.getLogger(XmlValidator.class);

    private final Schema schema;

    public XmlValidator(URL schemaUrl) throws XmlProcessingException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = factory.newSchema(schemaUrl);
        } catch (SAXException e) {
            throw new XmlProcessingException("Cannot load schema " + schemaUrl, e);
        }
    }

    /**
     * Validates the document.
     *
     * @throws XmlProcessingException if the file is unreadable, malformed or violates the schema;
     *                                the message lists all violations with line numbers
     */
    public void validate(Path xmlFile) throws XmlProcessingException {
        LOG.info("Validating {}", xmlFile);
        List<String> errors = new ArrayList<>();
        Validator validator = schema.newValidator();
        validator.setErrorHandler(new CollectingErrorHandler(errors));
        try (InputStream in = Files.newInputStream(xmlFile)) {
            validator.validate(new StreamSource(in));
        } catch (IOException | SAXException e) {
            throw new XmlProcessingException("Cannot validate " + xmlFile + ": " + e.getMessage(), e);
        }
        if (!errors.isEmpty()) {
            LOG.error("{} is invalid: {} violation(s)", xmlFile, errors.size());
            throw new XmlProcessingException(
                    xmlFile + " is invalid:" + System.lineSeparator() + String.join(System.lineSeparator(), errors));
        }
        LOG.info("{} is valid", xmlFile);
    }

    private record CollectingErrorHandler(List<String> errors) implements ErrorHandler {

        @Override
        public void warning(SAXParseException e) {
            LOG.warn("Validation warning: {}", describe(e));
        }

        @Override
        public void error(SAXParseException e) {
            errors.add(describe(e));
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

        private static String describe(SAXParseException e) {
            return "line " + e.getLineNumber() + ": " + e.getMessage();
        }
    }
}
