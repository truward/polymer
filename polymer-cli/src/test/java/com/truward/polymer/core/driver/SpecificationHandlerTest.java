package com.truward.polymer.core.driver;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.app.PolymerModule;
import com.truward.polymer.domain.DomainObjectSpecifier;
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
    specificationHandler = new PolymerModule()
        .addDefaults()
        .getInjectionContext().getBean(SpecificationHandler.class);
  }

  @Test
  public void shouldGenerateCode() {
    specificationHandler.parseClass(TestSpec.class);
    specificationHandler.done();
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
