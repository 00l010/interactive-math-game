package com.example.interactivemathgame

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var answerContainer: LinearLayout
    private lateinit var resultTextView: TextView
    private lateinit var scoreContainer: LinearLayout
    private lateinit var scoreTextView: TextView
    private lateinit var restartButton: Button

    private var score = 0
    private var currentQuestionIndex = 0

    private val questions = generateRandomQuestions(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        titleTextView = findViewById(R.id.titleTextView)
        questionTextView = findViewById(R.id.questionTextView)
        answerContainer = findViewById(R.id.answerContainer)
        resultTextView = findViewById(R.id.resultTextView)
        scoreContainer = findViewById(R.id.scoreContainer)
        scoreTextView = findViewById(R.id.scoreTextView)
        restartButton = findViewById(R.id.restartButton)

        // Set up UI text
        titleTextView.text = getString(R.string.math_challenge)
        titleTextView.textSize = 24f
        titleTextView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER

        // Score container setup
        scoreContainer.setPadding(20)
        scoreContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
        scoreTextView.text = getString(R.string.score, score)

        restartButton.isEnabled = false
        restartButton.setOnClickListener { restartGame() }

        loadQuestion()
    }

    private fun loadQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            questionTextView.text = question.text
            questionTextView.textSize = 20f

            // Clear and recreate answer buttons
            answerContainer.removeAllViews()
            val rowCount = 2
            val columnCount = 2
            var index = 0
            for (i in 0 until rowCount) {
                val row = LinearLayout(this)
                row.orientation = LinearLayout.HORIZONTAL
                row.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                for (j in 0 until columnCount) {
                    if (index < question.answers.size) {
                        val answerButton = Button(this)
                        answerButton.text = question.answers[index]
                        answerButton.setOnClickListener {
                            checkAnswer(answerButton.text.toString())
                        }
                        answerButton.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        row.addView(answerButton)
                        index++
                    }
                }
                answerContainer.addView(row)
            }
        } else {
            // Game over
            resultTextView.text = getString(R.string.game_over, score)
            resultTextView.setTextColor(ContextCompat.getColor(this, R.color.primary))
            restartButton.isEnabled = true
        }
    }

    private fun checkAnswer(selectedAnswer: String) {
        val correctAnswer = questions[currentQuestionIndex].correctAnswer
        if (selectedAnswer == correctAnswer) {
            score++
            resultTextView.text = getString(R.string.correct_answer)
            resultTextView.setTextColor(ContextCompat.getColor(this, R.color.success))
        } else {
            resultTextView.text = getString(R.string.incorrect_answer)
            resultTextView.setTextColor(ContextCompat.getColor(this, R.color.error))
        }
        scoreTextView.text = getString(R.string.score, score)
        currentQuestionIndex++
        loadQuestion()
    }

    private fun restartGame() {
        score = 0
        currentQuestionIndex = 0
        resultTextView.text = ""
        scoreTextView.text = getString(R.string.score, score)
        restartButton.isEnabled = false
        loadQuestion()
    }

    companion object {
        private fun generateRandomQuestions(count: Int): List<Question> {
            val operations = listOf("+", "-", "*", "/")
            val questions = mutableListOf<Question>()

            for (i in 1..count) {
                var num1 = Random.nextInt(1, 10)
                var num2 = Random.nextInt(1, 10)
                val operation = operations.random()
                val questionText: String
                val correctAnswer: String

                when (operation) {
                    "+" -> {
                        questionText = "$num1 + $num2"
                        correctAnswer = (num1 + num2).toString()
                    }
                    "-" -> {
                        questionText = "$num1 - $num2"
                        correctAnswer = (num1 - num2).toString()
                    }
                    "*" -> {
                        questionText = "$num1 * $num2"
                        correctAnswer = (num1 * num2).toString()
                    }
                    "/" -> {
                        if (num1 < num2) {
                            val temp = num1
                            num1 = num2
                            num2 = temp
                        }
                        questionText = "$num1 / $num2"
                        correctAnswer = if (num2 != 0) (num1 / num2).toString() else "0"
                    }
                    else -> continue
                }

                val answers = mutableSetOf(correctAnswer)
                while (answers.size < 4) {
                    answers.add(Random.nextInt(0, 20).toString())
                }

                questions.add(Question(questionText, answers.toList().shuffled(), correctAnswer))
            }

            return questions
        }
    }

    data class Question(val text: String, val answers: List<String>, val correctAnswer: String)
}
