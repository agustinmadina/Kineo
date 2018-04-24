package com.ownhealth.kineo.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ownhealth.kineo.R;
import com.ownhealth.kineo.persistence.JointDatabase;
import com.ownhealth.kineo.persistence.Medic.LocalMedicRepository;
import com.ownhealth.kineo.viewmodel.MedicsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ownhealth.kineo.utils.Constants.LOGIN_TOKEN;
import static com.ownhealth.kineo.utils.Constants.SHARED_PREFERENCES;

/**
 * Created by Agustin Madina on 3/26/2018.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;
    private MedicsViewModel mMedicsViewModel;

    @BindView(R.id.text_input_email) TextInputLayout emailTextInput;
    @BindView(R.id.text_input_password) TextInputLayout passwordTextInput;
    @BindView(R.id.input_email) EditText emailEditText;
    @BindView(R.id.input_password) EditText passwordEditText;
    @BindView(R.id.btn_login) Button loginButton;
    @BindView(R.id.link_signup)
    TextView signupButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES, 0);
        if (settings.getBoolean(LOGIN_TOKEN, false)) {
            Intent patientListIntent = new Intent(this, PatientsActivity.class);
            startActivity(patientListIntent);
            finish();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        MedicsViewModel.Factory factory = new MedicsViewModel.Factory(getApplication(), new LocalMedicRepository(JointDatabase.getInstance(getApplication()).medicDao()));
        mMedicsViewModel = ViewModelProviders.of(this, factory).get(MedicsViewModel.class);
        setUpListeners();
    }

    private void setUpListeners() {
        passwordEditText.setOnEditorActionListener(passwordEnterKeyListener());
        loginButton.setOnClickListener(v -> tryTologin());
    }

    /**
     * Listener that checks if enter key was pressed while editing password field, in order to click tryTologin button instantly
     */
    EditText.OnEditorActionListener passwordEnterKeyListener() {
        return (textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.performClick();
                return true;
            }
            return false;
        };
    }


    private void tryTologin() {
        Log.d(TAG, getString(R.string.login_tag));

        if (!bothRequiredFieldsAreCompleted()) {
            Toast.makeText(getBaseContext(), R.string.login_complete_both_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.login_authenticating));
        progressDialog.show();

        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        progressDialog.show();
        mMedicsViewModel.getMedicByEmailAndPassword(email, password).observe(this, medic -> {
            if (medic != null) {
                progressDialog.dismiss();
                onLoginSuccess();
            } else {
                progressDialog.dismiss();
                onLoginFailed();
            }
        });
    }

    /**
     * Checks if username and password TextFields are filled, and sets corresponding error in TextInputLayout.
     *
     * @return whether both fields are filled or not.
     */
    public boolean bothRequiredFieldsAreCompleted() {
        boolean valid = true;

        String username = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty()) {
            emailTextInput.setError(getString(R.string.login_email_required));
            valid = false;
        } else {
            emailTextInput.setError(null);
            emailEditText.getBackground().clearColorFilter();
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
        SharedPreferences prefs = this.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(LOGIN_TOKEN, true);
        editor.apply();
        Intent patientListIntent = new Intent(this, PatientsActivity.class);
        startActivity(patientListIntent);
        finish();
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.login_incorrect_credentials, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.link_signup)
    void signUpClick() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                onLoginSuccess();
            }
        }
    }
}
