package com.github.david04.liftutils.algo

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Bitmap extends Serializable{

  private var ranges = List[(Long, Long)]()

  def update(idx: Long, v: Boolean) = {if (v) ranges = set(idx, ranges) else ranges = unset(idx, ranges); this}

  def apply(idx: Long): Boolean = valueAt(idx, ranges)

  private def valueAt(idx: Long, r: List[(Long, Long)]): Boolean =
    if (r.isEmpty) false
    else if (idx < r.head._1 || idx > r.last._2) false
    else if (r.size == 1) idx >= r.head._1 && idx <= r.head._2
    else r.splitAt(r.size / 2) match {
      case (r1, r2) if idx < r2.head._1 => valueAt(idx, r1)
      case (r1, r2) => valueAt(idx, r2)
    }

  private def join(r1: List[(Long, Long)], r2: List[(Long, Long)]) =
    if (r1.last._2 != r2.head._1 - 1) r1 ::: r2
    else r1.dropRight(1) ::: List((r1.last._1, r2.head._2)) ::: r2.tail

  private def set(idx: Long, r: List[(Long, Long)]): List[(Long, Long)] =
    if (r.isEmpty) List((idx, idx))
    else if (idx < r.head._1) join(List((idx, idx)), r)
    else if (idx > r.last._2) join(r, List((idx, idx)))
    else if (r.size == 1) r // Already set
    else r.splitAt(r.size / 2) match {
      case (r1, r2) if idx < r2.head._1 => join(set(idx, r1), r2)
      case (r1, r2) => join(r1, set(idx, r2))
    }

  private def unset(idx: Long, r: List[(Long, Long)]): List[(Long, Long)] =
    if (r.isEmpty) r
    else if (idx < r.head._1) r
    else if (idx > r.last._2) r
    else if (r.size == 1)
      (if (r.head._1 < idx) Some((r.head._1, idx - 1)) else None) ::
        (if (r.head._2 > idx) Some((idx + 1, r.head._2)) else None) ::
        Nil flatten
    else r.splitAt(r.size / 2) match {
      case (r1, r2) if idx < r2.head._1 => unset(idx, r1) ::: r2
      case (r1, r2) => r1 ::: unset(idx, r2)
    }
}
