package com.example.mybrain.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mybrain.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class DashboardFragment extends Fragment {

    private FrameLayout cardContainer;
    private TextView taskText;
    private Button btnBack, btnAnswer, btnForward;

    // предыдущие поля и flashcards остаются без изменений...
    private boolean showingAnswer = false;
    private final List<HistoryEntry> history = new ArrayList<>();
    private int currentHistoryIndex = -1;
    private final Random random = new Random();

    private static class HistoryEntry {
        final int cardIndex;
        final boolean showDefinitionAsQuestion;
        HistoryEntry(int cardIndex, boolean showDefinitionAsQuestion) {
            this.cardIndex = cardIndex;
            this.showDefinitionAsQuestion = showDefinitionAsQuestion;
        }
    }

    // Ваш набор карточек
    String[][] flashcards = {
            // Математика
            {"S = πr²", "Формула площади круга (r - радиус)"},
            {"V = (4/3)πr³", "Формула объёма шара"},
            {"sin²α + cos²α = 1", "Основное тригонометрическое тождество"},
            {"a³ - b³ = (a - b)(a² + ab + b²)", "Формула разности кубов"},
            {"ln(e) = 1", "Натуральный логарифм числа e"},
            {"∫a dx = ax + C", "Интеграл от константы"},

            // Физика
            {"F = G*(m₁m₂)/r²", "Закон всемирного тяготения"},
            {"P = ρgh", "Давление жидкости на глубине h"},
            {"Q = cmΔT", "Формула количества теплоты"},
            {"I = U/R", "Закон Ома для участка цепи"},
            {"λ = v/f", "Связь длины волны с частотой и скоростью"},
            {"A = qΔφ", "Работа электростатического поля"},

            // Химия
            {"pH = -lg[H+]", "Формула водородного показателя"},
            {"ν = V/V_m", "Количество газообразного вещества"},
            {"ω = (m_вещ/m_смеси)*100%", "Массовая доля вещества"},
            {"Al₂O₃ + 6HCl → 2AlCl₃ + 3H₂O", "Реакция оксида алюминия с соляной кислотой"},
            {"C₆H₁₂O₆ → 2C₂H₅OH + 2CO₂", "Спиртовое брожение глюкозы"},

            // Русский язык
            {"-ться/-тся", "Окончание глаголов определяется вопросом: что делать?/что делает?"},
            {"НЕ с глаголами", "Пишется раздельно, кроме исключений (негодовать, ненавидеть)"},
            {"Причастный оборот", "Выделяется запятыми, если стоит после определяемого слова"},
            {"Правописание Н/НН", "В суффиксах страдательных причастий прошедшего времени"},

            // Обществознание
            {"Инфляция", "Устойчивое повышение общего уровня цен"},
            {"Социальная стратификация", "Расслоение общества на группы по различным критериям"},
            {"Правовое государство", "Государство, ограниченное в своих действиях правом"},
            {"ВВП", "Рыночная стоимость всех конечных товаров и услуг"},

            // Биология
            {"Митохондрия", "Энергетическая станция клетки (синтез АТФ)"},
            {"Фотосинтез", "6CO₂ + 6H₂O → C₆H₁₂O₆ + 6O₂"},
            {"Митоз", "Процесс деления соматических клеток"},
            {"Фенотип", "Совокупность внешних и внутренних признаков организма"},

            // Информатика
            {"IP-адрес", "Уникальный идентификатор устройства в сети"},
            {"HTTP 404", "Код состояния: страница не найдена"},
            {"Алгоритм Дейкстры", "Поиск кратчайшего пути в графе"},
            {"O(n log n)", "Сложность быстрой сортировки (QuickSort)"},

            // История
            {"862 г.", "Призвание варягов (начало династии Рюриковичей)"},
            {"988 г.", "Крещение Руси князем Владимиром"},
            {"1242 г.", "Ледовое побоище (Александр Невский)"},
            {"1861 г.", "Отмена крепостного права в России"},
            {"1917 г.", "Великая Октябрьская социалистическая революция"},

            // География
            {"Антропогенный ландшафт", "Ландшафт, изменённый хозяйственной деятельностью"},
            {"Ингрессия", "Проникновение морских вод в понижения рельефа суши"},
            {"Субтропический пояс", "Расположен между тропическим и умеренным поясами"}
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardContainer = view.findViewById(R.id.cardContainer);
        taskText      = view.findViewById(R.id.TasksBoard);
        btnBack       = view.findViewById(R.id.btnBack);
        btnAnswer     = view.findViewById(R.id.AnswerCard);
        btnForward    = view.findViewById(R.id.btnForward);

        // увеличим расстояние до “камеры”, чтобы 3D-поворот был более заметным
        float scale = getResources().getDisplayMetrics().density;
        cardContainer.setCameraDistance(8000 * scale);

        // первая карточка
        int first = random.nextInt(flashcards.length);
        history.add(new HistoryEntry(first, true));
        currentHistoryIndex = 0;
        updateUI();

        btnAnswer.setOnClickListener(v -> flipCard());
        btnForward.setOnClickListener(v -> slideToNext(true));
        btnBack   .setOnClickListener(v -> slideToNext(false));
    }

    private void updateUI() {
        HistoryEntry e = history.get(currentHistoryIndex);
        String[] card = flashcards[e.cardIndex];
        String question = e.showDefinitionAsQuestion ? card[1] : card[0];
        String answer   = e.showDefinitionAsQuestion ? card[0] : card[1];

        taskText.setText(showingAnswer ? answer : question);
        btnBack.setEnabled(currentHistoryIndex > 0);
        btnAnswer.setText(showingAnswer ? "Вопрос" : "Ответ");
    }

    // 1) Переворот карточки: два этапа по 150ms
    private void flipCard() {
        cardContainer
                .animate()
                .rotationY(90)
                .setDuration(150)
                .withEndAction(() -> {
                    showingAnswer = !showingAnswer;
                    updateUI();
                    // сразу ставим “сбоку” в минус, чтобы плавно повернуть в 0
                    cardContainer.setRotationY(-90);
                    cardContainer
                            .animate()
                            .rotationY(0)
                            .setDuration(150)
                            .start();
                })
                .start();
    }

    private void slideToNext(boolean next) {
        // блокируем кнопки, чтобы не было двойных нажатий
        btnBack.setEnabled(false);
        btnForward.setEnabled(false);
        btnAnswer.setEnabled(false);

        float width = cardContainer.getWidth();
        float offset = next ? -width : width;

        cardContainer
                .animate()
                .translationX(offset)
                .setDuration(300)
                .withEndAction(() -> {
                    // обновляем историю
                    if (next) {
                        while (history.size() > currentHistoryIndex + 1) {
                            history.remove(history.size() - 1);
                        }
                        int nextIdx;
                        do {
                            nextIdx = random.nextInt(flashcards.length);
                        } while (nextIdx == history.get(currentHistoryIndex).cardIndex);
                        history.add(new HistoryEntry(nextIdx, true));
                        currentHistoryIndex++;
                    } else if (currentHistoryIndex > 0) {
                        currentHistoryIndex--;
                    }
                    showingAnswer = false;
                    updateUI();

                    // сразу ставим контейнер за экран справа или слева
                    cardContainer.setTranslationX(-offset);
                    // и анимируем обратно в центр
                    cardContainer
                            .animate()
                            .translationX(0)
                            .setDuration(300)
                            .withEndAction(() -> {
                                // восстанавливаем кнопки
                                btnBack.setEnabled(currentHistoryIndex > 0);
                                btnForward.setEnabled(true);
                                btnAnswer.setEnabled(true);
                            })
                            .start();
                })
                .start();
    }
}

