package com.kotori316.testutil.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public final class HashCacheIgnore {
    public static void addMetaInfToIgnoreSet(Path rootDir, Set<Path> ignoreSet) {
        addChildrenToIgnoreSet(rootDir.resolve("META-INF"), ignoreSet);
    }

    private static void addChildrenToIgnoreSet(Path path, Set<Path> ignoreSet) {
        if (Files.exists(path)) {
            try (var stream = Files.walk(path)) {
                stream.forEach(ignoreSet::add);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
