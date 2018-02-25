# jmockito

You probably know the two popular mocking libraries Mockito and jMock: the former features a very elegant, fluent API whereas the latter is more recommended when strict checks are required on stubbing, in the spirit of having tests document exactly what happens in the code under test.

##### Mockito pros and cons
Mockito is very expressive and guides the programmer writing statements like

```java
org.mockito.Mockito.when(mock.provideIntValue()).thenReturn(5);
```

which offer the undeniable advantage of being statically type-checked, in that the compiler would prevent from writing something like

```java
org.mockito.Mockito.when(mock.provideIntValue()).thenReturn("five");
```

On the other hand Mockito, when creating mocks with ```org.mockito.Mockito.mock(clazz);```,

1. deliberately sets default behaviours for non-stubbed method invocations (for instance: should the above stubbing be missing, any invocation to method ```provideIntValue``` would peacefully return ```0```)

2. does not check that all stubbing statements are actually needed

3. encourages developers to verify explicitly that stubbed calls have indeed been performed only when they perceive it is relevant

which is what jMock users generally dislike.

##### jMock pros and cons

On the other hand, jMock is totally rigorous in checking the stubbing, but its syntax is not fluent in that it breaks the when/then method chain into two distinct consecutive method calls, as in the following example

```java
one(mock.provideIntValue).provideIntValue();
will(returnValue(5));
```

which is quite weird as well as stateful (the implementation is based on a builder) and not statically type-checked, so the developer could inadvertently write something like

```java
one(mock.provideIntValue).provideIntValue();
will(returnValue("five"));
```

and he would discover the mistake only at runtime.

##### jmockito pros

This said, jmockito is just a simple way to get the best of the two approaches:

1. if you just need to ensure that your tests do not subtly rely on Mockito default behaviours, just use class ```org.mockito.JMockito``` to create your mock: this will cause an ```org.mockito.UnexpectedInvocationError``` to be thrown when an unstubbed call is met. Unfortunately, you cannot any longer write when/then statements like above, because they would be executed, hence causing ```org.mockito.UnexpectedInvocationError``` to be thrown, so you must rewrite these statements
- in plain Mockito, like ```org.mockito.Mockito.doReturn(5).when(mock).provideIntValue();```, which is not statically type-checked, or with JMockito, like ```org.mockito.JMockito.returnValue(5).when(mock).executes(t -> t.provideIntValue());```, which guarantees compile-time type check

- as follows: ```org.mockito.JMockito.when(mock).executes(t -> t.provideIntValue()).thenReturn(5);```, which is generally preferred for sake of readability

2. if you want to ensure that all and only the provided stubbing statements were actually necessary (i.e. not only ensure that no unstubbed calls are met but also that no unnecessary stubbing was done), you just have to add a ```public @org.junit.Rule Mockery mockery = new Mockery();``` and have it instantiate the mocks (i.e. ```mockery.mock(clazz);```) as well as originate the stubbing statements (i.e. ```mockery.when(mock).executes(t -> t.provideIntValue()).thenReturn(5);```). This will make all Mockito "verify" statements unnecessary, as they are executed under the hood.

Invocations to void methods may be stubbed with ```runs``` instead of ```executes```, as in the following example:

```java
mockery.when(mock).runs(t -> t.someProcedure()).doNothing();
```

meaning that no exception is thrown.