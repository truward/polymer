package com.truward.polymer.core.driver;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.driver.support.DefaultDomainObjectSpecifier;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public class SpecificationHandlerTest {
  private SpecificationHandler specificationHandler;

  @Before
  public void init() {
    specificationHandler = new SpecificationHandler(ImmutableList.<SpecificationDriver>of(
        new DefaultDomainObjectSpecifier()));
  }

  @Test
  public void shouldGenerateCode() {
    assertTrue(specificationHandler.parseClass(TestSpec.class));
  }


  //
  // Test data, should be public
  //

  public interface User {
    String getName();
    int getAge();
  }

  public static final class TestSpec {
    @Resource
    private DomainObjectSpecifier specifier;

    @Specification
    public void user() {
      final User user = specifier.domainObject(User.class);
      specifier
          .hasLength(user.getName())
          .isNonNegative(user.getAge());
    }
  }
}
