package ua.lab2.tariffs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/** Shared helpers that build test documents. */
public final class TestData {

    private TestData() {
    }

    /** The sample document shipped with the application (6 tariffs, 3 operators). */
    public static Path sampleXml() {
        try {
            return Path.of(TestData.class.getResource("/tariffs.xml").toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static URL schema() {
        return TestData.class.getResource("/tariffs.xsd");
    }

    public static URL stylesheet() {
        return TestData.class.getResource("/tariffs-by-operator.xsl");
    }

    /** A valid tariff element; arguments let a test break exactly one thing. */
    public static String tariff(String id, String operator, String payroll, String tarification) {
        return """
                <Tariff id="%s" status="ACTIVE">
                    <Name>Test</Name>
                    <OperatorName>%s</OperatorName>
                    <Payroll>%s</Payroll>
                    <CallPrices>
                        <Inside>1.00</Inside>
                        <Outside>2.00</Outside>
                        <Landline>3.00</Landline>
                    </CallPrices>
                    <SmsPrice>0.50</SmsPrice>
                    <Parameters>
                        <FavoriteNumbers>2</FavoriteNumbers>
                        <Tarification>%s</Tarification>
                        <ConnectionFee>10.00</ConnectionFee>
                    </Parameters>
                </Tariff>
                """.formatted(id, operator, payroll, tarification);
    }

    public static String tariff(String id) {
        return tariff(id, "Op", "10.00", "PER_MINUTE");
    }

    public static String document(String... tariffs) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Tariffs xmlns=\"http://lab2.ua/tariffs\">\n"
                + String.join("", tariffs) + "</Tariffs>\n";
    }

    public static Path write(Path dir, String fileName, String content) {
        try {
            return Files.writeString(dir.resolve(fileName), content);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
