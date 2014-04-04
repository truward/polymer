package com.truward.polymer.marshal.gson;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.*;
import com.truward.polymer.core.output.MemOutputStreamProvider;
import com.truward.polymer.core.support.driver.DefaultSpecificationHandler;
import com.truward.polymer.domain.DomainObject;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.driver.spi.DomainSpecificationDriver;
import com.truward.polymer.marshal.gson.spi.GsonMarshallingDriver;
import com.truward.polymer.marshal.gson.support.DefaultGsonMarshallingSpecifier;
import com.truward.polymer.marshal.json.GsonMarshallingSpecifier;
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
public final class GsonMarshallingTest {

  private MemOutputStreamProvider mosp;
  private Implementer jsonMarshallerImplementer;
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
    new GsonMarshallingDriver().join(injectionContext);

    specificationHandler = injectionContext.getBean(SpecificationHandler.class);
    specificationStateAwareBeans = injectionContext.getBeans(SpecificationStateAware.class);
    jsonMarshallerImplementer = injectionContext.getBean(DefaultGsonMarshallingSpecifier.class);
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
    private GsonMarshallingSpecifier jsonMarshallingSpecifier;

    @Specification(ordinal = 1)
    public void specifyDomainObject() {
      domainObjectSpecifier.targets(Foo.class);
    }

    @Specification
    public void specifyFooFieldTraits(@DomainObject Foo foo) {
      domainObjectSpecifier.isNullable(foo.getId());
    }

    @Specification(ordinal = 2)
    public void specifyGsonSerialization() {
      jsonMarshallingSpecifier
          .setGeneratorTarget(FqName.parse("generated.GsonMarshallers"))
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
