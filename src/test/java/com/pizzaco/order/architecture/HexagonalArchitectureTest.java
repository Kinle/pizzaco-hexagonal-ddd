package com.pizzaco.order.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ArchUnit tests enforcing the Hexagonal Architecture "Golden Rule":
 *
 * <p>"The Inside never depends on the Outside."
 *
 * <p>- Domain must NOT depend on Application or Infrastructure - Application must NOT depend on
 * Infrastructure - Infrastructure may depend on anything (it's the outermost layer)
 */
class HexagonalArchitectureTest {

  private static final String BASE_PACKAGE = "com.pizzaco.order";
  private static JavaClasses classes;

  @BeforeAll
  static void setup() {
    classes =
        new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE_PACKAGE);
  }

  @Test
  @DisplayName("Domain layer must not depend on Application layer")
  void domainShouldNotDependOnApplication() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..application..")
        .because("The Domain layer is the innermost layer and must be self-contained")
        .check(classes);
  }

  @Test
  @DisplayName("Domain layer must not depend on Infrastructure layer")
  void domainShouldNotDependOnInfrastructure() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .because("The Domain layer must never know about frameworks, databases, or REST")
        .check(classes);
  }

  @Test
  @DisplayName("Application layer must not depend on Infrastructure layer")
  void applicationShouldNotDependOnInfrastructure() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..infrastructure..")
        .because("The Application layer coordinates via Ports, never via Adapters directly")
        .check(classes);
  }

  @Test
  @DisplayName("Domain layer must not use Spring framework")
  void domainShouldNotUseSpring() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("org.springframework..")
        .because("The Domain layer must be pure Java — no framework dependencies")
        .check(classes);
  }

  @Test
  @DisplayName("Domain layer must not use Jakarta persistence/JPA annotations")
  void domainShouldNotUseJakartaPersistence() {
    noClasses()
        .that()
        .resideInAPackage("..domain..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("jakarta.persistence..")
        .because(
            "The Domain layer must not depend on JPA — "
                + "jakarta.validation is allowed for constraint annotations")
        .check(classes);
  }

  @Test
  @DisplayName("Application layer must not use Spring framework")
  void applicationShouldNotUseSpring() {
    noClasses()
        .that()
        .resideInAPackage("..application..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("org.springframework..")
        .because("The Application layer should remain framework-agnostic")
        .check(classes);
  }
}
