package com.magaran.rtw

import com.magaran.typedmap.TypedMap

/** A pipeline block that has already failed. All operations short-circuit and preserve the failure metadata. */
class FailedPipelineBlock[A](metadataWithFailure: TypedMap) extends FalliblePipelineBlock[A] {

  override def compose[B](block: (() => A) => B): FalliblePipelineBlock[B] =
    new FailedPipelineBlock[B](metadataWithFailure)

  protected override def invokeBlock: A = throw new UnsupportedOperationException(
    "Can not invoke block on pipeline blocks that have already failed"
  )

  protected override val writeEnabled: Boolean = false

  protected override val readEnabled: Boolean = false

  protected override def failed: Boolean = true

  protected override def failedMetadata: TypedMap = metadataWithFailure

}
