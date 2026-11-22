package ua.lab2.tariffs.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Applies a compiled XSL stylesheet to XML files. */
public class XslTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(XslTransformer.class);

    private final Templates templates;

    public XslTransformer(URL stylesheetUrl) throws XmlProcessingException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            this.templates = factory.newTemplates(new StreamSource(stylesheetUrl.toExternalForm()));
        } catch (TransformerException e) {
            throw new XmlProcessingException("Cannot compile stylesheet " + stylesheetUrl, e);
        }
    }

    /** Transforms {@code input} and writes the result to {@code output}, creating parent folders. */
    public void transform(Path input, Path output) throws XmlProcessingException {
        LOG.info("Transforming {} -> {}", input, output);
        try {
            Files.createDirectories(output.toAbsolutePath().getParent());
            Transformer transformer = templates.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            try (OutputStream out = Files.newOutputStream(output)) {
                transformer.transform(new StreamSource(input.toUri().toString()), new StreamResult(out));
            }
        } catch (IOException | TransformerException e) {
            throw new XmlProcessingException("Cannot transform " + input + ": " + e.getMessage(), e);
        }
        LOG.info("Transformation done");
    }
}
