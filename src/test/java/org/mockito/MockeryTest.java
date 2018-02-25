package org.mockito;

import net.avh4.test.junit.Nested;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.exceptions.verification.NeverWantedButInvoked;
import org.mockito.exceptions.verification.WantedButNotInvoked;

import static org.junit.Assert.assertEquals;

@RunWith(Nested.class)
public class MockeryTest
{
  private Mockery mockery;

  @Before
  public void setUp()
  {
    mockery = new Mockery();
  }

  public class MockTest
  {
    @Test
    public void withoutName()
    {
      Mockable mock = mockery.mock(Mockable.class);
      assertEquals("mockable", mock.toString());
    }

    @Test
    public void withName()
    {
      Mockable mock = mockery.mock(Mockable.class, "pippo");
      assertEquals("pippo", mock.toString());
    }
  }

  public class WhenTest
  {
    private Mockable mock;
    private Mockery.When<Mockable> when;

    @Before
    public void setUp()
    {
      when = mockery.when(mock = mockery.mock(Mockable.class));
    }

    @Test
    public void stubbedExecution()
    {
      when.executes(t -> t.nonVoidMethod(12)).thenReturn("dodici");
      String returnValue = mock.nonVoidMethod(12);
      assertEquals("dodici", returnValue);
    }

    @Test(expected = UnexpectedInvocationError.class)
    public void unstubbedExecution()
    {
      when.executes(t -> t.nonVoidMethod(12)).thenReturn("dodici");
      mock.nonVoidMethod(15);
    }

    @Test
    public void stubbedRun()
    {
      when.runs(t -> t.voidMethod(13)).doNothing();
      mock.voidMethod(13);
    }

    @Test(expected = UnexpectedInvocationError.class)
    public void unstubbedRun()
    {
      when.runs(t -> t.voidMethod(13)).doNothing();
      mock.voidMethod(15);
    }
  }

  public class ReturnValueTest
  {
    private Mockable mock;

    @Before
    public void setUp()
    {
      mock = mockery.mock(Mockable.class);
    }

    @Test
    public void returnValue()
    {
      mockery.returnValue("dodici").when(mock).executes(t -> t.nonVoidMethod(12));
      assertEquals("dodici", mock.nonVoidMethod(12));
    }
  }

  public class DoNothing
  {
    private Mockable mock;

    @Before
    public void setUp()
    {
      mock = mockery.mock(Mockable.class);
    }

    @Test
    public void stubbedRun()
    {
      mockery.doNothing().when(mock).runs(t -> t.voidMethod(13));
      mock.voidMethod(13);
    }
  }

  public class Apply
  {
    @Test
    public void onlyStrictlyNeededStubbing() throws Throwable
    {
      Statement statement = new Statement()
      {
        public void evaluate() throws Throwable
        {
          Mockable mock = mockery.mock(Mockable.class);
          mockery.when(mock).executes(t -> t.nonVoidMethod(12)).thenReturn("dodici");
          mockery.when(mock).runs(t -> t.voidMethod(13)).doNothing();
          mockery.returnValue("quattordici").when(mock).executes(t -> t.nonVoidMethod(14));
          mockery.doNothing().when(mock).runs(t -> t.voidMethod(15));

          mockery.mock(Mockable.class); // no stubbing done on it, hence no verifications

          mock.nonVoidMethod(12);
          mock.voidMethod(13);
          mock.nonVoidMethod(14);
          mock.voidMethod(15);
        }
      };

      mockery.apply(statement, null).evaluate();
    }

    @Test(expected = WantedButNotInvoked.class)
    public void moreStubbingThanStrictlyNeeded() throws Throwable
    {
      Statement statement = new Statement()
      {
        public void evaluate() throws Throwable
        {
          Mockable mock = mockery.mock(Mockable.class);
          mockery.when(mock).executes(t -> t.nonVoidMethod(12)).thenReturn("dodici");
          mockery.when(mock).runs(t -> t.voidMethod(13)).doNothing();

          mock.nonVoidMethod(12);
        }
      };

      mockery.apply(statement, null).evaluate();
    }

    @Test(expected = NeverWantedButInvoked.class)
    public void lessStubbingThanStrictlyNeeded() throws Throwable
    {
      Statement statement = new Statement()
      {
        public void evaluate() throws Throwable
        {
          Mockable mock = mockery.mock(Mockable.class);
          mockery.when(mock).executes(t -> t.nonVoidMethod(12)).thenReturn("dodici");

          mock.nonVoidMethod(12);
          mock.voidMethod(13);
        }
      };

      mockery.apply(statement, null).evaluate();
    }

    @Test(expected = CustomThrowable.class)
    public void customThrowable() throws Throwable
    {
      Statement statement = new Statement()
      {
        public void evaluate() throws Throwable
        {
          throw new CustomThrowable();
        }
      };

      mockery.apply(statement, null).evaluate();
    }

    private final class CustomThrowable extends Throwable
    {
    }
  }

  public class Reset
  {
    @Test
    public void preservesNames()
    {
      Mockable mock1 = mockery.mock(Mockable.class, "pippo1");
      Mockable mock2 = mockery.mock(Mockable.class, "pippo2");

      mockery.reset();

      assertEquals("pippo1", mock1.toString());
      assertEquals("pippo2", mock2.toString());
    }
  }
}