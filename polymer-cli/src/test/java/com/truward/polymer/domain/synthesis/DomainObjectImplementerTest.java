package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.code.naming.FqName;
import com.truward.polymer.domain.analysis.DomainImplementationTarget;
import com.truward.polymer.domain.analysis.support.DefaultDomainImplementationTarget;
import com.truward.polymer.domain.driver.DomainImplementerSettingsProvider;
import com.truward.polymer.domain.synthesis.support.DefaultDomainObjectImplementer;
import com.truward.polymer.testutil.MemOutputStreamProvider;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Generated;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Sample spring-driven test.
 */
public class DomainObjectImplementerTest {

  @Generated("test")
  interface User {
    int getAge();
    String getName();
    Date getBirthDate();
  }

  @Generated("test")
  interface Employee extends User {
    int getWage();
    List<String> getResponsibilities();
  }

  @Generated("test")
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
  private DomainObjectImplementer implementer;

  @Before
  public void setup() {
    mosp = new MemOutputStreamProvider();
    injectionContext.registerBean(DefaultDomainAnalysisContext.class);
    injectionContext.registerBean(mosp);
    injectionContext.registerBean(new DomainImplementerSettingsProvider());
    injectionContext.registerBean(DefaultDomainObjectImplementer.class);

    analysisContext = injectionContext.getBean(DomainAnalysisContext.class);
    implementer = injectionContext.getBean(DomainObjectImplementer.class);
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
    final String code = mosp.getOneContent();
    assertTrue(code.contains("package")); // TODO: more complex verification
  }

  @Test
  public void shouldImplementEqualsAndHashCodeForPrimitiveType() {
    final DomainAnalysisResult result = analysisContext.analyze(Primitive.class);
    generateCode(result);
    final String code = mosp.getOneContent();
    assertTrue(code.contains("package")); // TODO: more complex verification
  }

  //
  // Private
  //

  private void generateCode(DomainAnalysisResult result) {
    final DomainImplementationTarget target = new DefaultDomainImplementationTarget(result);
    target.setTargetName(FqName.parse("generated.Sample"));

    implementer.generateCode(ImmutableList.of(target));
  }
}
