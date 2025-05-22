package com.example.mybrain.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mybrain.databinding.FragmentNotificationsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Загружаем и инициализируем JSON с данными пользователя
        JSONObject userData = loadUserData();

        // Получаем топ-3 предмета
        List<String> topSubjects = getTopSubjects(userData);

        // Формируем текст
        StringBuilder sb = new StringBuilder();
        if (!topSubjects.isEmpty()) {
            sb.append("Избранные предметы:\n");
            for (String subj : topSubjects) {
                sb.append("• ").append(subj).append("\n");
            }
        } else {
            sb.append("Ещё нет избранных предметов");
        }

        // Выводим в TextView
        binding.Fsubj.setText(sb.toString().trim());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /** Метод читает из internal storage файл user_data.dat и возвращает JSONObject с гарантией наличия subjects_stats */
    private JSONObject loadUserData() {
        JSONObject userData = null;
        try {
            File file = new File(requireContext().getFilesDir(), "user_data.dat");
            if (file.exists()) {
                String content = new Scanner(file).useDelimiter("\\Z").next();
                userData = new JSONObject(content);
            }
        } catch (Exception e) {
            // игнорируем: просто создадим заново ниже
        }

        if (userData == null) {
            userData = new JSONObject();
        }
        try {
            if (!userData.has("subjects_stats")) {
                userData.put("subjects_stats", new JSONObject());
            }
        } catch (JSONException e) {
            // не должно упасть, но если упало — создаём чистый раздел
            try {
                userData.put("subjects_stats", new JSONObject());
            } catch (JSONException ignored) {}
        }
        return userData;
    }

    /**
     * Возвращает список из максимум 3-х ключей subjects_stats,
     * отсортированных по убыванию count, а при равенстве — по последнему обращению
     */
    private List<String> getTopSubjects(JSONObject userData) {
        List<String> result = new ArrayList<>();
        try {
            JSONObject stats = userData.getJSONObject("subjects_stats");
            List<Map.Entry<String, JSONObject>> list = new ArrayList<>();

            Iterator<String> keys = stats.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject entry = stats.optJSONObject(key);
                if (entry != null) {
                    list.add(new AbstractMap.SimpleEntry<>(key, entry));
                }
            }

            Collections.sort(list, (a, b) -> {
                try {
                    int cntA = a.getValue().optInt("count", 0);
                    int cntB = b.getValue().optInt("count", 0);
                    if (cntA != cntB) {
                        return Integer.compare(cntB, cntA);
                    }
                    long timeA = a.getValue().optLong("lastAccessed", 0L);
                    long timeB = b.getValue().optLong("lastAccessed", 0L);
                    // более свежие — выше
                    return Long.compare(timeB, timeA);
                } catch (Exception e) {
                    return 0;
                }
            });

            for (int i = 0; i < Math.min(3, list.size()); i++) {
                result.add(list.get(i).getKey());
            }

        } catch (JSONException e) {
            // если вдруг нет раздела — вернём пустой список
        }
        return result;
    }
}
