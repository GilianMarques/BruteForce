package com.example.bruteforce.algorithm

class BruteForce(private val callback: Contract.Callback) : Contract {

    private var passwordFound: Boolean = false
    private var execute: Boolean = true
    private val rotors: MutableList<Rotor> = mutableListOf()
    private var attempts = 0

    private var includeNumbers: Boolean = false
    private var includeLowercase: Boolean = false
    private var includeUppercase: Boolean = false
    private var includeSpecialCharacters: Boolean = false

    override suspend fun execute(
        initialLength: Int,
        includeNumbers: Boolean,
        includeLowercase: Boolean,
        includeUppercase: Boolean,
        includeSpecialCharacters: Boolean,
    ) {
        this.includeNumbers = includeNumbers
        this.includeLowercase = includeLowercase
        this.includeUppercase = includeUppercase
        this.includeSpecialCharacters = includeSpecialCharacters

        populateArray(initialLength)

        while (execute && !passwordFound) {
            // Testo todas as senhas com 1 caractere, depois adiciono outro rotor, testo todas
            // as possibilidades com senhas de 2 caracteres e depois incluo outro rotor...
            for (i in rotors.size - 1 downTo 0) {
                rotateRotorIfNecessary(rotors[i], i)
            }

            checkPassword()
            addRotorIfNecessary(rotors[0])
        }
    }

    override fun cancel() {
        execute = false
    }

    private fun populateArray(length: Int) {
        repeat(length) {
            rotors.add(
                0,
                Rotor(
                    "#${rotors.size * -1}",
                    includeNumbers,
                    includeLowercase,
                    includeUppercase,
                    includeSpecialCharacters
                )
            )
        }
    }

    private fun addRotorIfNecessary(rotor: Rotor) {
        // O rotor da extrema esquerda deve estar no último caractere para que seja válido fazer a verificação
        if (rotor.isLastCharacter()) {
            // Se todos os rotores estão no último caractere, incluo um novo rotor no início da lista
            if (rotors.count { it.isLastCharacter() } == rotors.size) {
                populateArray(1)
            }
        }
    }

    /**
     * O rotor pode girar nas seguintes situações:
     * 1 - Se for o da extrema direita
     * 2 - Se o rotor ainda não foi inicializado (índice -1)
     * 3 - Se todos os rotores à direita dele estiverem na última posição
     */
    private fun rotateRotorIfNecessary(currentRotor: Rotor, index: Int) {
        if (index == rotors.size - 1) { // 1 - Se for o da extrema direita - o último rotor da direita sempre gira.
            currentRotor.nextIndex()
        } else if (!currentRotor.initialized) { // 2 - Se o rotor ainda não foi inicializado (índice -1)
            currentRotor.nextIndex()
        } else if (rotorsResetFrom(index)) { // 3 - Se todos os rotores à direita dele estiverem na última posição
            currentRotor.nextIndex()
        }
    }

    /**
     * Itera sobre o array de rotores a partir do índice recebido (excluindo ele) até a
     * última posição do array.
     *
     * Retorna true se todos os rotores iterados estiverem resetados.
     */
    private fun rotorsResetFrom(excludedIndex: Int): Boolean {
        for (i in (excludedIndex + 1) until rotors.size)
            if (!rotors[i].hasReset) return false
        return true
    }

    private suspend fun checkPassword() {
        var guess = ""
        rotors.forEach { guess += it.getCurrentLetter() }
        passwordFound = callback.checkGuess(guess, ++attempts)
    }
}
