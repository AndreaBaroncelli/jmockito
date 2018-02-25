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

    public Runs runs(Consumer<? super MOCK> consumer)
    {
      return new Runs(consumer);
    }

    public <VALUE> Executes<VALUE> executes(Function<? super MOCK, ? extends VALUE> function)
    {
      return new Executes<>(function);
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
        returnValue(value).when(mock).executes(function);
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

    public <MOCK> MockWrapper<MOCK> when(MOCK mock)
    {
      return new MockWrapper<>(mock);
    }

    public final class MockWrapper<MOCK>
    {
      private final MOCK mock;

      private MockWrapper(MOCK mock)
      {
        this.mock = mock;
      }

      public void executes(Function<? super MOCK, ? extends VALUE> function)
      {
        function.apply(Mockito.doReturn(value).when(mock));
      }
    }
  }
}