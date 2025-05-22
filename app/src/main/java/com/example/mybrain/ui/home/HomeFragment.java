package com.example.mybrain.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mybrain.databinding.FragmentHomeBinding;
import com.example.mybrain.ui.TaskManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    // Массив предметов можно вынести в константу, но здесь для наглядности оставим прямо
    private final String[] subjects = {
            "Английский язык", "Биология", "География",
            "Информатика и ИКТ", "Испанский язык", "История",
            "Китайский язык", "Литература", "Математика. Базовый уровень",
            "Математика. Профильный уровень", "Немецкий язык", "Обществознание",
            "Русский язык", "Физика", "Французский язык", "Химия", "Итоговое изложение"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Сразу прячем кнопку "Получить задания"
        binding.taskStartButton.setVisibility(View.GONE);

        // Устанавливаем слушатели
        binding.btnChooseSubject.setOnClickListener(v -> showSubjectDialog());
        binding.taskStartButton .setOnClickListener(v -> {
            String subject = binding.btnChooseSubject.getText().toString();
            if (!"Выбрать предмет".equals(subject)) {
                // Запускаем TaskManager
                Intent intent = new Intent(requireActivity(), TaskManager.class);
                intent.putExtra("subject", subject);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(),
                        "Сначала выберите предмет!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSubjectDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Выберите предмет")
                .setItems(subjects, (dialog, which) -> {
                    // Пользователь выбрал предмет
                    binding.btnChooseSubject.setText(subjects[which]);
                    binding.taskStartButton.setVisibility(View.VISIBLE);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
