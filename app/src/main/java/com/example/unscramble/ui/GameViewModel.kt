package com.example.unscramble.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel: ViewModel() {

    // Game UI state
    private var _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    init {
        resetGame()
    }

    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord
    }

    /**
     * MutableStateFlowの状態を更新すると、監視側に自動的に更新後の値が通知される。
     * 具体的には、MutableStateFlowのvalueを更新することがトリガーとなる。
     * 新たにインスタンスを代入しても状態が更新されたとはみなされないので、通知はいかない。
     */
    private fun resetGame() {
        Log.d("GameViewModel", "resetGame() step1 _uiState=$_uiState _uiState.value=${_uiState.value} uiState=${uiState.value}")
        usedWords.clear()

        // OK Pattern
        // _uiState.valueを更新することで、uiStateの状態もそれに追従する
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
        Log.d("GameViewModel", "resetGame() step2 _uiState=$_uiState _uiState=${_uiState.value} uiState=${uiState.value}")

        // NG Pattern
        // _uiStateのインスタンスを更新しちゃっているので、uiStateが参照しているインスタンスとは別物になっているので、状態更新のトリガーはされない
        _uiState = MutableStateFlow(GameUiState(currentScrambledWord = pickRandomWordAndShuffle()))
        Log.d("GameViewModel", "resetGame() step3 _uiState=$_uiState _uiState=${_uiState.value} uiState=${uiState.value}")
    }
    private fun pickRandomWordAndShuffle(): String {
        val currentWord = allWords.random()

        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            Log.d("pickRandomWordAndShuffle", "currentWord=$currentWord")
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // scramble word
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        Log.d("shuffleCurrentWord", "tempWord=${String(tempWord)}")
        return String(tempWord)
    }
}