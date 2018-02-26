package org.mockito;

import net.avh4.test.junit.Nested;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
      assertThat(mock.toString(), is("mockable"));
    }

    @Test
    public void withName()
    {
      Mockable mock = mockery.mock(Mockable.class, "pippo");
      assertThat(mock.toString(), is("pippo"));
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
      assertThat(returnValue, is("dodici"));
    }

    @Test(expected = UnexpectedInvocationError.class)
    public void unstubbedExecution()
    {
      when.executes(t -> t.nonVoidMethod(12)).thenReturn("dodici");
      mock.nonVoidMethod(14);
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
      assertThat(mock.nonVoidMethod(12), is("dodici"));
    }
  }

  public class DoNothingTest
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

  public class GivenTest
  {
    private Mockable mock;
    private Mockery.Given<Mockable> given;

    @Before
    public void setUp()
    {
      given = mockery.given(mock = mockery.mock(Mockable.class));
    }

    @Test
    public void stubbedExecution()
    {
      given.executing(t -> t.nonVoidMethod(12)).thenReturn("dodici");
      assertThat(mock.nonVoidMethod(12), is("dodici"));
    }

    @Test(expected = UnexpectedInvocationError.class)
    public void unstubbedExecution()
    {
      given.executing(t -> t.nonVoidMethod(12)).thenReturn("dodici");
      mock.nonVoidMethod(14);
    }

    @Test
    public void stubbedRun()
    {
      given.running(t -> t.voidMethod(13)).doNothing();
      mock.voidMethod(13);
    }

    @Test(expected = UnexpectedInvocationError.class)
    public void unstubbedRun()
    {
      given.running(t -> t.voidMethod(13)).doNothing();
      mock.voidMethod(15);
    }
  }

  public class ApplyTest
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

    @Test(expected = org.mockito.exceptions.verification.WantedButNotInvoked.class)
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

    @Test(expected = org.mockito.exceptions.verification.NeverWantedButInvoked.class)
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

  public class ResetTest
  {
    @Test
    public void preservesNames()
    {
      Mockable mock1 = mockery.mock(Mockable.class, "pippo1");
      Mockable mock2 = mockery.mock(Mockable.class, "pippo2");

      mockery.reset();

      assertThat(mock1.toString(), is("pippo1"));
      assertThat(mock2.toString(), is("pippo2"));
    }
  }
}