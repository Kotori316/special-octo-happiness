package com.kotori316.testutil.common.reporter;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;
import org.apache.logging.log4j.core.util.Throwables;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class CreateFileReporter implements TestReporter {
    private static final Pattern INVALID_LETTERS = Pattern.compile("[\\\\/:*?\"<>|]");
    private final Path parentDir;

    public CreateFileReporter(Path parentDir) {
        this.parentDir = parentDir;
    }

    @Override
    public void onTestFailed(GameTestInfo pTestInfo) {
        try {
            if (Files.notExists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            var matcher = INVALID_LETTERS.matcher(pTestInfo.getTestName());
            var file = parentDir.resolve(matcher.replaceAll("-") + ".txt");
            var error = Optional.ofNullable(pTestInfo.getError())
                .map(Throwables::toStringList)
                .orElse(List.of());
            Files.write(file, List.of(
                "Test failed.",
                pTestInfo.getTestName(),
                "Runs %d ms".formatted(pTestInfo.getRunTime()),
                String.join("\n", error)
            ));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void onTestSuccess(GameTestInfo pTestInfo) {

    }

    @Override
    public String toString() {
        return "CreateFileReporter[" +
            "parentDir=" + parentDir + ']';
    }
}
