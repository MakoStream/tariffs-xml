package ua.lab2.tariffs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    private final IntConsumer originalExit = Main.exitHandler;

    @AfterEach
    void restoreExitHandler() {
        Main.exitHandler = originalExit;
    }

    @Test
    void executeUsesGivenPaths(@TempDir Path dir) throws Exception {
        Path output = dir.resolve("out.xml");

        assertEquals(0, Main.execute(new String[] {TestData.sampleXml().toString(), output.toString()}));
        assertTrue(output.toFile().exists());
    }

    @Test
    void executeFallsBackToDefaultPaths() throws Exception {
        assertEquals(0, Main.execute(new String[0]));
    }

    @Test
    void mainDoesNotExitOnSuccess(@TempDir Path dir) throws Exception {
        List<Integer> exits = new ArrayList<>();
        Main.exitHandler = exits::add;

        Main.main(new String[] {TestData.sampleXml().toString(), dir.resolve("out.xml").toString()});

        assertTrue(exits.isEmpty());
    }

    @Test
    void mainExitsWithFailureCode(@TempDir Path dir) throws Exception {
        List<Integer> exits = new ArrayList<>();
        Main.exitHandler = exits::add;

        Main.main(new String[] {dir.resolve("missing.xml").toString(), dir.resolve("out.xml").toString()});

        assertEquals(List.of(1), exits);
    }
}
