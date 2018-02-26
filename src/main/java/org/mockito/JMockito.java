package org.mockito;

import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.stubbing.Answer;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public final class JMockito
{
  private static final Answer noUnexpectedInvocationsAnswer = invocationOnMock ->
  {
    throw new UnexpectedInvocationError(invocationOnMock);
  };

  public static <MOCK> MOCK mock(Class<MOCK> mockClass)
  {
    MOCK mock = Mockito.mock(mockClass, new MockSettingsImpl().defaultAnswer(noUnexpectedInvocationsAnswer));
    String className = mockClass.getSimpleName();
    returnValue(className.substring(0, 1).toLowerCase() + className.substring(1)).when(mock).executes(Object::toString);
    return mock;
  }

  public static <MOCK> MOCK mock(Class<MOCK> mockClass, String name)
  {
    MOCK mock = Mockito.mock(mockClass, new MockSettingsImpl().name(name).defaultAnswer(noUnexpectedInvocationsAnswer));
    returnValue(name).when(mock).executes(Object::toString);
    return mock;
  }

  public static <MOCK> When<MOCK> when(MOCK mock)
  {
    return new When<>(mock);
  }

  public static <VALUE> ReturnValue<VALUE> returnValue(VALUE value)
  {
    return new ReturnValue<>(value);
  }

  public static <MOCK> Given<MOCK> given(MOCK mock)
  {
    return new Given<>(mock);
  }

  public static void reset(Object mock)
  {
    String string = mock.toString();
    Mockito.reset(mock);
    returnValue(string).when(mock).executes(Object::toString);
  }

  public static final class When<MOCK>
  {
    private final MOCK mock;

    private When(MOCK mock)
    {
      this.mock = mock;
    }

    public <VALUE> Executes<VALUE> executes(Function<? super MOCK, ? extends VALUE> function)
    {
      return new Executes<>(function);
    }

    public Runs runs(Consumer<? super MOCK> consumer)
    {
      return new Runs(consumer);
    }

    public final class Executes<VALUE>
    {
      private final Function<? super MOCK, ? extends VALUE> function;

      private Executes(Function<? super MOCK, ? extends VALUE> function)
      {
        this.function = function;
      }

      public When<MOCK> thenReturn(VALUE value)
      {
        function.apply(Mockito.doReturn(value).when(mock));
        return When.this;
      }
    }

    public final class Runs
    {
      private final Consumer<? super MOCK> consumer;

      private Runs(Consumer<? super MOCK> consumer)
      {
        this.consumer = consumer;
      }

      public When<MOCK> doNothing()
      {
        consumer.accept(Mockito.doNothing().when(mock));
        return When.this;
      }
    }
  }

  public static final class ReturnValue<VALUE>
  {
    private final VALUE value;

    private ReturnValue(VALUE value)
    {
      this.value = value;
    }

    public <MOCK> When<MOCK> when(MOCK mock)
    {
      return new When<>(mock);
    }

    public final class When<MOCK>
    {
      private final MOCK mock;

      private When(MOCK mock)
      {
        this.mock = mock;
      }

      public void executes(Function<? super MOCK, ? extends VALUE> function)
      {
        function.apply(Mockito.doReturn(value).when(mock));
      }
    }
  }

  public static final class Given<MOCK>
  {
    private final MOCK mock;

    private Given(MOCK mock)
    {
      this.mock = mock;
    }

    public <VALUE> Executing<VALUE> executing(Function<? super MOCK, ? extends VALUE> function)
    {
      return new Executing<>(function);
    }

    public Running running(Consumer<? super MOCK> consumer)
    {
      return new Running(consumer);
    }

    public final class Executing<VALUE>
    {
      private final Function<? super MOCK, ? extends VALUE> function;

      private Executing(Function<? super MOCK, ? extends VALUE> function)
      {
        this.function = function;
      }

      public void thenReturn(VALUE value)
      {
        function.apply(Mockito.doReturn(value).when(mock));
      }
    }

    public final class Running
    {
      private final Consumer<? super MOCK> consumer;

      private Running(Consumer<? super MOCK> consumer)
      {
        this.consumer = consumer;
      }

      public void doNothing()
      {
        consumer.accept(Mockito.doNothing().when(mock));
      }
    }
  }
}