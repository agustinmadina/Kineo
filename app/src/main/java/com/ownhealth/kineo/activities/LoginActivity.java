package com.ownhealth.kineo.activities;

/**
 * Created by Agustin Madina on 3/26/2018.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ownhealth.kineo.R;

import static com.ownhealth.kineo.utils.Constants.LOGIN_PASSWORD;
import static com.ownhealth.kineo.utils.Constants.LOGIN_USERNAME;

/**
 * Created by Agustin Madina on 3/26/2018.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextInputLayout usernameTextInput;
    private TextInputLayout passwordTextInput;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.input_username);
        passwordEditText = findViewById(R.id.input_password);
        usernameTextInput = findViewById(R.id.text_input_username);
        passwordTextInput = findViewById(R.id.text_input_password);
        loginButton = findViewById(R.id.btn_login);
        setUpListeners();
    }

    private void setUpListeners() {
        passwordEditText.setOnEditorActionListener(passwordEnterKeyListener());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     * Listener that checks if enter key was pressed while editing password field, in order to click login button instantly
     */
    EditText.OnEditorActionListener passwordEnterKeyListener() {
        return new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        };
    }


    private void login() {
        Log.d(TAG, getString(R.string.login_tag));

        if (!bothRequiredFieldsAreCompleted()) {
            Toast.makeText(getBaseContext(), R.string.login_complete_both_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.login_authenticating));
        progressDialog.show();

        final String username = usernameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (LOGIN_USERNAME.equals(username) && LOGIN_PASSWORD.equals(password)) {
                            onLoginSuccess();
                        } else {
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 500);
    }

    /**
     * Checks if username and password TextFields are filled, and sets corresponding error in TextInputLayout.
     *
     * @return whether both fields are filled or not.
     */
    public boolean bothRequiredFieldsAreCompleted() {
        boolean valid = true;

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty()) {
            usernameTextInput.setError(getString(R.string.login_username_required));
            valid = false;
        } else {
            usernameTextInput.setError(null);
            usernameEditText.getBackground().clearColorFilter();
        }

        if (password.isEmpty()) {
            passwordTextInput.setError(getString(R.string.login_password_required));
            valid = false;
        } else {
            passwordTextInput.setError(null);
            passwordEditText.getBackground().clearColorFilter();
        }

        return valid;
    }

    private void onLoginSuccess() {
        loginButton.setEnabled(true);
        Intent homeScreenIntent = new Intent(this, MainActivity.class);
        startActivity(homeScreenIntent);
        finish();
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.login_incorrect_credentials, Toast.LENGTH_SHORT).show();
        loginButton.setEnabled(true);
    }
}
