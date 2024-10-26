package com.example.bruteforce

import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.bruteforce.algoritimo.Contrato
import com.example.bruteforce.algoritimo.ForcaBruta
import com.example.bruteforce.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class MainActivity : AppCompatActivity(), Contrato.Callback {

    private var possibilidades: Long = 0
    private lateinit var binding: ActivityMainBinding

    private var algoritimo: ForcaBruta? = null

    private var senha = "0"

    private var millisExecucao = 0L
    private val intervaloAttUi = 50L
    private var utimaAttUi = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initviews()
        estenderAppbar()
    }

    private fun initviews() {
        binding.fabAction.setOnClickListener {
            senha = binding.tilSenha.editText?.text.toString()
            val initChar = binding.tilInitChar.editText?.text.toString()

            if (senha.isEmpty()) notificarErro(getString(R.string.Insira_uma_senha_para_iniciar))
            else if (initChar.isNullOrEmpty() || initChar.toInt() < 1) notificarErro(getString(R.string.Digite_um_valor_maior_que_zero))
            else if (algoritimo == null) {

                binding.fabAction.setIconResource(R.drawable.vec_stop)
                binding.fabAction.setText(R.string.Interromper)
                executarAlgoritimo()

            } else {

                binding.fabAction.setIconResource(R.drawable.vec_lock)
                binding.fabAction.setText(R.string.Executar)
                algoritimo!!.cancelar()
                algoritimo = null

            }
        }

        binding.tilSenha.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Código a ser executado antes da alteração do texto
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Código a ser executado enquanto o texto está sendo alterado
                // Aqui você pode acessar o texto alterado através de s.toString()
                val textoAtual = s.toString()
                // Faça o que precisar com o textoAtual
            }

            override fun afterTextChanged(s: Editable?) {
                // Código a ser executado após a alteração do texto
                calcularPossibilidades(s.toString())
                binding.tilInitChar.editText?.setText("${s.toString().length}")
            }
        })
    }

    private fun estenderAppbar() {
        // Obtenha a altura total da tela
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        // Defina a altura do AppBar para 1/3 da altura da tela
        val appBarHeight = (screenHeight * 0.4).toInt()
        val appBarLayout = binding.appbar

        // Atualize o layoutParams do AppBar com a nova altura
        val layoutParams = appBarLayout.layoutParams
        layoutParams.height = appBarHeight
        appBarLayout.layoutParams = layoutParams
    }

    private fun notificarErro(mensagem: String) = Snackbar.make(
        binding.root,
        mensagem,
        Snackbar.LENGTH_LONG
    ).show()

    private fun executarAlgoritimo() {

        possibilidades = calcularPossibilidades(senha)
        millisExecucao = System.currentTimeMillis()

        val initChars =
            (binding.tilInitChar.editText?.text?.toString().takeIf { !it.isNullOrEmpty() }
                ?: "1").toInt()

        algoritimo = ForcaBruta(this@MainActivity)

        lifecycleScope.launch(Dispatchers.Default) {
            algoritimo!!.executar(
                initChars,
                binding.swNum.isChecked,
                binding.swAzLowercase.isChecked,
                binding.swAzUppercase.isChecked,
                binding.swEspeciais.isChecked
            )
        }


    }

    private fun calcularPossibilidades(senha: String): Long {
        // Definindo os conjuntos de caracteres
        val conjuntos = mapOf(
            "0123456789" to binding.swNum,
            "abcdefghijklmnopqrstuvwxyz" to binding.swAzLowercase,
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" to binding.swAzUppercase,
            "!@#$%^&*()-_=+[]{},.<>?;:'\"|`~" to binding.swEspeciais
        )

        // Variáveis para contar os tipos de caracteres presentes
        var totalCaracteres = 0

        // Avaliando a senha e atualizando os switches
        for ((conjunto, switch) in conjuntos) {
            if (senha.any { it in conjunto }) {
                totalCaracteres += conjunto.length
                switch.isChecked = true
            } else {
                switch.isChecked = false
            }
        }

        // Calculando o número total de possibilidades
        return if (totalCaracteres > 0) {
            totalCaracteres.toDouble().pow(senha.length.toDouble()).toLong()
        } else {
            0
        }
    }


    override suspend fun verificar(palpite: String, tentativas: Int): Boolean {

        var encontrou = palpite == senha
        if (System.currentTimeMillis() - utimaAttUi >= intervaloAttUi || encontrou) {
            withContext(Dispatchers.Main)
            {
                binding.tvSenha.text = palpite

                binding.tvTempo.text = String.format(
                    this@MainActivity.getString(R.string.Tempo_decorrido_x),
                    formatMillis(System.currentTimeMillis() - millisExecucao)
                )

                binding.tvComp.text = String.format(
                    this@MainActivity.getString(R.string.comprimento_x_caractere_s),
                    palpite.length
                )

                binding.tvTentativas.text = String.format(
                    this@MainActivity.getString(R.string.tentativas_x),
                    String.format(Locale.getDefault(), "%,d", tentativas)
                )

                val tempoEstimado = calcularTempoEstimado(tentativas)

                binding.tvEstimativa.text = String.format(
                    this@MainActivity.getString(R.string.tempo_estimado_x), tempoEstimado
                )

                // resetar o fab
                if (encontrou) binding.fabAction.callOnClick()
            }
            utimaAttUi = System.currentTimeMillis()


        }

        return encontrou
    }

    private fun calcularTempoEstimado(tentativas: Int): String {

        val tentPorMillis = tentativas / (System.currentTimeMillis() - millisExecucao)

        Log.d(
            "USUK",
            "MainActivity.".plus("calcularTempoEstimado() tentativas = $tentativas - tempo: ${(System.currentTimeMillis() - millisExecucao)} possibilidades: $possibilidades")
        )

        return if (tentPorMillis <= 0) ""
        else formatMillis(possibilidades / tentPorMillis)

    }

    private fun formatMillis(millis: Long): String {
        val horas = TimeUnit.MILLISECONDS.toHours(millis)
        val minutos = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val segundos = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val milissegundos = millis % 1000

        return if (horas > 0) String.format(
            Locale.getDefault(),
            "%02d:%02d:%02d:%03d",
            horas,
            minutos,
            segundos,
            milissegundos
        ) else String.format(
            Locale.getDefault(),
            "%02d:%02d:%03d",
            minutos,
            segundos,
            milissegundos
        )
    }

}