package com.example.mybrain.ui.dashboard;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybrain.R;

public class TheoryContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theory_detail);

        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvTopic = findViewById(R.id.tvTopic);
        TextView tvContent = findViewById(R.id.tvContent);

        // Получаем данные из Intent
        String subject = getIntent().getStringExtra("SUBJECT");
        String topic = getIntent().getStringExtra("TOPIC");

        // Устанавливаем данные
        tvSubject.setText(subject);
        tvTopic.setText(topic);
        tvContent.setText(TheoryContent.getTheory(subject, topic));
    }
}