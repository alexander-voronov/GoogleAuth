package ru.geekbrains.googleauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    // Используется, чтобы определить результат Activity регистрации через
// Google
    private static final int RC_SIGN_IN = 40404;
    private static final String TAG = "GoogleAuth";
    // Клиент для регистрации пользователя через Google
    private GoogleSignInClient googleSignInClient;
    // Кнопка регистрации через Google
    private com.google.android.gms.common.SignInButton buttonSignIn;
    // Кнопка выхода из Google
    private MaterialButton buttonSingOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Конфигурация запроса на регистрацию пользователя, чтобы получить
// идентификатор пользователя, его почту и основной профайл
// (регулируется параметром)
        GoogleSignInOptions gso = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
// Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(this, gso);
// Кнопка регистрации пользователя
        buttonSignIn = findViewById(R.id.sign_in_button);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                signIn();
                                            }
                                        }
        );
        // Кнопка выхода
        buttonSingOut = findViewById(R.id.sing_out_button);
        buttonSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        enableSign();
// Проверим, входил ли пользователь в это приложение через Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
// Пользователь уже входил, сделаем кнопку недоступной
            disableSign();
            //buttonSignIn.setEnabled(false);
// Обновим почтовый адрес этого пользователя и выведем его на экран
            updateUI(account.getEmail());
        }
    }

    // Получаем результаты аутентификации от окна регистрации пользователя
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
// Когда сюда возвращается Task, результаты аутентификации уже
// готовы
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Инициируем регистрацию пользователя
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Выход из учётной записи в приложении
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI("email");
                        enableSign();
                    }
                });
    }

    //https://developers.google.com/identity/sign-in/android/backend-auth?authuser=1
// Получаем данные пользователя
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account =
                    completedTask.getResult(ApiException.class);
// Регистрация прошла успешно
            disableSign();
            //buttonSignIn.setEnabled(false);
            updateUI(account.getEmail());
        } catch (ApiException e) {
// The ApiException status code indicates the detailed failure
// reason. Please refer to the GoogleSignInStatusCodes class
// reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // Обновляем данные о пользователе на экране
    private void updateUI(String idToken) {
        TextView token = findViewById(R.id.token);
        token.setText(idToken);
    }

    private void enableSign() {
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign() {
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
    }


}