package com.truward.polymer.core.driver;

import com.truward.polymer.annotation.Specification;
import com.truward.polymer.core.PolymerModule;
import com.truward.polymer.core.output.OutputStreamProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Alexander Shabanov
 */
public class SpecificationHandlerTest {
  private SpecificationHandler specificationHandler;


  @Before
  public void init() {
    final PolymerModule module = new PolymerModule().addDefaults();

    module.getInjectionContext().registerBean(Mockito.mock(OutputStreamProvider.class, new Answer() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        throw new AssertionError("Should not be called");
      }
    }));

    specificationHandler = module.getInjectionContext().getBean(SpecificationHandler.class);
  }

  @Test
  public void shouldProcessNullSpec() {
    assertNotNull(specificationHandler.parseClass(TestSpec.class));
    specificationHandler.done();
  }

  @Test
  public void shouldSortSpecByOrdinals() {
    // spec 1
    OrdinalSumProvider sumProvider = (OrdinalSumProvider) specificationHandler.parseClass(OrderedSpec1.class);
    assertNotNull(sumProvider);
    assertEquals("Accumulated ordinal sum should be 321", 321, sumProvider.getOrdinalOrderSum());

    // spec 2
    sumProvider = (OrdinalSumProvider) specificationHandler.parseClass(OrderedSpec2.class);
    assertNotNull(sumProvider);
    assertEquals("Accumulated ordinal sum should be 321", 321, sumProvider.getOrdinalOrderSum());
  }

  //
  // Test data, should be public
  //

  public static final class TestSpec {

    @Specification
    public void nullSpec() {
    }
  }

  // ordinals test

  public static abstract class OrdinalSumProvider {
    private int multiplier = 1;
    private int sum = 0;

    public final int getOrdinalOrderSum() {
      return sum;
    }

    protected void inc(int pos) {
      sum += multiplier * pos;
      multiplier *= 10;
    }
  }

  public static final class OrderedSpec1 extends OrdinalSumProvider {
    @Specification(ordinal = 1)
    public void spec1() {
      inc(1);
    }

    @Specification(ordinal = 2)
    public void spec2() {
      inc(2);
    }

    @Specification
    public void lastSpec() {
      inc(3);
    }
  }

  // changed order of appearance
  public static final class OrderedSpec2 extends OrdinalSumProvider {
    @Specification(ordinal = 2)
    public void spec2() {
      inc(2);
    }

    @Specification
    public void lastSpec() {
      inc(3);
    }

    @Specification(ordinal = 1)
    public void spec1() {
      inc(1);
    }
  }
}
