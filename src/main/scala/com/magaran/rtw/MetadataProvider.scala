package com.magaran.rtw

/** Provides typed metadata context for pipeline stages and result builders. */
trait MetadataProvider[A] {

  def getMetadata: A

}
