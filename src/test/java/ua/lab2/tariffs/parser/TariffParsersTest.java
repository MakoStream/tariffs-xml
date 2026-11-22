package ua.lab2.tariffs.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import ua.lab2.tariffs.TestData;
import ua.lab2.tariffs.model.Status;
import ua.lab2.tariffs.model.Tarification;
import ua.lab2.tariffs.model.Tariff;

class TariffParsersTest {

    static Stream<TariffParser> parsers() {
        return Stream.of(new SaxTariffParser(), new DomTariffParser(), new StaxTariffParser());
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void parsesAllTariffsInDocumentOrder(TariffParser parser) throws Exception {
        List<Tariff> tariffs = parser.parse(TestData.sampleXml());

        assertEquals(List.of("tariff-1", "tariff-2", "tariff-3", "tariff-4", "tariff-5", "tariff-6"),
                tariffs.stream().map(Tariff::getId).toList());
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void mapsEveryField(TariffParser parser) throws Exception {
        Tariff tariff = parser.parse(TestData.sampleXml()).get(1);

        assertEquals("tariff-2", tariff.getId());
        assertEquals(Status.ACTIVE, tariff.getStatus());
        assertEquals("Максимум", tariff.getName());
        assertEquals("Kyivstar", tariff.getOperatorName());
        assertEquals(new BigDecimal("350.00"), tariff.getPayroll());
        assertEquals(new BigDecimal("0.00"), tariff.getCallPrices().getInside());
        assertEquals(new BigDecimal("0.00"), tariff.getCallPrices().getOutside());
        assertEquals(new BigDecimal("0.00"), tariff.getCallPrices().getLandline());
        assertEquals(new BigDecimal("0.00"), tariff.getSmsPrice());
        assertEquals(5, tariff.getParameters().getFavoriteNumbers().intValue());
        assertEquals(Tarification.TWELVE_SECOND, tariff.getParameters().getTarification());
        assertEquals(new BigDecimal("50.00"), tariff.getParameters().getConnectionFee());
    }

    @Test
    void allParsersProduceTheSameTariffs() throws Exception {
        List<String> sax = snapshots(new SaxTariffParser());
        assertEquals(sax, snapshots(new DomTariffParser()));
        assertEquals(sax, snapshots(new StaxTariffParser()));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void ignoresUnknownElements(TariffParser parser, @TempDir Path dir) throws Exception {
        String tariff = TestData.tariff("tariff-1").replace("<Name>", "<Extra>x</Extra><Name>");
        Path file = TestData.write(dir, "extra.xml", TestData.document(tariff));

        assertEquals(1, parser.parse(file).size());
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void failsOnMissingFile(TariffParser parser, @TempDir Path dir) {
        assertThrows(TariffParseException.class, () -> parser.parse(dir.resolve("missing.xml")));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void failsOnMalformedXml(TariffParser parser, @TempDir Path dir) {
        Path file = TestData.write(dir, "broken.xml", "<Tariffs xmlns=\"http://lab2.ua/tariffs\"><Tariff");

        assertThrows(TariffParseException.class, () -> parser.parse(file));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void failsOnUnknownEnumValue(TariffParser parser, @TempDir Path dir) {
        Path file = TestData.write(dir, "enum.xml",
                TestData.document(TestData.tariff("tariff-1", "Op", "10.00", "HOURLY")));

        assertThrows(TariffParseException.class, () -> parser.parse(file));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    void failsOnInvalidNumber(TariffParser parser, @TempDir Path dir) {
        Path file = TestData.write(dir, "number.xml",
                TestData.document(TestData.tariff("tariff-1", "Op", "abc", "PER_MINUTE")));

        assertThrows(TariffParseException.class, () -> parser.parse(file));
    }

    private static List<String> snapshots(TariffParser parser) throws TariffParseException {
        return parser.parse(TestData.sampleXml()).stream()
                .map(t -> String.join("|", t.getId(), t.getStatus().toString(), t.getName(),
                        t.getOperatorName(), t.getPayroll().toPlainString(),
                        t.getCallPrices().getInside().toPlainString(),
                        t.getCallPrices().getOutside().toPlainString(),
                        t.getCallPrices().getLandline().toPlainString(),
                        t.getSmsPrice().toPlainString(),
                        t.getParameters().getFavoriteNumbers().toString(),
                        t.getParameters().getTarification().toString(),
                        t.getParameters().getConnectionFee().toPlainString()))
                .toList();
    }
}
