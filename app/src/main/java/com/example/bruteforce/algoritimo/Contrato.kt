package com.example.bruteforce.algoritimo

interface Contrato {

    suspend fun executar(
        compInicial: Int,
        incluirNumeros: Boolean,
        incluirMinusculas: Boolean,
        incluirMaiusculas: Boolean,
        incluirEspeciais: Boolean,

        )

    fun cancelar()

    interface Callback {

        /**
         * retorne true para interromper a execução*/
        suspend fun verificar(palpite: String, tentativas: Int): Boolean
    }

}


