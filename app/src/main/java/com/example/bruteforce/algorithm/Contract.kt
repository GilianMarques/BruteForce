package com.example.bruteforce.algorithm

interface Contract {

    suspend fun execute(
        initialLength: Int,
        includeNumbers: Boolean,
        includeLowercase: Boolean,
        includeUppercase: Boolean,
        includeSpecialCharacters: Boolean,
    )

    fun cancel()

    interface Callback {

        /**
         * Retorne true para interromper a execução.
         */
        suspend fun checkGuess(guess: String, attempts: Int): Boolean
    }
}
