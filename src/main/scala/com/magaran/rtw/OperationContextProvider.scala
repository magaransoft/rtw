package com.magaran.rtw

/** Provides read and write operation contexts for pipeline block execution.
  * Implement this trait to define how your application provides database connections
  * or other contextual resources for each stage type.
  */
trait OperationContextProvider {

  given operationContextProvider: OperationContextProvider = this

  def withinWriteContext[A](block: => A): A

  def withinReadContext[A](block: => A): A

}
