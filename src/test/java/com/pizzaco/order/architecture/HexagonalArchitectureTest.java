package com.pizzaco.order.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class HexagonalArchitectureTest {

    @Test
    void coreShouldNotDependOnInfrastructureOrSpring() {
        var importedClasses = new ClassFileImporter().importPackages("com.pizzaco.order");

        noClasses()
                .that()
                .resideInAPackage("..core..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "org.springframework..")
                .check(importedClasses);
    }
}
