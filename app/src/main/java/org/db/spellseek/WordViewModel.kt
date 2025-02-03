package org.db.spellseek

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordViewModel : ViewModel() {
    var words: List<String> = emptyList()
    val wordLimit = mutableIntStateOf(5)
    var validWords = mutableListOf<String>()
    var count: MutableList<Int> = emptyList<Int>().toMutableList()

    fun loadWords(context: Context) {
        viewModelScope.launch {
            words = withContext(Dispatchers.IO) {
                readFileFromAssets(context, "words.txt").split("\\s+".toRegex())
            }
        }
    }

    fun findWords(letters: String, center: String): List<String> {
        validWords = emptyList<String>().toMutableList()
        count = emptyList<Int>().toMutableList()

        for (word in words) {
            if (isValidWord(word, letters, center)) {
                validWords.add(word)
                if (isPangram(word, letters + center))
                    count.add(7)
                else
                    count.add(1)
            }
        }
        return validWords
    }

    fun isValidWord(word: String, letters: String, centerLetter: String): Boolean {
        if (word.length < wordLimit.intValue) return false
        if (!word.contains(centerLetter)) return false

        val letterSet = letters.toSet() + centerLetter.toSet()
        for (char in word) {
            if (char !in letterSet) return false
        }
        return true
    }

    fun isPangram(word: String, allLetters: String): Boolean {
        return allLetters.toSet().all { it in word.toSet() }
    }


    fun readFileFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    fun wordCount(): List<Int> {
        return count
    }
}