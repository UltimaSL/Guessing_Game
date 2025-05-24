package com.example.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private lateinit var intentosRestantes: TextView
    private lateinit var mensajeDeJuego: TextView
    private lateinit var etGuess: TextInputEditText // Campo donde el usuario ingresa su número
    private lateinit var btnComprobar: Button
    private lateinit var btnRestart: Button


    private var randomNumber: Int = 0
    private var attemptsLeft: Int = 3
    private val INACTIVITY_TIMEOUT_MS: Long = 60 * 1000 // 1 minuto en milisegundos para inactividad
    private lateinit var inactivityTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intentosRestantes = findViewById(R.id.intentosRestantes)
        mensajeDeJuego = findViewById(R.id.mensajeDeJuego)
        etGuess = findViewById(R.id.etGuess)
        btnComprobar = findViewById(R.id.btnComprobar)
        btnRestart = findViewById(R.id.btnRestart)

        startNewGame()

        btnComprobar.setOnClickListener {
            checkGuess()
        }

        btnRestart.setOnClickListener {
            startNewGame()
        }
    }

    private fun startNewGame() {
        randomNumber = Random.nextInt(1, 101)
        attemptsLeft = 3

        intentosRestantes.text = "Intentos restantes: $attemptsLeft"
        mensajeDeJuego.text = "Adivina el número entre 1 y 100"
        etGuess.setText("") // borra cualquier texto previo en el campo de adivinanza
        etGuess.isEnabled = true
        btnComprobar.isEnabled = true // habilita el botón de adivinar
        btnRestart.visibility = View.GONE // oculta el botón de reiniciar al inicio del juego

        startInactivityTimer()
    }

    private fun checkGuess() {
        val guessText = etGuess.text.toString()
        if (guessText.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un número", Toast.LENGTH_SHORT).show()
            return
        }

        val guess = guessText.toIntOrNull()
        if (guess == null || guess < 1 || guess > 100) {
            Toast.makeText(this, "Por favor, ingresa un número válido entre 1 y 100", Toast.LENGTH_SHORT).show()
            return
        }

        attemptsLeft--
        intentosRestantes.text = "Intentos restantes: $attemptsLeft"

        when {
            guess == randomNumber -> {
                mensajeDeJuego.text = "¡Felicidades! ¡Adivinaste el número $randomNumber!"
                endGame(true)
            }
            guess < randomNumber -> {
                mensajeDeJuego.text = "El número es muy bajo. Inténtalo de nuevo."
            }
            else -> {
                mensajeDeJuego.text = "El número es muy alto. Inténtalo de nuevo."
            }
        }

        if (attemptsLeft == 0 && guess != randomNumber) {
            mensajeDeJuego.text = "¡Perdiste! El número era $randomNumber."
            endGame(false) // Finaliza el juego, indicando que el usuario perdió
        }
        resetInactivityTimer() // reinicia el temporizador de inactividad después de cada intento
    }

    private fun endGame(hasWon: Boolean) {
        etGuess.isEnabled = false // apaga el campo de texto
        btnComprobar.isEnabled = false // apaga el botón de adivinar
        btnRestart.visibility = View.VISIBLE // hace visible el botón de reiniciar
        inactivityTimer.cancel()
    }

    // inicia el temporizador de inactividad
    private fun startInactivityTimer() {
        // cancela temporizadores en curso para evitar múltiples temporizadores
        if (this::inactivityTimer.isInitialized) {
            inactivityTimer.cancel()
        }

        inactivityTimer = object : CountDownTimer(INACTIVITY_TIMEOUT_MS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                mensajeDeJuego.text = "Perdiste por inactividad. El número era $randomNumber."
                endGame(false)
                Toast.makeText(this@MainActivity, "Perdiste por inactividad.", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    // reinicia el temporizador de inactividad
    private fun resetInactivityTimer() {
        inactivityTimer.cancel() // Cancela el temporizador actual
        startInactivityTimer() // Inicia uno nuevo
    }

    // reinicia el temporizador al tocar la pantalla
    override fun onUserInteraction() {
        super.onUserInteraction()
        resetInactivityTimer()
    }

    // para reiniciar el temporizador cuando el app esta activa  (el botón de adivinar está habilitado)
    override fun onResume() {
        super.onResume()
        if (btnComprobar.isEnabled) {
            startInactivityTimer()
        }
    }

    //para pausar el temporizador cuando el app no este en uso
    override fun onPause() {
        super.onPause()
        if (this::inactivityTimer.isInitialized) {
            inactivityTimer.cancel()
        }
    }
}