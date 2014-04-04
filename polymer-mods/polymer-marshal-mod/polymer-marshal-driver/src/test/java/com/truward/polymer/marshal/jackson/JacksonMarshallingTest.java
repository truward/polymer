package com.truward.polymer.marshal.jackson;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.*;
import com.truward.polymer.core.output.MemOutputStreamProvider;
import com.truward.polymer.core.support.driver.DefaultSpecificationHandler;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.driver.spi.DomainSpecificationDriver;
import com.truward.polymer.marshal.jackson.spi.JacksonMarshallingDriver;
import com.truward.polymer.marshal.jackson.support.DefaultJacksonMarshallingSpecifier;
import com.truward.polymer.marshal.json.JacksonMarshallingSpecifier;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Alexander Shabanov
 */
public final class JacksonMarshallingTest {

  private MemOutputStreamProvider mosp;
  private SpecificationHandler specificationHandler;
  private List<SpecificationStateAware> specificationStateAwareBeans;
  private Implementer jsonMarshallerImplementer;

  @Before
  public void setup() {
    final InjectionContext injectionContext = new DefaultInjectionContext();

    mosp = new MemOutputStreamProvider();

    // common contexts
    injectionContext.registerBean(mosp);
    injectionContext.registerBean(DefaultSpecificationHandler.class);

    new DomainSpecificationDriver().join(injectionContext);
    new JacksonMarshallingDriver().join(injectionContext);

    specificationHandler = injectionContext.getBean(SpecificationHandler.class);
    specificationStateAwareBeans = injectionContext.getBeans(SpecificationStateAware.class);
    jsonMarshallerImplementer = injectionContext.getBean(DefaultJacksonMarshallingSpecifier.class);
  }


  @Test
  public void shouldGenerateTarget() {
    specificationHandler.parseClass(FooSpecification.class);
    SpecificationUtil.notifyState(specificationStateAwareBeans, SpecificationState.COMPLETED);

    jsonMarshallerImplementer.generateImplementations();

    final String code = getOneContent();
    System.out.println(code);
    assertTrue(code.contains("package generated;"));
  }


  //
  // Test data
  //

  @SuppressWarnings("UnusedDeclaration")
  public interface Foo {
    Long getId();
    int getAge();
    String getName();
    List<String> getTags();
  }

  public static final class FooSpecification {
    @Resource
    private DomainObjectSpecifier domainObjectSpecifier;

    @Resource
    private JacksonMarshallingSpecifier jsonMarshallingSpecifier;

    @Specification(ordinal = 1)
    public void specifyDomainObject() {
      domainObjectSpecifier.targets(Foo.class);
    }

    @Specification
    public void specifyFooFieldTraits(@DomainObject Foo foo) {
      domainObjectSpecifier.isNullable(foo.getId());
    }

    @Specification(ordinal = 2)
    public void specifyJsonSerialization() {
      jsonMarshallingSpecifier
          .setGeneratorTarget(FqName.parse("generated.JacksonMarshallers"))
          .addDomainEntity(Foo.class);
    }
  }

  //
  // Private
  //

  private String getOneContent() {
    assertEquals(1, mosp.getContentMap().size());
    return mosp.getContentMap().values().iterator().next();
  }
}
