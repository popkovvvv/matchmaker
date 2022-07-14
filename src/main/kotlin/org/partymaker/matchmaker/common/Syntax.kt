package org.partymaker.matchmaker.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.partymaker.matchmaker.entity.Rank

suspend fun <T> io(block: CoroutineScope.() -> T) =
    withContext(Dispatchers.IO + MDCContext()) {
        block()
    }

fun Double.calculateRank(): Rank = if (this <= 50.0) {
    Rank.LOW
} else if (this <= 150.0) {
    Rank.MIDDLE
} else Rank.HIGH

fun Rank.downGrade(): Rank = when (this) {
    Rank.HIGH -> Rank.MIDDLE
    Rank.MIDDLE -> Rank.LOW
    Rank.LOW -> Rank.LOW
}

fun Rank.upGrade(): Rank = when (this) {
    Rank.LOW -> Rank.MIDDLE
    Rank.MIDDLE -> Rank.HIGH
    Rank.HIGH -> Rank.HIGH
}
