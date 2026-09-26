package com.saiful.findbackbd.utils

import com.saiful.findbackbd.data.model.LostFoundItem
import kotlin.math.abs
import kotlin.math.sqrt

object MatchUtils {
    fun score(a: LostFoundItem, b: LostFoundItem): Int {
        var s = 0
        if (a.category.equals(b.category, ignoreCase = true)) s += 40
        val aWords = a.name.lowercase().split(" ").toSet()
        val bWords = b.name.lowercase().split(" ").toSet()
        if (aWords.intersect(bWords).isNotEmpty()) s += 30
        val aDesc = a.description.lowercase().split(" ").toSet()
        val bDesc = b.description.lowercase().split(" ").toSet()
        if (aDesc.intersect(bDesc).isNotEmpty()) s += 15
        val dLat = a.latitude - b.latitude
        val dLon = a.longitude - b.longitude
        val dist = sqrt(dLat * dLat + dLon * dLon)
        if (dist < 0.02) s += 20
        if (abs(a.date.hashCode() - b.date.hashCode()) < 50000) s += 10
        return s.coerceAtMost(100)
    }
}
