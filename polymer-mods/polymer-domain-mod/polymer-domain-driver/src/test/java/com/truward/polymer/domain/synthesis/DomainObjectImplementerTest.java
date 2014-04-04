package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.driver.SpecificationUtil;
import com.truward.polymer.core.output.MemOutputStreamProvider;
import com.truward.polymer.domain.DefensiveCopyStyle;
import com.truward.polymer.domain.DomainImplementerSettings;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.driver.spi.DomainSpecificationDriver;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests {@link Implementer} in conjunction with the analysis-related classes.
 */
public final class DomainObjectImplementerTest {

  @SuppressWarnings("UnusedDeclaration")
  interface User {
    int getAge();
    String getName();
    Date getBirthDate();
  }

  @SuppressWarnings("UnusedDeclaration")
  interface Employee extends User {
    int getWage();
    List<String> getResponsibilities();
  }

  @SuppressWarnings("UnusedDeclaration")
  interface Primitive {
    byte getA();
    short getB();
    char getC();
    int getD();
    long getE();
    float getF();
    double getG();
  }

  private DomainAnalysisContext analysisContext;
  private MemOutputStreamProvider mosp;
  private Implementer implementer;
  private List<SpecificationStateAware> specificationStateAwareBeans;
  private DomainImplementerSettings settings;
  private DomainObjectSpecifier domainObjectSpecifier;

  @Before
  public void setup() {
    mosp = new MemOutputStreamProvider();
    final InjectionContext injectionContext = new DefaultInjectionContext();
    injectionContext.registerBean(mosp);
    new DomainSpecificationDriver().join(injectionContext);

    analysisContext = injectionContext.getBean(DomainAnalysisContext.class);
    implementer = injectionContext.getBean(Implementer.class);
    specificationStateAwareBeans = injectionContext.getBeans(SpecificationStateAware.class);
    settings = injectionContext.getBean(DomainImplementerSettings.class);
    domainObjectSpecifier = injectionContext.getBean(DomainObjectSpecifier.class);
  }

  @Test
  public void shouldAnalyzeClass() {
    final DomainAnalysisResult result = analysisContext.analyze(Employee.class);
    final List<DomainField> fields = ImmutableList.copyOf(result.getDeclaredFields());
    assertNotNull(fields);
  }

  @Test
  public void shouldImplement() {
    generateCode(User.class);
    final String code = getOneContent(mosp);
    assertTrue(code.contains("package")); // TODO: more complex verification
  }

  @Test
  public void shouldImplementEqualsAndHashCodeForPrimitiveType() {
    generateCode(Primitive.class);
    final String code = getOneContent(mosp);
    assertTrue(code.startsWith("package generated;\n"));
  }

  @Test
  public void shouldGenerateBuilder() {
    final String packageName = "com.mycompany.model";

    // trigger beginning
    SpecificationUtil.notifyState(specificationStateAwareBeans, SpecificationState.RECORDING);

    // specify
    final Employee employee = domainObjectSpecifier.domainObject(Employee.class);
    domainObjectSpecifier
        .target(employee)
        .isNonNegative(employee.getAge())
        .isNullable(domainObjectSpecifier.domainObject(User.class).getName())
        .setTargetName(employee, FqName.parse(packageName + ".DefaultEmployee"))
        .assignBuilder(employee);

    // trigger completion
    SpecificationUtil.notifyState(specificationStateAwareBeans, SpecificationState.COMPLETED);
    implementer.generateImplementations();

    final String code = getOneContent(mosp);
    System.out.println(code);
    assertTrue(code.startsWith("package " + packageName + ";\n"));
    assertFalse(code.contains("if (name == null) {")); // name is nullable
    assertTrue(code.contains("public final class DefaultEmployee"));
    assertTrue(code.contains("public Builder setAge(int"));
    assertTrue(code.contains("public Builder setName(String"));
    assertTrue(code.contains("public Builder setBirthDate(Date"));
  }

  @Test
  public void shouldGenerateCustomPackage() {
    final String packageName = "com.mysite.generated";
    settings.setTargetPackageName(FqName.parse(packageName));
    settings.setImplClassPrefix("Default");
    settings.setImplClassSuffix("Implementation");
    settings.setDefensiveCopyStyle(DefensiveCopyStyle.JDK);

    generateCode(Primitive.class);
    final String code = getOneContent(mosp);
    assertTrue(code.contains("package " + packageName));
    assertTrue(code.contains("class DefaultPrimitiveImplementation"));
  }

  //
  // Private
  //

  private void generateCode(Class<?> domainClass) {
    domainObjectSpecifier.targets(domainClass);
    SpecificationUtil.notifyState(specificationStateAwareBeans, SpecificationState.COMPLETED);
    implementer.generateImplementations();
  }

  private String getOneContent(MemOutputStreamProvider provider) {
    final Map<String, String> contentMap = provider.getContentMap();
    assertEquals(1, contentMap.size());
    return contentMap.values().iterator().next();
  }
}
