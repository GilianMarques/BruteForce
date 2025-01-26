package com.example.bruteforce.algorithm

class BruteForce(private  val callback: suspend (guess: String, attempts: Int) -> Boolean) {

    private var passwordFound: Boolean = false
    private var execute: Boolean = true
    private val gears: MutableList<Gear> = mutableListOf()
    private var attempts = 0

    private var includeNumbers: Boolean = false
    private var includeLowercase: Boolean = false
    private var includeUppercase: Boolean = false
    private var includeSpecialCharacters: Boolean = false

     suspend fun execute(
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
            // Testo todas as senhas com 1 caractere, depois adiciono outra gear, testo todas
            // as possibilidades com senhas de 2 caracteres e depois incluo outra gear...
            for (i in gears.size - 1 downTo 0) {
                rotateGearIfNecessary(gears[i], i)
            }

            passGuessToBeChecked()
            addGearIfNecessary(gears[0])
        }
    }

     fun cancel() {
        execute = false
    }

    private fun populateArray(length: Int) {
        repeat(length) {
            gears.add(
                0,
                Gear(
                    "#${gears.size * -1}",
                    includeNumbers,
                    includeLowercase,
                    includeUppercase,
                    includeSpecialCharacters
                )
            )
        }
    }

    private fun addGearIfNecessary(gear: Gear) {
        // O rotor da extrema esquerda deve estar no último caractere para que seja válido fazer a verificação
        if (gear.isLastCharacter()) {
            // Se todos os rotores estão no último caractere, incluo um nova engrenagem no início da lista
            if (gears.count { it.isLastCharacter() } == gears.size) {
                populateArray(1)
            }
        }
    }

    /**
     * A engrenagem pode girar nas seguintes situações:
     * 1 - Se for a da extrema direita
     * 2 - Se a engrenagem ainda não foi inicializada (índice -1)
     * 3 - Se todas as engrenaens à direita dela estiverem na última posição
     */
    private fun rotateGearIfNecessary(currentGear: Gear, index: Int) {
        if (index == gears.size - 1) { // 1 - Se for o da extrema direita - o última engrenagem da direita sempre gira.
            currentGear.nextIndex()
        } else if (!currentGear.initialized) { // 2 - Se a engrenagem ainda não foi inicializado (índice -1)
            currentGear.nextIndex()
        } else if (gearsResetedFrom(index)) { // 3 - Se todos os rotores à direita dele estiverem na última posição
            currentGear.nextIndex()
        }
    }

    /**
     * Itera sobre o array de engrenagens a partir do índice recebido (excluindo ela) até a
     * última posição do array.
     *
     * Retorna true se todas as engrenagens iteradas estiverem resetadas.
     */
    private fun gearsResetedFrom(excludedIndex: Int): Boolean {
        for (i in (excludedIndex + 1) until gears.size)
            if (!gears[i].hasReset) return false
        return true
    }

    private suspend fun passGuessToBeChecked() {
        var guess = ""
        gears.forEach { guess += it.getCurrentLetter() }
        passwordFound = callback(guess, ++attempts)
    }
}
