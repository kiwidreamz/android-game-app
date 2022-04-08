package com.spopov.footballmemory.models

data class MemoryCard(
    val identifier: Int,
    var isFaceup: Boolean = false,
    var isMatched: Boolean = false
    )