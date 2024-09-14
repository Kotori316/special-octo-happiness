package com.kotori316.testutil.common.reporter;

import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.TestReporter;

import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.stream.Stream;

public class ReporterRegister {
    static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .toFormatter();

    public static void changeReporter() {
        var time = ZonedDateTime.now().format(FORMATTER);
        GlobalTestReporter.replaceWith(
            new CombinedReporter(current(), new CreateFileReporter(Path.of("crash-reports/" + time)))
        );
    }

    private static TestReporter current() {
        try {
            var field = Stream.of(GlobalTestReporter.class.getDeclaredFields())
                .filter(f -> f.getType() == TestReporter.class)
                .filter(f -> (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No TestReporter field found in GlobalTestReporter"));
            field.setAccessible(true);
            return (TestReporter) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
