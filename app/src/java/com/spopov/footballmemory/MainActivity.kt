package com.spopov.footballmemory

import android.animation.ArgbEvaluator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar
import com.spopov.footballmemory.models.BoardLeague
import com.spopov.footballmemory.models.BoardSize
import com.spopov.footballmemory.models.MemoryGame
import com.spopov.footballmemory.models.MemoryGame.Companion.whatsGoingOn
import com.spopov.footballmemory.utils.LALIGA
import com.spopov.footballmemory.utils.PREMIERLEAGUE


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        var whichleague = 1
    }

    private lateinit var clRoot : ConstraintLayout
    private lateinit var rvBoard : RecyclerView
    private lateinit var tvNumMoves : TextView
    private lateinit var tvNumPairs : TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize: BoardSize = BoardSize.EASY


    /*
    private var boardLeague: BoardLeague = BoardLeague.PREMIERLEAGUE

    private lateinit var radioGoup : RadioGroup
    private lateinit var rbPremierLeague : RadioButton

     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)


        setupBoard()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog ("""Are you sure you want to refresh ? 
                        |You will quit your current game !""".trimMargin(), null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
                return true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_new_league -> {
                showNewLeagueDialog()
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    /* private fun showNewLeagueDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_league_selection, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (leagueBoard) {
            leagueBoard.PREMIERLEAGUE -> radioGroupSize.check(R.id.rbPremierLeague)
            leagueBoard.LALIGA -> radioGroupSize.check(R.id.rbLaLiga)
        }
        */


    private fun showNewLeagueDialog() {
        var boardLeagueView = LayoutInflater.from(this).inflate(R.layout.dialog_league_selection, null)
        val radioGroupLeague = boardLeagueView.findViewById<RadioGroup>(R.id.radioLeague)
        showAlertDialog("Select League", boardLeagueView, View.OnClickListener {
            Log.i("League", boardLeagueView.toString())
            setupBoard()
        })
    }


    fun onRadioButtonClicked(view: View?) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when (view.getId()) {
                R.id.rbPremierLeague -> if (checked) {
                    whichleague = 1
                    Log.i("whichleague", "Premier League is $whichleague")
                    whatAmIDoing()
                }
                R.id.rbLaLiga -> if (checked) {
                    whichleague = 2
                    Log.i("whichleague", "La Liga is $whichleague")
                    whatAmIDoing()
                }
                R.id.rbSerieA -> if (checked) {
                    whichleague = 3
                    Log.i("whichleague", "La Liga is $whichleague")
                    whatAmIDoing()
                }
                R.id.rbBundesliga -> if (checked) {
                    whichleague = 4
                    Log.i("whichleague", "La Liga is $whichleague")
                    whatAmIDoing()
                }
                R.id.rbLigue1 -> if (checked) {
                    whichleague = 5
                }
                R.id.rbEredivisie -> if (checked) {
                    whichleague = 6
                }
                R.id.rbBrasileirao -> if (checked) {
                    whichleague = 7
                }
                R.id.rbChampionship -> if (checked) {
                    whichleague = 8
                }
                R.id.rbMLS -> if (checked) {
                    whichleague = 9
                }
                R.id.rbALeague -> if (checked) {
                    whichleague = 10
                }
                R.id.rbRestOfEurope1 -> if (checked) {
                    whichleague = 11
                }
                R.id.rbRestOfEurope2 -> if (checked) {
                    whichleague = 12
                }
                R.id.rbRestOfWorldAmericas -> if (checked) {
                    whichleague = 13
                }
                R.id.rbRestOfWorld2 -> if (checked) {
                    whichleague = 14
                }
            }

        }
    }
    fun whatAmIDoing(){
        whatsGoingOn()
        Log.i("whichleague", "$whichleague")
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Select Size", boardSizeView, View.OnClickListener {
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }


    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        supportActionBar?.title =

            when (whichleague) {
                1 -> "Premier League"
                2 -> "La Liga"
                3 -> "Serie A"
                4 -> "Bundesliga"
                5 -> "Ligue 1"
                6 -> "Eredivisie"
                7 -> "Brasileirao"
                8 -> "Championship"
                9 -> "MLS"
                10 -> "A-League"
                11 -> "Rest of Europe 1"
                12 -> "Rest of Europe 2"
                13 -> "RoW (Americas)"
                else -> "Rest of World 2"
            }

        when (boardSize) {
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy: 4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Medium: 6 x 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Hard: 6 x 4"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        memoryGame = MemoryGame(boardSize)
        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener {
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {

        if (memoryGame.haveWonGame()) {
            Snackbar.make(clRoot, "You already won!", Snackbar.LENGTH_LONG).show()
            return
        }

        if (memoryGame.isCardFaceUp(position)) {
            Snackbar.make(clRoot, "Invalid Move!", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (memoryGame.flipCard(position)) {
            Log.i(TAG, "Found a match! Num pairs found : ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs : ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Snackbar.make(clRoot, "You won ! Congratulations.", Snackbar.LENGTH_LONG).show()
                CommonConfetti.rainingConfetti(clRoot, intArrayOf(
                    Color.BLACK,
                    Color.GREEN,
                    //Color.CYAN,
                    //Color.WHITE,
                    //Color.BLUE,
                    Color.DKGRAY,
                    Color.GREEN,
                    //Color.RED,
                    Color.GRAY,
                    //Color.MAGENTA,
                    //Color.YELLOW
                )).oneShot()
            }
        }

        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"

        adapter.notifyDataSetChanged()
    }
}

