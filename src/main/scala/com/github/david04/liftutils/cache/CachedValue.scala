package com.github.david04.liftutils.cache

class CachedValue[T](f: => T, timeout: Long) {
  var expires = 0
  var value = Option.empty[T]

  def v = {
    if (value.isEmpty || System.currentTimeMillis() >= expires) {
      value = Some(f)
    }
    value.get
  }
}

case class CacheMap[K, V](get: K => V, timeout: Long) {
  val cache = collection.mutable.Map[K, (Long, V)]()
  def apply(k: K) = cache.get(k).filter(_._1 < System.currentTimeMillis()) match {
    case Some(v) => v._2
    case None =>
      cache(k) = (System.currentTimeMillis() + timeout, get(k))
      cache(k)._2
  }
}
