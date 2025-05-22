package com.example.mybrain.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import androidx.fragment.app.Fragment;
import com.example.mybrain.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheoryFragment extends Fragment {
    private ExpandableListView expandableListView;
    private final HashMap<String, List<String>> topics = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theory, container, false);
        expandableListView = view.findViewById(R.id.expandableListView);

        setupData();
        setupAdapter();
        expandableListView.expandGroup(0); // Раскрываем первую группу

        return view;
    }

    private void setupData() {
        topics.put("📐 Математика", List.of(
                "Тригонометрия",
                "Алгебра",
                "Геометрия",
                "Производные",
                "Интегралы"
        ));

        topics.put("📖 Русский язык", List.of(
                "Орфография",
                "Пунктуация",
                "Синтаксис",
                "Морфология",
                "Лексика"
        ));

        topics.put("⚛️ Физика", List.of(
                "Механика",
                "Термодинамика",
                "Электродинамика",
                "Оптика",
                "Квантовая физика"
        ));
        topics.put("🌱 Биология", List.of(
                "Генетика",
                "Экосистемы",
                "Анатомия",
                "Эволюция",
                "Ботаника"
        ));

        topics.put("🧪 Химия", List.of(
                "Органическая химия",
                "Неорганическая химия",
                "Реакции",
                "Строение атома",
                "Химические связи"
        ));

        topics.put("🌍 Иностранный язык", List.of(
                "Грамматика",
                "Лексика",
                "Аудирование",
                "Письмо",
                "Говорение"
        ));

        topics.put("🏛️ Обществознание", List.of(
                "Право",
                "Экономика",
                "Политика",
                "Социология",
                "Философия"
        ));

        topics.put("📚 Литература", List.of(
                "Анализ произведений",
                "Литературные направления",
                "Поэзия",
                "Драматургия",
                "Критика"
        ));

        topics.put("🗺️ География", List.of(
                "Климат",
                "Население",
                "Геология",
                "Страноведение",
                "Экономическая география"
        ));

        topics.put("💻 Информатика и ИКТ", List.of(
                "Алгоритмы",
                "Программирование",
                "Сети",
                "Базы данных",
                "Кибербезопасность"
        ));

    }

    private void setupAdapter() {
        TheoryAdapter adapter = new TheoryAdapter(
                requireContext(),
                new ArrayList<>(topics.keySet()),
                topics
        );
        expandableListView.setAdapter(adapter);
    }
}