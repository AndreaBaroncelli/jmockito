package org.mockito;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class JMockitoTest
{
  public static class MockTest
  {
    @Test
    public void withoutName()
    {
      Mockable mock = JMockito.mock(Mockable.class);
      assertThat(mock.toString(), is("mockable"));
    }

    @Test
    public void withName()
    {
      Mockable mock = JMockito.mock(Mockable.class, "pippo");
      assertThat(mock.toString(), is("pippo"));
    }
  }

  public static class WhenTest
  {
    private Mockable mock;
    private JMockito.When<Mockable> when;

    @Before
    public void setUp()
    {
      when = JMockito.when(mock = JMockito.mock(Mockable.class));
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

  public static class ReturnValueTest
  {
    private Mockable mock;

    @Before
    public void setUp()
    {
      mock = JMockito.mock(Mockable.class);
    }

    @Test
    public void returnValue()
    {
      JMockito.returnValue("dodici").when(mock).executes(t -> t.nonVoidMethod(12));
      assertThat(mock.nonVoidMethod(12), is("dodici"));
    }
  }

  public static class GivenTest
  {
    private Mockable mock;
    private JMockito.Given<Mockable> given;

    @Before
    public void setUp()
    {
      given = JMockito.given(mock = JMockito.mock(Mockable.class));
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

  public static class ResetTest
  {
    @Test
    public void preservesName()
    {
      Mockable mock = JMockito.mock(Mockable.class, "pippo");
      JMockito.reset(mock);
      assertThat(mock.toString(), is("pippo"));
    }
  }
}