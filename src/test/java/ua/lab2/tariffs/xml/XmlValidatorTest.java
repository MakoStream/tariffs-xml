package ua.lab2.tariffs.xml;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.lab2.tariffs.TestData;

class XmlValidatorTest {

    private XmlValidator validator;

    @BeforeEach
    void setUp() throws XmlProcessingException {
        validator = new XmlValidator(TestData.schema());
    }

    private String violationsOf(Path dir, String... tariffs) {
        Path file = TestData.write(dir, "doc.xml", TestData.document(tariffs));
        return assertThrows(XmlProcessingException.class, () -> validator.validate(file)).getMessage();
    }

    @Test
    void acceptsSampleDocument() {
        assertDoesNotThrow(() -> validator.validate(TestData.sampleXml()));
    }

    @Test
    void rejectsNegativeAmountAndReportsLine(@TempDir Path dir) {
        String message = violationsOf(dir, TestData.tariff("tariff-1", "Op", "-5", "PER_MINUTE"));

        assertTrue(message.contains("Payroll"));
        assertTrue(message.contains("line "));
    }

    @Test
    void reportsEveryViolation(@TempDir Path dir) {
        String message = violationsOf(dir,
                TestData.tariff("tariff-1", "Op", "-5", "PER_MINUTE"),
                TestData.tariff("tariff-2", "Op", "10.00", "HOURLY"));

        assertTrue(message.contains("Payroll"));
        assertTrue(message.contains("Tarification"));
    }

    @Test
    void rejectsDuplicateIds(@TempDir Path dir) {
        String message = violationsOf(dir, TestData.tariff("tariff-1"), TestData.tariff("tariff-1"));

        assertTrue(message.contains("tariff-1"));
    }

    @Test
    void rejectsIdThatDoesNotMatchPattern(@TempDir Path dir) {
        assertTrue(violationsOf(dir, TestData.tariff("x1")).contains("x1"));
    }

    @Test
    void rejectsUnknownStatus(@TempDir Path dir) {
        String tariff = TestData.tariff("tariff-1").replace("status=\"ACTIVE\"", "status=\"BROKEN\"");

        assertTrue(violationsOf(dir, tariff).contains("BROKEN"));
    }

    @Test
    void rejectsMissingRequiredAttribute(@TempDir Path dir) {
        String tariff = TestData.tariff("tariff-1").replace(" status=\"ACTIVE\"", "");

        assertTrue(violationsOf(dir, tariff).contains("status"));
    }

    @Test
    void rejectsDocumentWithoutTariffs(@TempDir Path dir) {
        assertTrue(violationsOf(dir).contains("Tariffs"));
    }

    @Test
    void rejectsMalformedXml(@TempDir Path dir) {
        Path file = TestData.write(dir, "broken.xml", "<Tariffs");

        assertThrows(XmlProcessingException.class, () -> validator.validate(file));
    }

    @Test
    void rejectsMissingFile(@TempDir Path dir) {
        assertThrows(XmlProcessingException.class, () -> validator.validate(dir.resolve("missing.xml")));
    }

    @Test
    void rejectsSchemaThatIsNotASchema(@TempDir Path dir) throws Exception {
        Path notSchema = TestData.write(dir, "not.xsd", "<notASchema/>");

        assertThrows(XmlProcessingException.class, () -> new XmlValidator(notSchema.toUri().toURL()));
    }
}
