package com.example.mybrain.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mybrain.R;
import com.example.mybrain.Sys.MainActivity;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class TaskManager extends AppCompatActivity {

    private Button backBt, skipButton, answerButton;
    private EditText AnswerPlain;
    private TextView Task, progressText;
    private List<Question> questions;
    private int taskIndex = 0;
    private int attempts = 0;
    private String currentProjId;
    private static final String API_BASE_URL = "https://api.fipi.sitebydev.ru/";
    private ApiService apiService;

    private static final Map<String, String> SUBJECTS = new HashMap<String, String>() {{
        put("Английский язык", "4B53A6CB75B0B5E1427E596EB4931A2A");
        put("Биология", "CA9D848A31849ED149D382C32A7A2BE4");
        put("География", "20E79180061DB32845C11FC7BD87C7C8");
        put("Информатика и ИКТ", "B9ACA5BBB2E19E434CD6BEC25284C67F");
        put("Испанский язык", "8C65A335D93D9DA047C42613F61416F3");
        put("История", "068A227D253BA6C04D0C832387FD0D89");
        put("Китайский язык", "F6298F3470D898D043E18BC680F60434");
        put("Литература", "4F431E63B9C9B25246F00AD7B5253996");
        put("Математика. Базовый уровень", "E040A72A1A3DABA14C90C97E0B6EE7DC");
        put("Математика. Профильный уровень", "AC437B34557F88EA4115D2F374B0A07B");
        put("Немецкий язык", "B5963A8D84CF9020461EAE42F37F541F");
        put("Обществознание", "756DF168F63F9A6341711C61AA5EC578");
        put("Русский язык", "AF0ED3F2557F8FFC4C06F80B6803FD26");
        put("Физика", "BA1F39653304A5B041B656915DC36B38");
        put("Французский язык", "5BAC840990A3AF0A4EE80D1B5A1F9527");
        put("Химия", "EA45D8517ABEB35140D0D83E76F14A41");
        put("Итоговое изложение", "FBCAFDDFA469AEBD4FAAED11E271A183");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_page);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        apiService = retrofit.create(ApiService.class);

        initializeViews();
        loadSubjectQuestions();
        setupClickListeners();
    }

    private void initializeViews() {
        backBt = findViewById(R.id.Bacikbutton);
        skipButton = findViewById(R.id.SkipTaskButton);
        AnswerPlain = findViewById(R.id.AnswerPlain);
        Task = findViewById(R.id.TaskText);
        answerButton = findViewById(R.id.AnswerTaskbutton);
        progressText = findViewById(R.id.progressText);
        skipButton.setVisibility(View.GONE);
    }

    private void loadSubjectQuestions() {
        String subjectName = getIntent().getStringExtra("subject");
        currentProjId = SUBJECTS.get(subjectName);

        if (currentProjId == null) {
            showErrorAndReturn("Предмет не поддерживается");
            return;
        }

        apiService.getQuestions(currentProjId, 1, 10).enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(@NonNull Call<List<Question>> call,
                                   @NonNull Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions = response.body();
                    if (questions.size() == 10) {
                        displayCurrentQuestion();
                        updateProgressIndicator();
                    } else {
                        showErrorAndReturn("Загружено только " + questions.size() + " вопросов");
                    }
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Question>> call,
                                  @NonNull Throwable t) {
                showErrorAndReturn("Ошибка сети: " + t.getMessage());
            }
        });
    }

    private void setupClickListeners() {
        backBt.setOnClickListener(v -> navigateToMain());
        answerButton.setOnClickListener(v -> validateAnswer());
        skipButton.setOnClickListener(v -> proceedToNextQuestion());
    }

    private void validateAnswer() {
        String userInput = AnswerPlain.getText().toString().trim();
        if (userInput.isEmpty()) {
            Toast.makeText(this, "Введите ответ перед проверкой", Toast.LENGTH_SHORT).show();
            return;
        }

        AnswerRequest request = new AnswerRequest(userInput);
        String currentQid = questions.get(taskIndex).id;

        apiService.checkAnswer(currentProjId, currentQid, request)
                .enqueue(new Callback<CheckResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CheckResponse> call,
                                           @NonNull Response<CheckResponse> response) {
                        if (response.isSuccessful()) {
                            handleValidationResult(response.body());
                        } else if (response.code() == 422) {
                            parseValidationError(response);
                        } else {
                            handleApiError(response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CheckResponse> call,
                                          @NonNull Throwable t) {
                        showErrorAndReturn("Сетевая ошибка: " + t.getMessage());
                    }
                });
    }

    private void handleValidationResult(CheckResponse response) {
        if (response != null && response.isCorrect) {
            Toast.makeText(this, "✓ Правильный ответ!", Toast.LENGTH_SHORT).show();
            proceedToNextQuestion();
        } else {
            attempts++;
            if (attempts >= 2) {
                proceedToNextQuestion();
            } else {
                skipButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "✗ Осталось попыток: " + (2 - attempts),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseValidationError(Response<CheckResponse> response) {
        try {
            String errorBody = response.errorBody().string();
            ValidationError error = new Gson().fromJson(errorBody, ValidationError.class);
            showErrorAndReturn("Ошибка валидации: " + error.getFirstError());
        } catch (Exception e) {
            showErrorAndReturn("Ошибка формата ответа");
        }
    }

    private void proceedToNextQuestion() {
        if (taskIndex < 9) {
            taskIndex++;
            attempts = 0;
            AnswerPlain.setText("");
            skipButton.setVisibility(View.GONE);
            displayCurrentQuestion();
            updateProgressIndicator();
        } else {
            completeAllTasks();
        }
    }

    private void displayCurrentQuestion() {
        String rawContent = questions.get(taskIndex).text;
        Task.setText(processMathML(rawContent));
    }

    private String processMathML(String content) {
        Pattern pattern = Pattern.compile("mathml\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("<math>$1</math>");
    }

    private void updateProgressIndicator() {
        progressText.setText(String.format("%d/10", taskIndex + 1));
    }

    private void completeAllTasks() {
        Toast.makeText(this, "Все задания завершены! 🎉", Toast.LENGTH_LONG).show();
        navigateToMain();
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void handleApiError(int code) {
        showErrorAndReturn("API ошибка: " + code);
    }

    private void showErrorAndReturn(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        navigateToMain();
    }

    // Retrofit интерфейс
    public interface ApiService {
        @GET("questions/{proj_id}")
        Call<List<Question>> getQuestions(
                @Path("proj_id") String projectId,
                @Query("page") int pageNumber,
                @Query("pagesize") int pageSize
        );

        @POST("check/{proj_id}/{qid}")
        Call<CheckResponse> checkAnswer(
                @Path("proj_id") String projectId,
                @Path("qid") String questionId,
                @Body AnswerRequest answer
        );
    }

    // Модели данных
    public static class Question {
        @SerializedName("id")
        String id;

        @SerializedName("text")
        String text;
    }

    public static class AnswerRequest {
        @SerializedName("answer")
        String answer;

        AnswerRequest(String answer) {
            this.answer = answer;
        }
    }

    public static class CheckResponse {
        @SerializedName("isCorrect")
        boolean isCorrect;
    }

    public static class ValidationError {
        @SerializedName("qidtail")
        List<ValidationDetail> details;

        String getFirstError() {
            if (details != null && !details.isEmpty()) {
                ValidationDetail detail = details.get(0);
                if (detail.mqg != null) return detail.mqg;
                if (detail.lqc != null) return detail.lqc.get(0).toString();
            }
            return "Неизвестная ошибка";
        }
    }

    public static class ValidationDetail {
        @SerializedName("lqc")
        List<Object> lqc;

        @SerializedName("mqg")
        String mqg;

        @SerializedName("type")
        String type;
    }
}