package ua.lab2.tariffs;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.lab2.tariffs.model.Tariff;
import ua.lab2.tariffs.parser.DomTariffParser;
import ua.lab2.tariffs.parser.SaxTariffParser;
import ua.lab2.tariffs.parser.StaxTariffParser;
import ua.lab2.tariffs.parser.TariffParseException;
import ua.lab2.tariffs.parser.TariffParser;
import ua.lab2.tariffs.sort.TariffComparator;
import ua.lab2.tariffs.xml.XmlProcessingException;
import ua.lab2.tariffs.xml.XmlValidator;
import ua.lab2.tariffs.xml.XslTransformer;

/** The whole workflow: validate, parse with every parser, sort, transform. */
public class TariffApp {

    private static final Logger LOG = LoggerFactory.getLogger(TariffApp.class);
    private static final String SCHEMA = "/tariffs.xsd";
    private static final String STYLESHEET = "/tariffs-by-operator.xsl";

    private record Step(TariffParser parser, TariffComparator order) { }

    private final XmlValidator validator;
    private final XslTransformer transformer;
    private final List<Step> steps = List.of(
            new Step(new SaxTariffParser(), TariffComparator.BY_NAME),
            new Step(new DomTariffParser(), TariffComparator.BY_PAYROLL),
            new Step(new StaxTariffParser(), TariffComparator.BY_OPERATOR_THEN_PAYROLL));

    public TariffApp(XmlValidator validator, XslTransformer transformer) {
        this.validator = validator;
        this.transformer = transformer;
    }

    /** Builds the application with the schema and stylesheet bundled in the classpath. */
    public static TariffApp createDefault() throws XmlProcessingException {
        return new TariffApp(
                new XmlValidator(resource(SCHEMA)),
                new XslTransformer(resource(STYLESHEET)));
    }

    /**
     * Runs the workflow.
     *
     * @return 0 on success, 1 if the input is invalid or processing failed
     */
    public int run(Path input, Path output) {
        try {
            validator.validate(input);
            for (Step step : steps) {
                List<Tariff> tariffs = new ArrayList<>(step.parser().parse(input));
                tariffs.sort(step.order());
                LOG.info("{} sorted by {}:", step.parser().getClass().getSimpleName(), step.order());
                tariffs.forEach(tariff -> LOG.info("  {}", describe(tariff)));
            }
            transformer.transform(input, output);
            LOG.info("Grouped document written to {}", output);
            return 0;
        } catch (XmlProcessingException | TariffParseException e) {
            LOG.error("Processing failed: {}", e.getMessage());
            return 1;
        }
    }

    private static String describe(Tariff tariff) {
        return String.format("%-9s %-14s %-9s payroll=%s sms=%s",
                tariff.getId(), tariff.getName(), tariff.getOperatorName(),
                tariff.getPayroll(), tariff.getSmsPrice());
    }

    static URL resource(String name) throws XmlProcessingException {
        URL url = TariffApp.class.getResource(name);
        if (url == null) {
            throw new XmlProcessingException("Missing classpath resource " + name);
        }
        return url;
    }
}
