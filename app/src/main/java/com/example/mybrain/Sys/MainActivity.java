package com.example.mybrain.Sys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.mybrain.R;
import com.example.mybrain.databinding.ActivityMainBinding;
import com.example.mybrain.ui.TaskManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialButton btnChooseSubject, taskStartButton;
    private final String[] subjects = {
            "Английский язык", "Биология", "География",
            "Информатика и ИКТ", "Испанский язык", "История",
            "Китайский язык", "Литература", "Математика. Базовый уровень",
            "Математика. Профильный уровень", "Немецкий язык", "Обществознание",
            "Русский язык", "Физика", "Французский язык", "Химия", "Итоговое изложение"
    };
    private JSONObject userData;
    private TextView Fs;
    public String[] Fsube = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!isUserRegistered()) {
            startActivity(new Intent(this, FirstSee.class));
            finish();
            return;
        }

        loadUserData();
        initUI();
        setupButtonListeners();
        setupNavigation();
    }

    private void initUI() {
        try {
            btnChooseSubject = findViewById(R.id.btnChooseSubject);
            taskStartButton = findViewById(R.id.taskStartButton);
            taskStartButton.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e("MainActivity", "UI Error: ", e);
        }
    }

    private void updateFavoriteTextView() {
        try {
            StringBuilder sb = new StringBuilder("Избранные предметы:\n");
            for (String subject : Fsube) {
                if (subject != null) {
                    sb.append("• ").append(subject).append("\n");
                }
            }
            Fs.setText(sb.toString().trim());
        } catch (Exception e) {
            Log.e("MainActivity", "Text Update Error: ", e);
        }
    }

    private void loadUserData() {
        try {
            // 1) Считаем файл, если он есть
            File file = new File(getFilesDir(), "user_data.dat");
            if (file.exists()) {
                String content = new Scanner(file).useDelimiter("\\Z").next();
                userData = new JSONObject(content);
            }

            // 2) Если файла не было или парсинг упал, создаём пустой объект
            if (userData == null) {
                userData = new JSONObject();
            }

            // 3) Гарантируем наличие раздела subjects_stats
            if (!userData.has("subjects_stats")) {
                userData.put("subjects_stats", new JSONObject());
            }

        } catch (Exception e) {
            // Ловим и IOException (Scanner), и JSONException
            Log.e("MainActivity", "Load or init Error: ", e);

            // На случай, если всё совсем плохó, восстанавливаем userData
            if (userData == null) {
                userData = new JSONObject();
                try {
                    userData.put("subjects_stats", new JSONObject());
                } catch (JSONException ex) {
                    // практически не может случиться, но пускай будет
                    Log.e("MainActivity", "JSON init error", ex);
                }
            }
        }
    }

    private void setupButtonListeners() {
        btnChooseSubject.setOnClickListener(v -> showSubjectDialog());

        taskStartButton.setOnClickListener(v -> {
            String subject = btnChooseSubject.getText().toString();
            if (!subject.equals("Выбрать предмет")) {
                updateSubjectStats(subject);
                startActivity(new Intent(this, TaskManager.class)
                        .putExtra("subject", subject));
            } else {
                Toast.makeText(this, "Выберите предмет!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSubjectDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Выберите предмет")
                .setItems(subjects, (dialog, which) -> {
                    btnChooseSubject.setText(subjects[which]);
                    taskStartButton.setVisibility(View.VISIBLE);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.nav_theory
        ).build();

        NavController navController = Navigation.findNavController(
                this,
                R.id.nav_host_fragment_activity_main
        );
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void updateSubjectStats(String subject) {
        try {
            JSONObject stats = userData.getJSONObject("subjects_stats");
            JSONObject subjectData;

            if (stats.has(subject)) {
                subjectData = stats.getJSONObject(subject);
                subjectData.put("count", subjectData.getInt("count") + 1);
            } else {
                subjectData = new JSONObject();
                subjectData.put("count", 1);
            }

            subjectData.put("lastAccessed", System.currentTimeMillis());
            stats.put(subject, subjectData);

            saveUserData();
            updateFavoriteSubjects();
            updateFavoriteTextView();
        } catch (Exception e) {
            Log.e("MainActivity", "Stats Error: ", e);
        }
    }

    private void updateFavoriteSubjects() {
        try {
            JSONObject stats = userData.getJSONObject("subjects_stats");
            List<Map.Entry<String, JSONObject>> subjectsList = new ArrayList<>();

            Iterator<String> keys = stats.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                subjectsList.add(new AbstractMap.SimpleEntry<>(key, stats.getJSONObject(key)));
            }

            Collections.sort(subjectsList, (a, b) -> {
                try {
                    int countCompare = Integer.compare(
                            b.getValue().getInt("count"),
                            a.getValue().getInt("count")
                    );
                    if (countCompare != 0) return countCompare;

                    return Long.compare(
                            a.getValue().getLong("lastAccessed"),
                            b.getValue().getLong("lastAccessed")
                    );
                } catch (Exception e) {
                    return 0;
                }
            });

            for (int i = 0; i < 3; i++) {
                Fsube[i] = (i < subjectsList.size()) ? subjectsList.get(i).getKey() : null;
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Sort Error: ", e);
        }
    }

    private void saveUserData() {
        try (FileOutputStream fos = openFileOutput("user_data.dat", MODE_PRIVATE)) {
            fos.write(userData.toString().getBytes());
        } catch (Exception e) {
            Log.e("MainActivity", "Save Error: ", e);
        }
    }

    private boolean isUserRegistered() {
        return getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("seen_welcome", false);
    }
}