package com.spopov.footballmemory.models

import android.util.Log
import com.spopov.footballmemory.MainActivity
import com.spopov.footballmemory.MainActivity.Companion.whichleague
import com.spopov.footballmemory.utils.*

//import com.spopov.footballmemory.MainActivity.whichleague

class MemoryGame(private val boardSize: BoardSize) {

    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    companion object {
        fun whatsGoingOn() {
            Log.i("memoryleague", "memory league is $whichleague")
        }
    }

    init {
        val chosenImages =

            when (whichleague) {
                1 -> PREMIERLEAGUE.shuffled().take(boardSize.getNumPairs())
                2 -> LALIGA.shuffled().take(boardSize.getNumPairs())
                3 -> SERIEA.shuffled().take(boardSize.getNumPairs())
                4 -> BUNDESLIGA.shuffled().take(boardSize.getNumPairs())
                5 -> LIGUE1.shuffled().take(boardSize.getNumPairs())
                6 -> EREDIVISIE.shuffled().take(boardSize.getNumPairs())
                7 -> BRASILEIRAO.shuffled().take(boardSize.getNumPairs())
                8 -> CHAMPIONSHIP.shuffled().take(boardSize.getNumPairs())
                9 -> MLS.shuffled().take(boardSize.getNumPairs())
                10 -> ALEAGUE.shuffled().take(boardSize.getNumPairs())
                11 -> RESTOFEUROPE1.shuffled().take(boardSize.getNumPairs())
                12 -> RESTOFEUROPE2.shuffled().take(boardSize.getNumPairs())
                13 -> RESTOFWORLDAMERICAS.shuffled().take(boardSize.getNumPairs())
                else -> RESTOFWORLD2.shuffled().take(boardSize.getNumPairs())
            }

        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }

    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card = cards[position]

        var foundMatch = false
        if (indexOfSingleSelectedCard == null) {
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }

        card.isFaceup = !card.isFaceup
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
            card.isFaceup = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceup
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }

}

