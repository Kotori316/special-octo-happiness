package com.kotori316.testutil.reporter;

import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.TestReporter;

import java.util.ArrayList;
import java.util.List;

public class CombinedReporter implements TestReporter {
    private final List<TestReporter> reporters;

    public CombinedReporter(List<TestReporter> reporters) {
        this.reporters = reporters;
    }

    public CombinedReporter(TestReporter... reporters) {
        this(List.of(reporters));
    }

    @Override
    public void onTestFailed(GameTestInfo gameTestInfo) {
        reporters.forEach(r -> r.onTestFailed(gameTestInfo));
    }

    @Override
    public void onTestSuccess(GameTestInfo gameTestInfo) {
        reporters.forEach(r -> r.onTestSuccess(gameTestInfo));

    }

    @Override
    public void finish() {
        TestReporter.super.finish();
        reporters.forEach(TestReporter::finish);
    }

    public CombinedReporter append(TestReporter reporter) {
        var copy = new ArrayList<>(reporters);
        copy.add(reporter);
        return new CombinedReporter(copy);
    }
}
