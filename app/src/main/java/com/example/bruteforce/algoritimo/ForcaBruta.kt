package com.example.bruteforce.algoritimo

const val TAG = "USUCK"

class ForcaBruta(private val callback: Contrato.Callback) : Contrato {

    private var senhaEncontrada: Boolean = false
    private var executar: Boolean = true
    private val rotores: MutableList<Rotor> = mutableListOf()
    private var tentativas = 0

    var incluirNumeros: Boolean = false
    var incluirMinusculas: Boolean = false
    var incluirMaiusculas: Boolean = false
    var incluirEspeciais: Boolean = false


    override suspend fun executar(
        compInicial: Int,
        incluirNumeros: Boolean,
        incluirMinusculas: Boolean,
        incluirMaiusculas: Boolean,
        incluirEspeciais: Boolean,
    ) {
        // Inicializa as variáveis de instância com os valores recebidos
        this.incluirNumeros = incluirNumeros
        this.incluirMinusculas = incluirMinusculas
        this.incluirMaiusculas = incluirMaiusculas
        this.incluirEspeciais = incluirEspeciais

        popularArray(compInicial)

        while (executar && !senhaEncontrada) {
            // testo todas as senhas com 1 caractere, depois adiciono outro rotor, testo todas
            // as possibilidades com senhas de 2 caracteres e depois incluo outro rotor....
            for (i in rotores.size - 1 downTo 0) {
                girarRotorSeNecessario(rotores[i], i)
            }

            conferirSenha()
            addEngrenegemSeNecessario(rotores[0])

        }
    }

    override fun cancelar() {
        executar = false
    }

    private fun popularArray(comprimento: Int) {

        repeat(comprimento) {
            rotores.add(
                0,
                Rotor(
                    "#${rotores.size * -1}",
                    incluirNumeros,
                    incluirMinusculas,
                    incluirMaiusculas,
                    incluirEspeciais
                )
            )
        }
    }

    private fun addEngrenegemSeNecessario(rotor: Rotor) {

        // o rotor da extrema esquerda deve estar no ultimo caractere para que seja valido fazer a verificação
        if (rotor.ultimoCaractere()) {

            // se todos os rotores estao no ultimo caractere, incluo uma novo rotor  no inicio da lista
            if (rotores.count { it.ultimoCaractere() } == rotores.size) {
                popularArray(1)
            }
        }

    }

    /**
     * o rotor pode girar nas seguintes siuações
     * 1 - se for a da extrema direita
     * 2 - se o rotor ainda nao foi inicializado (indice -1)
     * 3 - se todas as rotores à direita dela estiverem na ultima posição
     * */
    private fun girarRotorSeNecessario(rotorAtual: Rotor, indice: Int) {

        if (indice == rotores.size - 1) { // 1 - se for a da extrema direita - a ultimo rotor da direita sempre gira.
            rotorAtual.proxIndice()
        } else if (!rotorAtual.inicializado) { //2 - se o rotor ainda nao foi inicializado (indice -1)
            rotorAtual.proxIndice()
        } else if (rotoresResetadosApartirDe(indice)) { //3 - se todas as rotores à direita dela estiverem na ultima posição
            rotorAtual.proxIndice()
        }

    }

    /**
     * Itera sobre o array de rotores a partir do indice recebido (excluindo ele) até a
     * ultima posição do array.
     *
     * Retorna true se todos os rotores iteredos estiverem resetadas
     * */
    private fun rotoresResetadosApartirDe(indiceExcl: Int): Boolean {
        for (i in (indiceExcl + 1)..<rotores.size)
            if (!rotores[i].resetou) return false
        return true
    }

    private suspend fun conferirSenha() {
        var palpite = ""
        rotores.forEach { palpite += it.getLetraAtual() }
        senhaEncontrada = callback.verificar(palpite, ++tentativas)
    }
}