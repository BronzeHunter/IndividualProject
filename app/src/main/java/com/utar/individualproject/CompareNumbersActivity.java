package com.utar.individualproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import java.util.Random;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class CompareNumbersActivity extends AppCompatActivity {
    private int num1, num2;
    private boolean isGreaterThanQuestion;
    private int questionCount = 1;
    private int level = 1;
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 15000; // 15 секунд
    private int incorrectAnswersCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_numbers);

        TextView questionTextView = findViewById(R.id.questionTextView);
        Button yesButton = findViewById(R.id.yesButton);
        Button noButton = findViewById(R.id.noButton);

        askQuestion(questionTextView);

        yesButton.setOnClickListener(v -> checkAnswer(true));
        noButton.setOnClickListener(v -> checkAnswer(false));
    }

    @SuppressLint("SetTextI18n")
    private void askQuestion(TextView questionTextView) {
        if (questionCount == 6) { // достигнуто ли максимальное количество вопросов
            if (incorrectAnswersCount >= 2) {
                showFailedDialog();
                return;
            }
            level++;
            if (level > 10) {
                showFinalDialog();
                return;
            }
            questionCount = 1;
            incorrectAnswersCount = 0;
        }

        // Обновить текст вопроса
        TextView questionCountTextView = findViewById(R.id.questionCountTextView);
        TextView levelTextView = findViewById(R.id.levelTextView);
        TextView timerTextView = findViewById(R.id.timerTextView);
        questionCountTextView.setText("Question: " + questionCount + "/5");
        levelTextView.setText("Level: " + level);
        questionCount++; // Увеличение счетчика вопросов

        generateRandomNumbers();

        // Случайным образом определяет «больше» или «меньше».
        isGreaterThanQuestion = new Random().nextBoolean();

        // Set question based on the type of comparison
        if (isGreaterThanQuestion) {
            questionTextView.setText(num1 + " больше, чем " + num2 + "?");
        } else {
            questionTextView.setText(num1 + " меньше, чем " + num2 + "?");
        }

        startTimer(timerTextView);
    }

    private void checkAnswer(boolean userAnswer) {
        countDownTimer.cancel();
        boolean isCorrect;
        if (isGreaterThanQuestion) {
            isCorrect = num1 > num2;
        } else {
            isCorrect = num1 < num2;
        }

        String result = (userAnswer == isCorrect) ? "Правильно!" : "Неправильно!";
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        if (userAnswer != isCorrect) {
            incorrectAnswersCount++;
        }

        askQuestion(findViewById(R.id.questionTextView));
    }

    private void startTimer(TextView timerTextView) {
        // Рассчитываем длительность таймера: 30 секунд на первом уровне, уменьшается на 2 секунды за уровень, минимум 5 секунд.
        long timerDuration = Math.max(5000, 30000 - (level - 1) * 2000); // Минимум 5 секунд

        countDownTimer = new CountDownTimer(timerDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Времени осталось: " + millisUntilFinished / 1000 + " секунд");
            }

            public void onFinish() {
                incorrectAnswersCount++;
                Toast.makeText(CompareNumbersActivity.this, "Время вышло!", Toast.LENGTH_SHORT).show();
                askQuestion(findViewById(R.id.questionTextView));
            }
        }.start();
    }

    private void showFinalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Поздравляю!")
                .setMessage("Ты прошел все уровни!")
                .setPositiveButton("Start Over", (dialog, which) -> {
                    level = 1;
                    questionCount = 1;
                    incorrectAnswersCount = 0;
                    askQuestion(findViewById(R.id.questionTextView));
                })
                .setNegativeButton("Back to Main", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Неверно!")
                .setMessage("Слишком много неправильных ответов на уровне " + level + ". Попробуй снова!")
                .setPositiveButton("Попробовать снова", (dialog, which) -> {
                    questionCount = 1;
                    incorrectAnswersCount = 0;
                    askQuestion(findViewById(R.id.questionTextView));
                })
                .setCancelable(false)
                .show();
    }

    private void generateRandomNumbers() {
        Random random = new Random();
        int range = level * 5; // повышается диапазон с каждым уровнем
        num1 = random.nextInt(range);
        num2 = random.nextInt(range);

        while (num1 == num2){ // проверка на то, чтобы числа были не одинаковыми
            num1 = random.nextInt(range);
        }
    }
}
