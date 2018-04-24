package com.ownhealth.kineo.activities;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
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
import com.ownhealth.kineo.persistence.Medic.Medic;
import com.ownhealth.kineo.viewmodel.MedicsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Agustin Madina on 4/24/2018.
 */
public class SignupActivity extends AppCompatActivity {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private MedicsViewModel mMedicsViewModel;

    @BindView(R.id.text_input_email) TextInputLayout emailTextInput;
    @BindView(R.id.text_input_user) TextInputLayout userTextInput;
    @BindView(R.id.text_input_password) TextInputLayout passwordTextInput;
    @BindView(R.id.input_user) EditText userEditText;
    @BindView(R.id.input_email) EditText emailEditText;
    @BindView(R.id.input_password) EditText passwordEditText;
    @BindView(R.id.btn_login) Button signupButton;
    @BindView(R.id.link_signin) TextView signinText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        MedicsViewModel.Factory factory = new MedicsViewModel.Factory(getApplication(), new LocalMedicRepository(JointDatabase.getInstance(getApplication()).medicDao()));
        mMedicsViewModel = ViewModelProviders.of(this, factory).get(MedicsViewModel.class);
        setUpListeners();
    }

    private void setUpListeners() {
        passwordEditText.setOnEditorActionListener(passwordEnterKeyListener());
        signupButton.setOnClickListener(v -> login());
    }

    /**
     * Listener that checks if enter key was pressed while editing password field, in order to click login button instantly
     */
    EditText.OnEditorActionListener passwordEnterKeyListener() {
        return (textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signupButton.performClick();
                return true;
            }
            return false;
        };
    }


    private void login() {
        Log.d(TAG, getString(R.string.login_tag));

        if (!requiredFieldsAreCompleted()) {
            Toast.makeText(getBaseContext(), R.string.login_complete_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.create_authenticating));

        final String username = userEditText.getText().toString();
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        Medic medic = new Medic();
        medic.setName(username);
        medic.setEmail(password);
        medic.setPassword(email);

        mMedicsViewModel.addMedic(medic).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressDialog.show();
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                        setResult(RESULT_OK, null);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), R.string.create_account_problem, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Checks if TextFields are filled, and sets corresponding error in TextInputLayout.
     *
     * @return whether fields are filled or not.
     */
    public boolean requiredFieldsAreCompleted() {
        boolean valid = true;

        String username = userEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty()) {
            userTextInput.setError(getString(R.string.login_name_surname_required));
            valid = false;
        } else {
            userTextInput.setError(null);
            userEditText.getBackground().clearColorFilter();
        }

        if (email.isEmpty()) {
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

    @OnClick(R.id.link_signin)
    void signInClick() {
        finish();
    }
}