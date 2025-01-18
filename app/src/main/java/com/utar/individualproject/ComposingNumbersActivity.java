package com.utar.individualproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ComposingNumbersActivity extends AppCompatActivity {
    private int questionCount = 1;
    private int level = 1;
    private int incorrectAnswersCount = 0;
    private Random random = new Random();
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 15000; // 15 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composing_numbers);

        GridLayout num1Layout = findViewById(R.id.num1Layout);
        GridLayout num2Layout = findViewById(R.id.num2Layout);
        TextView instructionTextView = findViewById(R.id.instructionTextView);

        askQuestion(num1Layout, num2Layout, instructionTextView);

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
    private void askQuestion(GridLayout num1Layout, GridLayout num2Layout, TextView instructionTextView) {
        if (questionCount == 6) {
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

        TextView questionCountTextView = findViewById(R.id.questionCountTextView);
        TextView levelTextView = findViewById(R.id.levelTextView);
        TextView timerTextView = findViewById(R.id.timerTextView);
        questionCountTextView.setText("Вопрос: " + questionCount + "/5");
        levelTextView.setText("Уровень: " + level);
        questionCount++;

        String operation = getOperation();
        int rangeMultiplier = (operation.equals("multiplication") || operation.equals("division")) ? 2 : 5;
        int range = Math.min(level * rangeMultiplier, 10); // Ограничиваем диапазон значений для деления

        int num1, num2, correctAnswer;

        // Генерация чисел в зависимости от операции
        if (operation.equals("division")) {
            num2 = random.nextInt(range - 1) + 1; // Второе число от 1 до range
            int multiplier = random.nextInt(5) + 1; // Умножаем на случайное число от 1 до 5
            num1 = num2 * multiplier; // Первое число кратно второму
        } else {
            num1 = random.nextInt(range) + 1;
            num2 = random.nextInt(range) + 1;
        }

        // Рассчитываем правильный ответ
        correctAnswer = calculateAnswer(num1, num2, operation);

        // Устанавливаем текст инструкции
        instructionTextView.setText(getInstructionText(operation));

        // Проверяем, что ответ корректен
        if (operation.equals("division") && (num1 < num2 || correctAnswer * num2 != num1)) {
            // Если деление некорректно, регенерируем вопрос
            askQuestion(num1Layout, num2Layout, instructionTextView);
            return;
        }

        // Заполняем макет изображениями
        populateImages(num1Layout, num1, num2);
        populateImages(num2Layout, num2, num1);

        // Отображаем кнопки с ответами
        displayButtons(correctAnswer);

        // Запускаем таймер
        startTimer(timerTextView, num1Layout, num2Layout, instructionTextView, correctAnswer);
    }



    private String getOperation() {
        if (level >= 5) {
            // На уровнях 5+ включаем деление, умножение и сложение
            int operationChoice = random.nextInt(3); // 0 - division, 1 - multiplication, 2 - addition
            return operationChoice == 0 ? "division" : operationChoice == 1 ? "multiplication" : "addition";
        } else if (level >= 3) {
            // На уровнях 3-4: умножение, сложение и вычитание
            int operationChoice = random.nextInt(3); // 0 - multiplication, 1 - addition, 2 - subtraction
            return operationChoice == 0 ? "multiplication" : operationChoice == 1 ? "addition" : "subtraction";
        } else {
            // На уровнях 1-2: сложение и вычитание
            return random.nextBoolean() ? "addition" : "subtraction";
        }
    }

    private int calculateAnswer(int num1, int num2, String operation) {
        switch (operation) {
            case "addition":
                return num1 + num2;
            case "subtraction":
                return Math.abs(num1 - num2); // Разница по модулю
            case "multiplication":
                return num1 * num2;
            case "division":
                // Генерация чисел для деления так, чтобы результат всегда был целым числом
                num1 = (random.nextInt(10) + 1) * (random.nextInt(10) + 1); // Генерация кратного числа
                num2 = random.nextInt(10) + 1;
                return num1 / num2;
            default:
                return 0;
        }
    }

    private String getInstructionText(String operation) {
        switch (operation) {
            case "addition":
                return "Сколько всего объектов?";
            case "subtraction":
                return "Найди разницу между объектами";
            case "multiplication":
                return "Сколько всего объектов, если умножить?";
            case "division":
                return "Найди результат деления объектов";
            default:
                return "Инструкция";
        }
    }

    private void populateImages(GridLayout layout, int count, int excludeImage) {
        layout.removeAllViews();
        int[] imageResources = {
                R.drawable.apple,
                R.drawable.pear,
                R.drawable.nut
        };

        int randomImageIndex = random.nextInt(imageResources.length);
        while (imageResources[randomImageIndex] == excludeImage) {
            randomImageIndex = random.nextInt(imageResources.length);
        }

        int columns = 4;
        layout.setColumnCount(columns);
        layout.setRowCount((count + columns - 1) / columns);

        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResources[randomImageIndex]);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(10, 10, 10, 10);
            imageView.setLayoutParams(params);
            layout.addView(imageView);
        }
    }

    private void displayButtons(int correctAnswer) {
        Button[] buttons = new Button[4];
        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttons[3] = findViewById(R.id.button4);

        int correctButtonIndex = random.nextInt(4);
        int rangeForWrongAnswers = level >= 3 ? 30 : 10; // Диапазон неправильных ответов: 30 для уровня 3+, иначе 10.

        for (int i = 0; i < 4; i++) {
            if (i == correctButtonIndex) {
                buttons[i].setText(String.valueOf(correctAnswer));
            } else {
                int randomAnswer;
                do {
                    randomAnswer = random.nextInt(rangeForWrongAnswers) + 1; // Неправильные ответы в заданном диапазоне.
                } while (randomAnswer == correctAnswer); // Убеждаемся, что ответ не совпадает с правильным.
                buttons[i].setText(String.valueOf(randomAnswer));
            }
            int finalI = i;
            buttons[i].setOnClickListener(v -> onButtonClick(buttons[finalI], correctAnswer));
        }
    }

    private void onButtonClick(Button clickedButton, int correctAnswer) {
        countDownTimer.cancel();
        int selectedNumber = Integer.parseInt(clickedButton.getText().toString());
        if (selectedNumber == correctAnswer) {
            Toast.makeText(this, "Правильный ответ", Toast.LENGTH_SHORT).show();
        } else {
            incorrectAnswersCount++;
            Toast.makeText(this, "Неправильный ответ", Toast.LENGTH_SHORT).show();
        }

        GridLayout num1Layout = findViewById(R.id.num1Layout);
        GridLayout num2Layout = findViewById(R.id.num2Layout);
        TextView instructionTextView = findViewById(R.id.instructionTextView);
        askQuestion(num1Layout, num2Layout, instructionTextView);
    }

    private void startTimer(TextView timerTextView, GridLayout num1Layout, GridLayout num2Layout, TextView instructionTextView, int correctAnswer) {
        // Расчет продолжительности таймера: начальное значение 30 секунд, уменьшается на 2 секунды за уровень.
        long timerDuration = Math.max(5000, 30000 - (level - 1) * 2000); // Минимум 5 секунд

        countDownTimer = new CountDownTimer(timerDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Осталось: " + millisUntilFinished / 1000 + " секунд");
            }

            public void onFinish() {
                incorrectAnswersCount++;
                Toast.makeText(ComposingNumbersActivity.this, "Время вышло! Правильный ответ: " + correctAnswer, Toast.LENGTH_SHORT).show();
                askQuestion(num1Layout, num2Layout, instructionTextView);
            }
        }.start();
    }

    private void showFinalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Поздравляем!")
                .setMessage("Ты прошел все уровни! Молодец!")
                .setPositiveButton("Начать заново", (dialog, which) -> {
                    level = 1;
                    questionCount = 1;
                    GridLayout num1Layout = findViewById(R.id.num1Layout);
                    GridLayout num2Layout = findViewById(R.id.num2Layout);
                    TextView instructionTextView = findViewById(R.id.instructionTextView);
                    askQuestion(num1Layout, num2Layout, instructionTextView);
                })
                .setNegativeButton("Назад в меню", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Неудача!")
                .setMessage("У тебя слишком много неверных ответов на уровне " + level + ". Попробуй снова!")
                .setPositiveButton("Попробовать снова", (dialog, which) -> {
                    questionCount = 1;
                    incorrectAnswersCount = 0;
                    GridLayout num1Layout = findViewById(R.id.num1Layout);
                    GridLayout num2Layout = findViewById(R.id.num2Layout);
                    TextView instructionTextView = findViewById(R.id.instructionTextView);
                    askQuestion(num1Layout, num2Layout, instructionTextView);
                })
                .setCancelable(false)
                .show();
    }
}
