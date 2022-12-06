package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createdAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText =findViewById(R.id.email_edit_text);
        passwordEditText =findViewById(R.id.password_edit_text);
        confirmPasswordEditText =findViewById(R.id.confirm_password_edit_text);
        createdAccountBtn =findViewById(R.id.create_account_button);
        progressBar =findViewById(R.id.progress_bar);
        loginBtnTextView =findViewById(R.id.login_text_veiw_btn);

        createdAccountBtn.setOnClickListener(v->createdAccount());
        loginBtnTextView.setOnClickListener(v->finish());

    }
    void createdAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (!isValidated) {
            return;
        }
        createdAccountInFirebase(email, password);
    }

    void createdAccountInFirebase(String email, String password){
        channelInProgress(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Utility.showToast(CreateAccountActivity.this, "Учетная запись успеша создана. Проверьте почту для подтверждения");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                }
        );
    }


    void channelInProgress(boolean inProgress) {
        if(inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createdAccountBtn.setVisibility((View.GONE));
        } else {
            progressBar.setVisibility(View.GONE);
            createdAccountBtn.setVisibility((View.VISIBLE));
        }
    }
    boolean validateData(String email, String password, String conformPassword) {
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Неправильно введена почта");
            return false;
        }
        if(password.length()<6) {
            passwordEditText.setError("Недостаточная длинна пароля");
            return false;
        }
        if (!password.equals(conformPassword)) {
            confirmPasswordEditText.setError("Пароли не совпадают");
            return false;
        }
        return true;
    }
}