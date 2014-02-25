package com.truward.polymer.core.generator2;

import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Alexander Shabanov
 */
@Ignore
public class GeneratorTest {
  private CodeGenerator target;

  @Before
  public void init() {
    target = mock(CodeGenerator.class);
  }

  @Test
  public void shouldGenerateCode() {
    target.s("package").sp().s(FqName.parse("com.company.product")).c('\n');
  }
}
