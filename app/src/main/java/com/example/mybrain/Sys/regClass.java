package com.example.mybrain.Sys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mybrain.R;
import org.json.JSONObject;
import java.io.FileOutputStream;

public class regClass extends AppCompatActivity {

    private EditText nameInput, passInput;
    private final String[] initialSubjects = {
            "Английский язык", "Биология", "География",
            "Информатика и ИКТ", "Испанский язык", "История",
            "Китайский язык", "Литература", "Математика. Базовый уровень",
            "Математика. Профильный уровень", "Немецкий язык", "Обществознание",
            "Русский язык", "Физика", "Французский язык", "Химия", "Итоговое изложение"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        nameInput = findViewById(R.id.TextEmailAddress);
        passInput = findViewById(R.id.TextPasswordC);
        Button regButton = findViewById(R.id.RegButton);

        regButton.setOnClickListener(v -> processRegistration());
    }

    private void processRegistration() {
        String email = nameInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();

        if (!validateInput(email, password)) return;

        try {
            JSONObject userData = new JSONObject();
            userData.put("email", email);
            userData.put("password", password);

            JSONObject subjectsStats = new JSONObject();
            for (String subject : initialSubjects) {
                JSONObject subjectStats = new JSONObject();
                subjectStats.put("count", 0);
                subjectStats.put("lastAccessed", 0);
                subjectsStats.put(subject, subjectStats);
            }
            userData.put("subjects_stats", subjectsStats);

            saveUserData(userData);
            navigateToMain();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Некорректный email!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль слишком короткий!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserData(JSONObject userData) {
        try (FileOutputStream fos = openFileOutput("user_data.dat", MODE_PRIVATE)) {
            fos.write(userData.toString().getBytes());
            Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        startActivity(new Intent(this, FirstSee.class));
        finish();
    }
}