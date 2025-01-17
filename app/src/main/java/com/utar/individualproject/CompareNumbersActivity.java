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
    private int num1, num2, num3, num4;
    private String operation1, operation2;
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

        // Обработчик кнопки выхода
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Остановка таймера при уничтожении активности
        }
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
        questionCountTextView.setText("Вопрос: " + questionCount + "/5");
        levelTextView.setText("Уровень: " + level);
        questionCount++; // Увеличение счетчика вопросов

        generateRandomNumbers();
        generateOperations();

        // Случайным образом определяет «больше» или «меньше».
        isGreaterThanQuestion = new Random().nextBoolean();

        // Генерация выражений
        String expression1 = num1 + " " + (operation1.isEmpty() ? "" : operation1 + " " + num2);
        String expression2 = num3 + " " + (operation2.isEmpty() ? "" : operation2 + " " + num4);

        // Формирование вопроса
        if (isGreaterThanQuestion) {
            questionTextView.setText(expression1 + " больше, чем " + expression2 + "?");
        } else {
            questionTextView.setText(expression1 + " меньше, чем " + expression2 + "?");
        }

        startTimer(timerTextView);
    }

    private void generateOperations() {
        Random random = new Random();

        // На основе уровня добавляем операции
        if (level <= 2) {
            // Для уровней 1-2: без операций
            operation1 = "";
            operation2 = "";
        } else if (level <= 6) {
            // Для уровней 3-6: сложение и вычитание
            operation1 = random.nextBoolean() ? "+" : "-";
            operation2 = random.nextBoolean() ? "+" : "-";
        } else if (level <= 9) {
            // Для уровней 7-9: сложение, вычитание и умножение
            operation1 = random.nextBoolean() ? "+" : (random.nextBoolean() ? "-" : "*");
            operation2 = random.nextBoolean() ? "+" : (random.nextBoolean() ? "-" : "*");
        } else {
            // Для уровня 10 и выше: все операции (+, -, *, /)
            operation1 = random.nextBoolean() ? "+" : (random.nextBoolean() ? "-" : (random.nextBoolean() ? "*" : "/"));
            operation2 = random.nextBoolean() ? "+" : (random.nextBoolean() ? "-" : (random.nextBoolean() ? "*" : "/"));
        }
    }

    private void generateRandomNumbers() {
        Random random = new Random();
        int range = level * 5; // повышается диапазон с каждым уровнем
        num1 = random.nextInt(range);
        num2 = random.nextInt(range);
        num3 = random.nextInt(range);
        num4 = random.nextInt(range);

        // Убедиться, что числа для первого выражения разные
        while (num1 == num2) {
            num1 = random.nextInt(range);
        }
        // Убедиться, что числа для второго выражения разные
        while (num3 == num4) {
            num3 = random.nextInt(range);
        }

        // Дополнительная проверка на одинаковые числа в обоих выражениях
        while ((num1 == num3 && num2 == num4) || (num1 == num4 && num2 == num3)) {
            num3 = random.nextInt(range);
            num4 = random.nextInt(range);
        }
    }

    private void checkAnswer(boolean userAnswer) {
        countDownTimer.cancel();
        boolean isCorrect;

        // Вычисление результатов для первого выражения
        int result1 = calculateExpressionResult(num1, num2, operation1);
        int result2 = calculateExpressionResult(num3, num4, operation2);

        // Сравнение результатов
        if (isGreaterThanQuestion) {
            isCorrect = result1 > result2;
        } else {
            isCorrect = result1 < result2;
        }

        String result = (userAnswer == isCorrect) ? "Правильно!" : "Неправильно!";
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();

        if (userAnswer != isCorrect) {
            incorrectAnswersCount++;
        }

        askQuestion(findViewById(R.id.questionTextView));
    }

    private int calculateExpressionResult(int num1, int num2, String operation) {
        switch (operation) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "*":
                return num1 * num2;
            case "/":
                if (num2 != 0) {
                    return num1 / num2; // Простая реализация для деления, без проверки остатка
                } else {
                    return 0; // Если деление на 0, результат будет 0
                }
            default:
                return num1; // Для уровней 1-2, когда операций нет
        }
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
}
