package com.magaran.rtw

import scala.language.implicitConversions

/** An infallible pipeline block that produces a value of type `A` when executed.
  * Blocks can be composed, chained into other stages, or built into a final result.
  */
trait PipelineBlock[+A] {

  def compose[B](block: (() => A) => B): PipelineBlock[B] = new PipelineBlock[B] {
    protected override def invokeBlock: B = {
      val previousBlock = () => PipelineBlock.this.invokeBlock
      block(previousBlock)
    }

    protected override def writeEnabled: Boolean = PipelineBlock.this.writeEnabled

    protected override def readEnabled: Boolean = PipelineBlock.this.readEnabled
  }

  protected def invokeBlock: A

  protected def writeEnabled: Boolean

  protected def readEnabled: Boolean

  def asFallible: FalliblePipelineBlock[A] = new FalliblePipelineBlock[A] {

    protected override def invokeBlock: A = PipelineBlock.this.invokeBlock

    protected override def writeEnabled: Boolean = PipelineBlock.this.writeEnabled

    protected override def readEnabled: Boolean = PipelineBlock.this.readEnabled

  }

  infix def intoWriteStage[B](block: A => B)(using OperationContextProvider): PipelineBlock[B] = {
    new WritePipelineBlock(execute)(block)
  }

  infix def intoTransformStage[B](block: A => B)(using OperationContextProvider): PipelineBlock[B] = {
    new TransformPipelineBlock(execute)(block)
  }

  def build[B, C](using MetadataProvider[C], ResultBuilder[A, C, B], OperationContextProvider): B = {
    buildTargetActionWithProvidersAndExecute(identity)
  }

  def build[B, C, D](
    converter: A => B
  )(using MetadataProvider[D], ResultBuilder[B, D, C], OperationContextProvider): C = {

    buildTargetActionWithProvidersAndExecute(converter)
  }

  def execute(using OperationContextProvider): A = {
    executeTargetActionInOperationContext {
      invokeBlock
    }
  }

  private def buildTargetAction[B, C, D](
    converter: A => B,
    metadataProvider: MetadataProvider[D],
    resultBuilder: ResultBuilder[B, D, C]
  ): C = {
    resultBuilder.build(converter(invokeBlock), metadataProvider.getMetadata)
  }

  private def buildTargetActionWithProvidersAndExecute[C, B, D](
    converter: A => B
  )(using MetadataProvider[D], ResultBuilder[B, D, C], OperationContextProvider): C = {
    executeTargetActionInOperationContext {
      buildTargetAction(converter, summon, summon)
    }
  }

  // noinspection ScalaWeakerAccess
  protected def executeTargetActionInOperationContext[B](
    targetAction: => B
  )(using operationContextProvider: OperationContextProvider): B = {
    if (writeEnabled) {
      operationContextProvider.withinWriteContext {
        targetAction
      }
    } else if (readEnabled) {
      operationContextProvider.withinReadContext {
        targetAction
      }
    } else {
      targetAction
    }
  }

}

/** Provides implicit conversion from [[PipelineBlock]] to [[FalliblePipelineBlock]]. */
object PipelineBlock {
  given Conversion[PipelineBlock[?], FalliblePipelineBlock[?]] = _.asFallible
}
