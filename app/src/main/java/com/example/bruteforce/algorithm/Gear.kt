package com.example.bruteforce.algorithm

class Gear(
    private val id: String,
    includeNumbers: Boolean,
    includeLowercase: Boolean,
    includeUppercase: Boolean,
    includeSpecialCharacters: Boolean,
) {
    constructor() : this("", false, false, false, false)

    // É true quando a engrenagem muda do último para o primeiro caractere, depois volta a ser false.
    var hasReset = false
        private set

    /* A ideia é que o índice seja definido como 0 no loop principal, o que pode não acontecer quando
       o algoritmo for executado com mais de uma engrenagem. Nesse caso, a variável "initialized" indicará à função
       que avalia se uma engrenagem deve ser rotacionada que essa engrenagem precisa se mover de -1 para 0.
       Caso isso não aconteça, ao montar o palpite, o algoritmo pode falhar.
       */
    var initialized = false
        private set

    private var index = -1

    val lowercaseLetters: List<String> = listOf(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
        "u", "v", "w", "x", "y", "z"
    )

    val uppercaseLetters: List<String> = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z"
    )

    val numbers: List<String> = listOf(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    )

    val specialCharacters: List<String> = listOf(
        "!", " ", "@", "#", "$", "%", "^", "&", "*", "(", ")",
        "-", "_", "=", "+", "{", "}", "[", "]", ":", ";",
        "\"", "'", "<", ">", ",", ".", "?", "/",
        "ç", "ã", "â", "á", "à", "é", "ê", "í", "ó", "ô",
        "ú", "ü", "Ç", "Ã", "Â", "Á", "À", "É", "Ê", "Í",
        "Ó", "Ô", "Ú", "Ü"
    )

    private val characters: MutableList<String> = mutableListOf()

    init {
        if (includeNumbers) characters.addAll(numbers)
        if (includeLowercase) characters.addAll(lowercaseLetters)
        if (includeUppercase) characters.addAll(uppercaseLetters)
        if (includeSpecialCharacters) characters.addAll(specialCharacters)
    }

    fun getCurrentLetter(): String {
        return characters[index]
    }

    fun nextIndex() {
        hasReset = false
        initialized = true
        if (index == characters.size - 1) {
            index = -1
            hasReset = true
        }
        index++
    }

    fun isLastCharacter(): Boolean {
        return index == characters.size - 1
    }

    override fun toString(): String {
        return "$id - index: $index, last letter: ${isLastCharacter()}, hasReset: $hasReset"
    }
}
