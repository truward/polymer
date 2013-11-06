package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Sample spring-driven test.
 */
public class DomainObjectImplementerTest {

  interface User {
    int getAge();
    String getName();
    Date getBirthDate();
  }

  interface Employee extends User {
    int getWage();
    List<String> getResponsibilities();
  }

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
  private JavaCodeGenerator generator;

  @Before
  public void setup() {
    analysisContext = new DefaultDomainAnalysisContext();
    generator = new JavaCodeGenerator();
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
    final DomainObjectImplementer implementer = new DomainObjectImplementer(generator, result);
    implementer.generateCompilationUnit();
    generator.printContents();
  }

  @Test
  public void shouldImplementEqualsAndHashCodeForPrimitiveType() {
    final DomainAnalysisResult result = analysisContext.analyze(Primitive.class);
    final DomainObjectImplementer implementer = new DomainObjectImplementer(generator, result);
    implementer.generateCompilationUnit();
    generator.printContents();
  }
}
