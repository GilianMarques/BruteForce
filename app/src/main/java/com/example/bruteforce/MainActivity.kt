package com.example.bruteforce

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bruteforce.algorithm.Contract
import com.example.bruteforce.algorithm.BruteForce
import com.example.bruteforce.algorithm.Rotor
import com.example.bruteforce.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), Contract.Callback {

    private var delayBetweenIterations: Long = 0
    private lateinit var binding: ActivityMainBinding

    private var algorithm: BruteForce? = null

    private var password = "0"

    private var executionMillis = 0L
    private val uiUpdateInterval = 50L
    private var lastUiUpdate = 0L


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        adjustAppbarHeight()

        binding.tilPassword.editText?.setText("d63x")
    }

    private fun prepareToExecuteAlgorithm() {

        password = binding.tilPassword.editText?.text.toString()
        delayBetweenIterations = binding.tilPauseBetweenIterations.editText?.text.toString().ifBlank { "0" }.toLong()

        binding.fabAction.setIconResource(R.drawable.vec_stop)
        binding.fabAction.setText(R.string.Interromper)

    }

    private fun setupViews() = binding.apply {

        fabAction.setOnClickListener {
            tvHint.visibility = GONE

            if (algorithm == null) {
                prepareToExecuteAlgorithm()
                tryToExecuteAlgorithm()
            } else stopAlgorithm()
        }

        tilPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                // Código a ser executado após a alteração do texto
                analyzePasswordAndUpdateUi(s.toString())
                binding.tilInitChar.editText?.setText("${s.toString().length}")
            }
        })
    }

    private fun stopAlgorithm() {

        binding.fabAction.setIconResource(R.drawable.vec_lock)
        binding.fabAction.setText(R.string.Executar)
        algorithm!!.cancel()
        algorithm = null
    }

    private fun adjustAppbarHeight() {
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        val appBarHeight = (screenHeight * 0.4).toInt()
        val appBarLayout = binding.appbar

        val layoutParams = appBarLayout.layoutParams
        layoutParams.height = appBarHeight
        appBarLayout.layoutParams = layoutParams
    }

    private fun notifyError(message: String) = Snackbar.make(
        binding.root, message, Snackbar.LENGTH_LONG
    ).show()

    private fun tryToExecuteAlgorithm() {

        val initialCharsCount = binding.tilInitChar.editText?.text.toString()

        if (password.isEmpty()) notifyError(getString(R.string.Insira_uma_senha_para_iniciar))
        else if (initialCharsCount.isEmpty() || initialCharsCount.toInt() < 1) notifyError(getString(R.string.Digite_um_valor_maior_que_zero))
        else if (!binding.swNum.isChecked && !binding.swAzLowercase.isChecked && !binding.swAzUppercase.isChecked && !binding.swSpecialChars.isChecked) notifyError(
            getString(R.string.Selecione_um_pelo_menos_um_tipo_de_caractere_pra_considerar_na_busca)
        )
        else {

            executionMillis = System.currentTimeMillis()

            val initChars = (initialCharsCount.takeIf { it.isNotEmpty() } ?: "1").toInt()

            algorithm = BruteForce(this@MainActivity)

            lifecycleScope.launch(Dispatchers.IO) {
                algorithm!!.execute(
                    initChars,
                    binding.swNum.isChecked,
                    binding.swAzLowercase.isChecked,
                    binding.swAzUppercase.isChecked,
                    binding.swSpecialChars.isChecked
                )
            }
        }

    }

    private fun analyzePasswordAndUpdateUi(password: String) {

        val rotor = Rotor()

        val sets = mapOf(
            rotor.numbers to binding.swNum,
            rotor.lowercaseLetters to binding.swAzLowercase,
            rotor.uppercaseLetters to binding.swAzUppercase,
            rotor.specialCharacters to binding.swSpecialChars
        )

        for ((set, switch) in sets) switch.isChecked =
            password.any { it in set.joinToString { character -> character } }

    }

    override suspend fun checkGuess(guess: String, attempts: Int): Boolean {

        val found = guess == password
        if (System.currentTimeMillis() - lastUiUpdate >= uiUpdateInterval || found) withContext(Dispatchers.Main) {

            binding.tvPassword.text = guess

            binding.tvElapsedTime.text = String.format(
                this@MainActivity.getString(R.string.Tempo_decorrido_x), formatMillis(System.currentTimeMillis() - executionMillis)
            )

            binding.tvLength.text = String.format(
                this@MainActivity.getString(R.string.comprimento_x_caractere_s), guess.length
            )

            binding.tvAttempts.text = String.format(
                this@MainActivity.getString(R.string.tentativas_x), String.format(Locale.getDefault(), "%,d", attempts)
            )

            // Resetar o FAB
            if (found) binding.fabAction.callOnClick()

            lastUiUpdate = System.currentTimeMillis()
        }

        if (delayBetweenIterations > 0) delay(delayBetweenIterations)

        return found
    }

    private fun formatMillis(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val milliseconds = millis % 1000

        return if (hours > 0) String.format(
            Locale.getDefault(), "%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds
        ) else String.format(
            Locale.getDefault(), "%02d:%02d:%03d", minutes, seconds, milliseconds
        )
    }

}
