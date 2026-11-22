package ua.lab2.tariffs;

import java.nio.file.Path;
import java.util.function.IntConsumer;
import ua.lab2.tariffs.xml.XmlProcessingException;

/** Command-line entry point: {@code Main [input.xml [output.xml]]}. */
public final class Main {

    static final String DEFAULT_INPUT = "src/main/resources/tariffs.xml";
    static final String DEFAULT_OUTPUT = "target/output/tariffs-by-operator.xml";

    /** Replaceable in tests so that a failing run does not terminate the JVM. */
    static IntConsumer exitHandler = System::exit;

    private Main() {
    }

    public static void main(String[] args) throws XmlProcessingException {
        int exitCode = execute(args);
        if (exitCode != 0) {
            exitHandler.accept(exitCode);
        }
    }

    static int execute(String[] args) throws XmlProcessingException {
        Path input = Path.of(args.length > 0 ? args[0] : DEFAULT_INPUT);
        Path output = Path.of(args.length > 1 ? args[1] : DEFAULT_OUTPUT);
        return TariffApp.createDefault().run(input, output);
    }
}
