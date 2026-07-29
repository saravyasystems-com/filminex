package com.saravyasystems.filminex.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaDependency;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModularMonolithArchitectureTests {

    private static final String BASE = "com.saravyasystems.filminex";
    private static final Set<String> MODULES =
            Set.of(
                    "ai",
                    "assets",
                    "audit",
                    "capabilities",
                    "characters",
                    "collaboration",
                    "identity",
                    "knowledge",
                    "localization",
                    "locations",
                    "production",
                    "projects",
                    "rights",
                    "search",
                    "story",
                    "transparency",
                    "wardrobe");

    private static final Map<String, Set<String>> ALLOWED_DEPENDENCIES =
            Map.ofEntries(
                    Map.entry("ai", Set.of("assets", "audit", "capabilities")),
                    Map.entry("assets", Set.of("audit")),
                    Map.entry("audit", Set.of()),
                    Map.entry("capabilities", Set.of("identity")),
                    Map.entry("characters", Set.of("assets", "audit")),
                    Map.entry("collaboration", Set.of("audit", "identity", "projects")),
                    Map.entry("identity", Set.of("audit")),
                    Map.entry(
                            "knowledge",
                            Set.of("audit", "characters", "locations", "wardrobe")),
                    Map.entry(
                            "localization", Set.of("assets", "characters", "rights")),
                    Map.entry("locations", Set.of("assets", "audit")),
                    Map.entry(
                            "production",
                            Set.of(
                                    "assets",
                                    "audit",
                                    "characters",
                                    "localization",
                                    "locations",
                                    "rights",
                                    "story",
                                    "transparency",
                                    "wardrobe")),
                    Map.entry("projects", Set.of("identity")),
                    Map.entry("rights", Set.of("audit", "identity")),
                    Map.entry("search", Set.of("assets", "localization")),
                    Map.entry(
                            "story",
                            Set.of(
                                    "audit",
                                    "characters",
                                    "knowledge",
                                    "locations",
                                    "projects",
                                    "wardrobe")),
                    Map.entry("transparency", Set.of("audit", "rights")),
                    Map.entry("wardrobe", Set.of("assets", "audit")));

    private final JavaClasses productionClasses =
            new ClassFileImporter().importPackages(BASE);

    @Test
    void moduleInternalsAreNeverAccessedFromOutsideTheirModule() {
        for (String module : MODULES) {
            noClasses()
                    .that()
                    .resideOutsideOfPackage(modulePackage(module) + "..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage(modulePackage(module) + ".internal..")
                    .because(module + " implementation details are private to the module")
                    .check(productionClasses);
        }
    }

    @Test
    void internalImplementationTypesAreNotPublic() {
        classes()
                .that()
                .resideInAPackage(BASE + "..internal..")
                .should()
                .notBePublic()
                .check(productionClasses);
    }

    @Test
    void crossModuleDependenciesUseOnlyApprovedPublicApis() {
        for (JavaClass origin : productionClasses) {
            String sourceModule = moduleOf(origin);
            if (sourceModule == null) {
                continue;
            }

            for (JavaDependency dependency : origin.getDirectDependenciesFromSelf()) {
                String targetModule = moduleOf(dependency.getTargetClass());
                if (targetModule == null || sourceModule.equals(targetModule)) {
                    continue;
                }

                assertThat(ALLOWED_DEPENDENCIES.get(sourceModule))
                        .as("%s may depend on %s", sourceModule, targetModule)
                        .contains(targetModule);
                assertThat(dependency.getTargetClass().getPackageName())
                        .as(
                                "%s must use %s's public api: %s",
                                sourceModule,
                                targetModule,
                                dependency.getDescription())
                        .startsWith(modulePackage(targetModule) + ".api");
            }
        }
    }

    @Test
    void declaredGraphContainsEveryModuleExactlyOnce() {
        assertThat(ALLOWED_DEPENDENCIES.keySet()).containsExactlyInAnyOrderElementsOf(MODULES);
        assertThat(ALLOWED_DEPENDENCIES.values().stream().flatMap(Set::stream))
                .allMatch(MODULES::contains);
    }

    @Test
    void moduleDependenciesAreAcyclic() {
        slices()
                .matching(BASE + ".(*)..")
                .should()
                .beFreeOfCycles()
                .check(productionClasses);
    }

    private static String moduleOf(JavaClass type) {
        String prefix = BASE + ".";
        String packageName = type.getPackageName();
        if (!packageName.startsWith(prefix)) {
            return null;
        }

        String candidate = packageName.substring(prefix.length()).split("\\.")[0];
        return MODULES.contains(candidate) ? candidate : null;
    }

    private static String modulePackage(String module) {
        return BASE + "." + module;
    }
}
