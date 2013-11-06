package com.truward.polymer.domain.synthesis;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.core.generator.JavaCodeGenerator;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainAnalysisResult;
import com.truward.polymer.domain.analysis.DomainField;
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

  @Test
  public void shouldAnalyzeClass() {
    final DomainAnalysisContext analyzer = new DomainAnalysisContext();
    final DomainAnalysisResult result = analyzer.analyze(Employee.class);
    final List<DomainField> fields = ImmutableList.copyOf(result.getDeclaredFields());
    assertNotNull(fields);
  }

  @Test
  public void shouldImplement() {
    final DomainAnalysisContext analysisContext = new DomainAnalysisContext();
    final DomainAnalysisResult result = analysisContext.analyze(User.class);
    final JavaCodeGenerator generator = new JavaCodeGenerator();
    final DomainObjectImplementer implementer = new DomainObjectImplementer(generator, result);
    implementer.generateCompilationUnit();
    generator.printContents();
  }

  @Test
  public void shouldImplementEqualsAndHashCodeForPrimitiveType() {
    final DomainAnalysisContext analysisContext = new DomainAnalysisContext();
    final DomainAnalysisResult result = analysisContext.analyze(Primitive.class);
    final JavaCodeGenerator generator = new JavaCodeGenerator();
    final DomainObjectImplementer implementer = new DomainObjectImplementer(generator, result);
    implementer.generateCompilationUnit();
    generator.printContents();
  }
}
