package de.wps.ddd.cinema.architecture;

import de.wps.ddd.cinema.Application;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies the Spring Modulith module structure: modules may depend on each other only via exposed types
 * (public module API, named interfaces or events) and must be free of cycles.
 */
class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void satisfiesModulithRules() {
        modules.verify();
    }
}
