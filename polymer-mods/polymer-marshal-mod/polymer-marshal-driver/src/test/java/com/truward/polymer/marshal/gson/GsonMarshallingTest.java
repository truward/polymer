package com.truward.polymer.marshal.gson;

import com.truward.di.InjectionContext;
import com.truward.di.support.DefaultInjectionContext;
import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.driver.Implementer;
import com.truward.polymer.core.driver.SpecificationStateAware;
import com.truward.polymer.core.output.MemOutputStreamProvider;
import com.truward.polymer.domain.DomainImplementerSettings;
import com.truward.polymer.domain.DomainObjectSpecifier;
import com.truward.polymer.domain.analysis.DomainAnalysisContext;
import com.truward.polymer.domain.analysis.DomainImplementationTargetSink;
import com.truward.polymer.domain.analysis.support.DefaultDomainAnalysisContext;
import com.truward.polymer.domain.driver.support.DomainImplementerSettingsProvider;
import com.truward.polymer.domain.implementer.DomainObjectImplementer;
import com.truward.polymer.marshal.gson.implementer.GsonMarshallerImplementer;
import com.truward.polymer.marshal.gson.specification.support.DefaultGsonMarshallingSpecifier;
import com.truward.polymer.naming.FqName;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author Alexander Shabanov
 */
public class GsonMarshallingTest {

  private DomainAnalysisContext analysisContext;
  private MemOutputStreamProvider mosp;
  private Implementer implementer;
  private DomainImplementationTargetSink targetSink;
  private SpecificationStateAware specificationStateAware;
  private DomainImplementerSettings settings;

  @Before
  public void setup() {
    final InjectionContext injectionContext = new DefaultInjectionContext();

    mosp = new MemOutputStreamProvider();
    injectionContext.registerBean(DefaultDomainAnalysisContext.class);
    injectionContext.registerBean(mosp);
    injectionContext.registerBean(new DomainImplementerSettingsProvider());
    injectionContext.registerBean(DomainObjectImplementer.class);
    injectionContext.registerBean(DefaultGsonMarshallingSpecifier.class);
    //injectionContext.registerBean(GsonMarshallerImplementer.class);

    analysisContext = injectionContext.getBean(DomainAnalysisContext.class);
    implementer = injectionContext.getBean(Implementer.class);
    targetSink = injectionContext.getBean(DomainImplementationTargetSink.class);
    specificationStateAware = injectionContext.getBean(SpecificationStateAware.class);
    settings = injectionContext.getBean(DomainImplementerSettings.class);
  }


  @Test
  public void shouldGenerateTarget() {

  }


  //
  // Test data
  //

  public interface Foo {
    Long getId();
    int getAge();
    String getName();
  }

  public static final class FooSpecification {
    @Resource
    private DomainObjectSpecifier domainObjectSpecifier;

    @Resource
    private GsonMarshallingSpecifier gsonMarshallingSpecifier;

    @Specification(ordinal = 1)
    public void specifyDomainObject() {
      domainObjectSpecifier.target(Foo.class);
    }

    @Specification(ordinal = 2)
    public void specifyGsonSerialization() {
      gsonMarshallingSpecifier
          .setGeneratorTarget(FqName.parse("generated.GsonSerializer"))
          .addDomainEntity(Foo.class);
    }
  }
}
