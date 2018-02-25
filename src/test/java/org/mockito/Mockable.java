package org.mockito;

interface Mockable
{
  String nonVoidMethod(int n);

  void voidMethod(int n);
}