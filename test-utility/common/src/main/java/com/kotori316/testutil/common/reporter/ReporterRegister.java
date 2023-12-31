package com.kotori316.testutil.common.reporter;

import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.LogTestReporter;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class ReporterRegister {
    static final DateTimeFormatter FORMATTER =  new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .toFormatter();

    public static void changeReporter() {
        var time = ZonedDateTime.now().format(FORMATTER);
        GlobalTestReporter.replaceWith(
            new CombinedReporter(new LogTestReporter(), new CreateFileReporter(Path.of("crash-reports/" + time)))
        );
    }
}
