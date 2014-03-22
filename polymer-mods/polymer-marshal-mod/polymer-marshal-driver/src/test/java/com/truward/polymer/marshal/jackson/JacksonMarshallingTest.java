package com.truward.polymer.marshal.jackson;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.SpecificationHandler;
import com.truward.polymer.core.driver.SpecificationState;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.driver.SpecificationUtil;
import com.truward.polymer.core.output.MemOutputStreamProvider;
import com.truward.polymer.core.support.driver.DefaultSpecificationHandler;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.driver.support.DomainSpecificationDriver;
import com.truward.polymer.marshal.gson.support.GsonMarshallingDriver;
import com.truward.polymer.marshal.jackson.support.JacksonMarshallingDriver;
import com.truward.polymer.marshal.json.JsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.analysis.JsonMarshallerImplementer;
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
  private JsonMarshallerImplementer jsonMarshallerImplementer;
  private SpecificationHandler specificationHandler;
  private List<SpecificationStateAware> specificationStateAwareBeans;

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
    jsonMarshallerImplementer = injectionContext.getBean(JsonMarshallerImplementer.class);
  }


  @Test
  public void shouldGenerateTarget() {
    specificationHandler.parseClass(FooSpecification.class);
    SpecificationUtil.notifyState(specificationStateAwareBeans, SpecificationState.COMPLETED);

    jsonMarshallerImplementer.generateImplementations();

    final String code = getOneContent();
    System.out.println(code);
    assertTrue(code.contains("package"));
  }


  //
  // Test data
  //

  @SuppressWarnings("UnusedDeclaration")
  public interface Foo {
    Long getId();
    int getAge();
    String getName();
  }

  public static final class FooSpecification {
    @Resource
    private DomainObjectSpecifier domainObjectSpecifier;

    @Resource
    private JsonMarshallingSpecifier jsonMarshallingSpecifier;

    @Specification(ordinal = 1)
    public void specifyDomainObject() {
      domainObjectSpecifier.target(Foo.class);
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
