package com.example.mybrain.Sys;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybrain.R;

public class FirstSee extends AppCompatActivity {
    private TextView myTextView;
    private Button myButton;
    private String f_text = "";
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_time_hi);

        // Находим элементы по ID
        myTextView = findViewById(R.id.First_Start);
        myButton = findViewById(R.id.buttonN);
        myTextView.setText("Приветствую в BrainBooster!");
        // Устанавливаем обработчик нажатия на кнопку
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count == 0){
                    myTextView.setText("Здесь есть режим \"Карточки\", который позволяет ученикам изучать теорию, разбивая материал на небольшие порции. Этот метод помогает эффективно повторять информацию в формате удобных карточек, как шпаргалки.");
                    count += 1;
                }else if(count == 1){
                    myTextView.setText("Так же у вас будет страница с  теорией, где собрана вся необходимая информация по предмету, доступная для удобного изучения. Ученики могут легко просматривать и осваивать материал в структурированном виде.");
                    count += 1;
                }else if(count == 2){
                    myTextView.setText("Начните совершенствоваться вместе с BrainBooster!");
                    myButton.setText("Начать!");
                    count += 1;
                }if (count == 3) {
                    // 1) Сохраняем флаг о том, что экран уже показывали
                    getSharedPreferences("app_prefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("seen_welcome", true)
                            .apply();

                    // 2) Переходим в MainActivity
                    Intent intent = new Intent(FirstSee.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }

}
