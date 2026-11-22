package ua.lab2.tariffs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ua.lab2.tariffs.xml.XmlProcessingException;

class TariffAppTest {

    @Test
    void runsWholeWorkflow(@TempDir Path dir) throws Exception {
        Path output = dir.resolve("out/grouped.xml");

        int code = TariffApp.createDefault().run(TestData.sampleXml(), output);

        assertEquals(0, code);
        assertTrue(Files.exists(output));
    }

    @Test
    void returnsOneAndWritesNothingForInvalidInput(@TempDir Path dir) throws Exception {
        Path input = TestData.write(dir, "bad.xml",
                TestData.document(TestData.tariff("tariff-1", "Op", "-1", "PER_MINUTE")));
        Path output = dir.resolve("out.xml");

        assertEquals(1, TariffApp.createDefault().run(input, output));
        assertFalse(Files.exists(output));
    }

    @Test
    void returnsOneForMissingInput(@TempDir Path dir) throws Exception {
        assertEquals(1, TariffApp.createDefault().run(dir.resolve("missing.xml"), dir.resolve("out.xml")));
    }

    @Test
    void reportsMissingClasspathResource() {
        assertThrows(XmlProcessingException.class, () -> TariffApp.resource("/no-such-file.xsd"));
    }
}
