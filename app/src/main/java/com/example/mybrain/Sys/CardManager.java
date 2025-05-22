package com.example.mybrain.Sys;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mybrain.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardManager extends AppCompatActivity {

    private TextView taskText;
    private Button btnBack, btnAnswer;
    private boolean showingAnswer = false;
    private List<HistoryEntry> history = new ArrayList<>();
    private int currentHistoryIndex = -1;
    private Random random = new Random();

    // Внутренний класс для хранения истории
    private static class HistoryEntry {
        int cardIndex;
        boolean showDefinitionAsQuestion;

        HistoryEntry(int cardIndex, boolean showDefinitionAsQuestion) {
            this.cardIndex = cardIndex;
            this.showDefinitionAsQuestion = showDefinitionAsQuestion;
        }
    }

    String[][] flashcards = {
            // 📐 Математика (дополнено)
            {"(a + b)² = a² + 2ab + b²", "Квадрат суммы"},
            {"a² - b² = (a - b)(a + b)", "Формула разности квадратов"},
            {"V = S₁*h/3", "Объём пирамиды (S₁ - площадь основания)"},
            {"P = 2(a + b)", "Периметр прямоугольника"},
            {"e = lim(1 + 1/n)ⁿ", "Определение числа Эйлера"},
            {"tgα = sinα/cosα", "Определение тангенса"},

            // ⚛️ Физика (дополнено)
            {"F = ma", "Второй закон Ньютона"},
            {"E = mc²", "Эквивалентность массы и энергии"},
            {"E = hν", "Энергия фотона"},
            {"A = Fs cosα", "Работа силы"},
            {"p = mv", "Импульс тела"},
            {"η = (Qполез/Qзатр)*100%", "КПД тепловой машины"},

            // 🧪 Химия (дополнено)
            {"PV = nRT", "Уравнение Менделеева-Клапейрона"},
            {"m = ν*M", "Масса вещества (M - молярная масса)"},
            {"2H₂O → 2H₂↑ + O₂↑", "Электролиз воды"},
            {"m₁/m₂ = M₁/M₂", "Закон эквивалентов"},
            {"Σmвход = Σmвыход", "Закон сохранения массы"},

            // 📖 Русский язык (дополнено)
            {"Цыган встал на цыпочки", "Правописание Ы/И после Ц"},
            {"-то/-либо/-нибудь", "Через дефис с неопределёнными местоимениями"},
            {"[О]:", "Двоеточие в БСП при объяснении/раскрытии"},
            {"Не с причастиями", "Раздельно при наличии зависимых слов"},

            // 🏛️ Обществознание (дополнено)
            {"Принцип разделения властей", "Законодательная, исполнительная, судебная"},
            {"Рыночная экономика", "Свободное ценообразование и конкуренция"},
            {"Социальные нормы", "Правила поведения в обществе"},
            {"Этническая общность", "Нация, народность, племя"},

            // 🌱 Биология (дополнено)
            {"Мейоз", "Деление клеток с уменьшением хромосомного набора"},
            {"Гомологичные органы", "Сходное происхождение, разное строение (крыло летучей мыши и ласт тюленя)"},
            {"Естественный отбор", "Движущая сила эволюции"},
            {"Рефлекс", "Ответная реакция на раздражитель"},

            // 💻 Информатика (дополнено)
            {"255.255.255.0", "Маска подсети класса C"},
            {"SELECT * FROM table", "SQL-запрос выборки данных"},
            {"F(n) = F(n-1) + F(n-2)", "Рекурсивное определение чисел Фибоначчи"},
            {"SOLID", "Принципы объектно-ориентированного проектирования"},

            // 📜 История (дополнено)
            {"1380 г.", "Куликовская битва (Дмитрий Донской)"},
            {"1700-1721 гг.", "Северная война (Пётр I)"},
            {"1812 г.", "Отечественная война с Наполеоном"},
            {"1991 г.", "Распад СССР"},

            // 🗺️ География (дополнено)
            {"Эффект Кориолиса", "Отклонение движения объектов в Северном полушарии вправо"},
            {"Урбанизация", "Рост городов и городского населения"},
            {"Кучево-дождевые", "Облака вертикального развития"},
            {"Сейсмический пояс", "Тихоокеанское «огненное кольцо»"},

            // 🌍 Иностранный язык (новый раздел)
            {"Present Continuous", "Действие происходит в момент речи: I am reading"},
            {"der/die/das", "Артикли в немецком языке"},
            {"Passé composé", "Прошедшее время во французском: avoir/être + причастие"},
            {"Irregular verbs", "go → went → gone (английские исключения)"},

            // 📚 Литература (новый раздел)
            {"«Горе от ума»", "А.С. Грибоедов, жанр: комедия"},
            {"Лишний человек", "Образы Онегина, Печорина"},
            {"Серебряный век", "1890-1920-е гг. (Ахматова, Блок)"},
            {"Гротеск", "Художественное преувеличение («Нос» Гоголя)"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dashboard);

        taskText = findViewById(R.id.TasksBoard);
        btnBack = findViewById(R.id.btnBack);
        btnAnswer = findViewById(R.id.AnswerCard);
        Button btnForward = findViewById(R.id.btnForward);

        // Инициализация первой карточки
        int firstCard = random.nextInt(flashcards.length);
        history.add(new HistoryEntry(firstCard, random.nextBoolean()));
        currentHistoryIndex = 0;
        updateUI();

        btnForward.setOnClickListener(v -> moveForward());
        btnBack.setOnClickListener(v -> moveBack());
        btnAnswer.setOnClickListener(v -> toggleAnswer());
    }

    private void updateUI() {
        HistoryEntry entry = history.get(currentHistoryIndex);
        String[] card = flashcards[entry.cardIndex];

        String question = entry.showDefinitionAsQuestion ? card[1] : card[0];
        String answer = entry.showDefinitionAsQuestion ? card[0] : card[1];

        taskText.setText(showingAnswer ? answer : question);
        btnBack.setEnabled(currentHistoryIndex > 0);
        btnAnswer.setText(showingAnswer ? "Скрыть ответ" : "Показать ответ");
    }

    private void moveForward() {
        // Генерация новой уникальной карточки
        int newCard;
        do {
            newCard = random.nextInt(flashcards.length);
        } while (newCard == history.get(currentHistoryIndex).cardIndex);

        // Очистка истории после текущего индекса
        while (history.size() > currentHistoryIndex + 1) {
            history.remove(history.size() - 1);
        }

        history.add(new HistoryEntry(newCard, random.nextBoolean()));
        currentHistoryIndex++;
        showingAnswer = false;
        updateUI();
    }

    private void moveBack() {
        if (currentHistoryIndex > 0) {
            currentHistoryIndex--;
            showingAnswer = false;
            updateUI();
        }
    }

    private void toggleAnswer() {
        showingAnswer = !showingAnswer;
        updateUI();
    }
}