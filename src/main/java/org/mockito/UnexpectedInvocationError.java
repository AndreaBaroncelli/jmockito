package org.mockito;

import org.mockito.invocation.InvocationOnMock;

final class UnexpectedInvocationError extends AssertionError
{
  UnexpectedInvocationError(InvocationOnMock invocationOnMock)
  {
    super("unexpected invocation " + invocationOnMock.toString());
  }
}