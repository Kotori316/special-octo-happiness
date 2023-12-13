package com.kotori316.testutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class JUnitConnector {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String packageName;

    JUnitConnector(String packageName) {
        this.packageName = packageName;
    }

    void findTests() {
        var request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectPackage(this.packageName))
            .build();
        var launcher = LauncherFactory.create();
        var plan = launcher.discover(request);
        for (var child : getAll(plan)) {
            LOGGER.info("Test found: {}", child);
        }
    }

    static Set<TestIdentifier> getAll(TestPlan testPlan) {
        return testPlan.getRoots().stream().flatMap(i -> getAll(testPlan, i)).collect(Collectors.toSet());
    }

    static Stream<TestIdentifier> getAll(TestPlan testPlan, TestIdentifier identifier) {
        var children = testPlan.getChildren(identifier);
        if (children.isEmpty()) {
            // This identifier is container without children.
            return Stream.of(identifier);
        } else {
            return children
                .stream()
                .flatMap(i -> i.isTest() ? Stream.of(i) : getAll(testPlan, i));
        }
    }

    public static void main(String[] args) {
        new JUnitConnector(JUnitConnector.class.getPackageName()).findTests();
    }
}
