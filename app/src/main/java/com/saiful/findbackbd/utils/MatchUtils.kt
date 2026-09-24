package com.saiful.findbackbd.utils

import com.saiful.findbackbd.data.model.LostFoundItem
import kotlin.math.abs
import kotlin.math.sqrt

object MatchUtils {
 fun score(a: LostFoundItem, b: LostFoundItem): Int {
  var s = 0
  if (a.category.equals(b.category, true)) s += 30
  if (a.name.split(" ").intersect(b.name.split(" ").toSet()).isNotEmpty()) s += 25
  if (a.description.split(" ").intersect(b.description.split(" ").toSet()).isNotEmpty()) s += 15
  val d = sqrt(
   (a.latitude - b.latitude) * (a.latitude - b.latitude) +
           (a.longitude - b.longitude) * (a.longitude - b.longitude)
  )
  if (d < 0.02) s += 20
  if (abs(a.date.hashCode() - b.date.hashCode()) < 50000) s += 10
  return s.coerceAtMost(100)
 }
}