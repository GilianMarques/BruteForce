package com.example.bruteforce.algoritimo

class Rotor(
    val id: String,
    incluirNumeros: Boolean,
    incluirMinusculas: Boolean,
    incluirMaiusculas: Boolean,
    incluirEspeciais: Boolean,
) {

    // é true quando o rotor muda do ultimo pro primeiro caractere, depois volta a ser false
    var resetou = false
        private set

    /* A ideia é que o índice seja definido como 0 no loop principal, o que pode não acontecer quando
   o algoritmo for executado com mais de um rotor. Nesse caso, a variável "inicializado" indicará à função
   que avalia se um rotor deve ser rotacionado que esse rotor precisa se mover de -1 para 0.
   Caso isso não aconteça, ao montar o palpite, o algoritmo pode falhar. */

    var inicializado = false
        private set

    private var indice = -1

    private val letrasMinusculas: List<String> = listOf(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
        "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
        "u", "v", "w", "x", "y", "z"
    )

    private val letrasMaiusculas: List<String> = listOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z"
    )

    private val numeros: List<String> = listOf(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    )

    private val caracteresEspeciais: List<String> = listOf(
        "!", "@", "#", "$", "%", "^", "&", "*", "(", ")",
        "-", "_", "=", "+", "{", "}", "[", "]", ":", ";",
        "\"", "'", "<", ">", ",", ".", "?", "/",
        "ç", "ã", "â", "á", "à", "é", "ê", "í", "ó", "ô",
        "ú", "ü", "Ç", "Ã", "Â", "Á", "À", "É", "Ê", "Í",
        "Ó", "Ô", "Ú", "Ü"
    )

    private val caracteres: MutableList<String> = mutableListOf()


    init {
        if (incluirNumeros) caracteres.addAll(numeros)
        if (incluirMinusculas) caracteres.addAll(letrasMinusculas)
        if (incluirMaiusculas) caracteres.addAll(letrasMaiusculas)
        if (incluirEspeciais) caracteres.addAll(caracteresEspeciais)
    }

    fun getLetraAtual(): String {
        return caracteres[indice]
    }

    fun proxIndice() {
        resetou = false
        inicializado = true
        if (indice == caracteres.size - 1) {
            indice = -1
            resetou = true
        }
        indice++
    }

    fun ultimoCaractere(): Boolean {
        return indice == caracteres.size - 1
    }

    override fun toString(): String {
        return "$id - indice: $indice, ult. letra: ${ultimoCaractere()}, resetou: $resetou"
    }

}