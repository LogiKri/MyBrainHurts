package com.example.mybrain.Sys;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybrain.R;

public class Motiv extends AppCompatActivity {
    private TextView MotivationText;
    private String[] motivators = {
            "Каждый шаг имеет значение. Даже если ты движешься медленно, ты все равно обгоняешь тех, кто стоит на месте. Не сравнивай свою первую главу с чужой десятой — твое время придет.",
            "Страх — это компас, указывающий на рост. Если тебе страшно, значит, ты на пороге чего-то важного. Сделай глубокий вдох и шагни вперед — именно так рождается смелость.",
            "Ты сильнее, чем думаешь. Помни: алмаз становится бриллиантом только под давлением. Сегодняшние трудности — это тренировка для завтрашних побед.",
            "Не жди идеального момента. Его не существует. Начни сейчас, с тем, что у тебя есть. Идеальные условия создаются в процессе, а не в ожидании.",
            "Ошибки — это не конец, а уроки. Каждая неудача приближает тебя к успеху. Спроси себя не «Почему я?», а «Чему это меня учит?»",
            "Твоя цель стоит потраченных сил. Когда захочется сдаться, вспомни, ради чего начал. Мечты не работают, пока не работаешь ты.",
            "Ты — автор своей жизни. Перестань переписывать чужие сценарии. Даже маленькие ежедневные выборы определяют, кем ты станешь через год.",
            "Солнце всегда возвращается после ночи. Трудные дни не вечны. Просто продолжай идти — скоро ты увидишь, как тучи рассеиваются.",
            "Сравнивай себя только с собой вчерашним. Рост на 1% каждый день через год превратится в 37-кратный прогресс. Маленькие победы тоже меняют жизнь.",
            "Ты заслуживаешь большего, чем страх. Мир полон возможностей для тех, кто решает действовать. Сегодня — лучший день, чтобы начать. Завтра будет поздно мечтать. 🔥"
    };

    private Handler handler = new Handler();
    private int currentMotivatorIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications); // Убедитесь, что используете правильный файл разметки

        MotivationText = findViewById(R.id.Description); // Получаем TextView

        // Запускаем изменение текста каждые 30 секунд
        startMotivationUpdates();
    }

    private void startMotivationUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Обновляем текст в TextView
                MotivationText.setText(motivators[currentMotivatorIndex]);

                // Переходим к следующему мотиватору
                currentMotivatorIndex = (currentMotivatorIndex + 1) % motivators.length;

                // Запланировать следующий вызов через 30 секунд
                handler.postDelayed(this, 30000);
            }
        }, 1); // Первый вызов через 30 секунд
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Останавливаем обновления при уничтожении активности
    }
}