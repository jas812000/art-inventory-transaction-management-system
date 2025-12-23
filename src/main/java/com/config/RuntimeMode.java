package com.config;

/**
 * Defines the runtime context in which the application is executing.
 * <p>
 * The runtime mode controls environment-sensitive behavior such as:
 * <ul>
 *   <li>Filesystem paths</li>
 *   <li>Sample data initialization</li>
 *   <li>Test data isolation</li>
 * </ul>
 *
 * {@link #APPLICATION} is the default mode and is used for normal execution.
 * {@link #TEST} is enabled by setting the JVM system property:
 *
 * <pre>
 *   -Druntime.mode=test
 * </pre>
 */
public enum RuntimeMode {

    /**
     * Normal application runtime.
     * Uses user-scoped data directories (or environment overrides).
     */
    APPLICATION,

    /**
     * Test runtime.
     * Uses test-scoped data directories and avoids mutating real user data.
     */
    TEST
}
