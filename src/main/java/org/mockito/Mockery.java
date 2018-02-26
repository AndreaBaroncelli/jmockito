package org.mockito;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.exceptions.verification.NeverWantedButInvoked;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("WeakerAccess")
public class Mockery implements TestRule
{
  private final ArrayList<Verification> verifications = new ArrayList<>();
  private final ArrayList<Object> mocks = new ArrayList<>();

  public final Statement apply(Statement statement, Description description)
  {
    return new Statement()
    {
      public void evaluate() throws Throwable
      {
        verifications.clear();

        try
        {
          statement.evaluate();
        }
        catch (UnexpectedInvocationError e)
        {
          throw new NeverWantedButInvoked(e.getMessage());
        }

        verifications.forEach(Verification::verify);
        mocks.forEach(Mockito::verifyNoMoreInteractions);
      }
    };
  }

  public <MOCK> MOCK mock(Class<MOCK> mockClass)
  {
    MOCK mock = JMockito.mock(mockClass);
    mocks.add(mock);
    return mock;
  }

  public <MOCK> MOCK mock(Class<MOCK> mockClass, String name)
  {
    MOCK mock = JMockito.mock(mockClass, name);
    mocks.add(mock);
    return mock;
  }

  public <MOCK> When<MOCK> when(MOCK mock)
  {
    return new When<>(mock, verifications);
  }

  public <VALUE> ReturnValue<VALUE> returnValue(VALUE value)
  {
    return new ReturnValue<>(value, verifications);
  }

  public DoNothing doNothing()
  {
    return new DoNothing(verifications);
  }

  public <MOCK> Given<MOCK> given(MOCK mock)
  {
    return new Given<>(mock);
  }

  public void reset()
  {
    mocks.forEach(JMockito::reset);
  }

  public static final class When<MOCK>
  {
    private final MOCK mock;
    private final ArrayList<Verification> verifications;

    private When(MOCK mock, ArrayList<Verification> verifications)
    {
      this.mock = mock;
      this.verifications = verifications;
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

      public void thenReturn(VALUE value)
      {
        function.apply(Mockito.doReturn(value).when(mock));
        verifications.add(() -> function.apply(Mockito.verify(mock)));
      }
    }

    public final class Runs
    {
      private final Consumer<? super MOCK> consumer;

      private Runs(Consumer<? super MOCK> consumer)
      {
        this.consumer = consumer;
      }

      public void doNothing()
      {
        consumer.accept(Mockito.doNothing().when(mock));
        verifications.add(() -> consumer.accept(Mockito.verify(mock)));
      }
    }
  }

  public static final class ReturnValue<VALUE>
  {
    private final VALUE value;
    private final ArrayList<Verification> verifications;

    private ReturnValue(VALUE value, ArrayList<Verification> verifications)
    {
      this.value = value;
      this.verifications = verifications;
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
        verifications.add(() -> function.apply(Mockito.verify(mock)));
      }
    }
  }

  public static final class DoNothing
  {
    private final ArrayList<Verification> verifications;

    public DoNothing(ArrayList<Verification> verifications)
    {
      this.verifications = verifications;
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

      public void runs(Consumer<? super MOCK> consumer)
      {
        consumer.accept(Mockito.doNothing().when(mock));
        verifications.add(() -> consumer.accept(Mockito.verify(mock)));
      }
    }
  }

  public final class Given<MOCK>
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
        verifications.add(() -> function.apply(Mockito.verify(mock)));
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
        verifications.add(() -> consumer.accept(Mockito.verify(mock)));
      }
    }
  }

  private interface Verification
  {
    void verify();
  }
}