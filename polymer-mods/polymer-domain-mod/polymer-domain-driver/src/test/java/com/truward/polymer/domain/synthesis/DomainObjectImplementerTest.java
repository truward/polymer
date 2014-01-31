package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.output.MemOutputStreamProvider;
import com.truward.polymer.domain.DefensiveCopyStyle;
import com.truward.polymer.domain.DomainImplementerSettings;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import com.truward.polymer.domain.driver.DomainImplementerSettingsProvider;
import com.truward.polymer.domain.driver.support.DomainObjectImplementer;
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
public class DomainObjectImplementerTest {

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
  private InjectionContext injectionContext = new DefaultInjectionContext();
  private Implementer implementer;
  private DomainImplementationTargetSink targetSink;
  private SpecificationStateAware specificationStateAware;
  private DomainImplementerSettings settings;

  @Before
  public void setup() {
    mosp = new MemOutputStreamProvider();
    injectionContext.registerBean(DefaultDomainAnalysisContext.class);
    injectionContext.registerBean(mosp);
    injectionContext.registerBean(new DomainImplementerSettingsProvider());
    injectionContext.registerBean(DomainObjectImplementer.class);

    analysisContext = injectionContext.getBean(DomainAnalysisContext.class);
    implementer = injectionContext.getBean(Implementer.class);
    targetSink = injectionContext.getBean(DomainImplementationTargetSink.class);
    specificationStateAware = injectionContext.getBean(SpecificationStateAware.class);
    settings = injectionContext.getBean(DomainImplementerSettings.class);
  }

  @Test
  public void shouldAnalyzeClass() {
    final DomainAnalysisResult result = analysisContext.analyze(Employee.class);
    final List<DomainField> fields = ImmutableList.copyOf(result.getDeclaredFields());
    assertNotNull(fields);
  }

  @Test
  public void shouldImplement() {
    final DomainAnalysisResult result = analysisContext.analyze(User.class);
    generateCode(result);
    final String code = getOneContent(mosp);
    assertTrue(code.contains("package")); // TODO: more complex verification
  }

  @Test
  public void shouldImplementEqualsAndHashCodeForPrimitiveType() {
    final DomainAnalysisResult result = analysisContext.analyze(Primitive.class);
    generateCode(result);
    final String code = getOneContent(mosp);
    assertTrue(code.contains("package")); // TODO: more complex verification
  }

  @Test
  public void shouldGenerateCustomPackage() {
    final String packageName = "com.mysite.generated";
    settings.setDefaultTargetPackageName(FqName.parse(packageName));
    settings.setDefaultImplClassPrefix("Default");
    settings.setDefaultImplClassSuffix("Implementation");
    settings.setDefensiveCopyStyle(DefensiveCopyStyle.JDK);

    final DomainAnalysisResult result = analysisContext.analyze(Primitive.class);
    generateCode(result);
    final String code = getOneContent(mosp);
    assertTrue(code.contains("package " + packageName));
    assertTrue(code.contains("class DefaultPrimitiveImplementation"));
  }

  //
  // Private
  //

  private void generateCode(DomainAnalysisResult result) {
    targetSink.submit(result);
    specificationStateAware.setState(SpecificationState.COMPLETED);
    implementer.generateImplementations();
  }

  private String getOneContent(MemOutputStreamProvider provider) {
    final Map<String, String> contentMap = provider.getContentMap();
    assertEquals(1, contentMap.size());
    return contentMap.values().iterator().next();
  }
}
