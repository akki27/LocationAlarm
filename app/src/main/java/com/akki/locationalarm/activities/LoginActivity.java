package com.akki.locationalarm.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.akki.locationalarm.R;
import com.akki.locationalarm.models.LoginResponse;
import com.akki.locationalarm.models.UserCredentials;
import com.akki.locationalarm.networking.APIClient;
import com.akki.locationalarm.networking.APIInterface;
import com.akki.locationalarm.services.LocationFeedService;
import com.akki.locationalarm.utils.AppPreferences;
import com.akki.locationalarm.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * * Created by v-akhilesh.chaudhary on 06/10/2018.
 *
 * A login screen that offers login via user name/password.
 * TODO: Need to provide user registration feature as well.
 * For Now for API test purpose below credentials is hardcode in login api (API1)
 * userName = "3403243489"
 * password = "masterPass"
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // UI references.
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CoordinatorLayout mLoginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    /**
    * Set up the login form
     */
    private void initViews() {
        mUserNameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mLoginLayout = findViewById(R.id.loginLayout);

    }


    /**
     * Attempts to sign in
     */
    private void attemptLogin() {

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid user name .
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!AppUtils.isUserNameValid(userName)) {
            mUserNameView.setError(getString(R.string.error_invalid_userName));
            focusView = mUserNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!AppUtils.passwordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            //TODO: Remove these hardcoding for userName and passWord once "register" api availbale.
            userName = "3403243489";
            password = "masterPass";

            doUserLogin(userName, password);
        }
    }

    private void doUserLogin(String userName, String password) {
        if(AppUtils.isNetworkAvailable(this)) {
            // Show a progress spinner, and kick off a background task to perform the user login attempt.
            showProgress(true);

            String loginToken = AppPreferences.getLoginToken(this);
            String authorizationHeaderStr = "Bearer " + loginToken;
            String contentTypeHeader = "application/json";
            String acceptHeader = "application/json";

            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            Call<LoginResponse> call = apiInterface.loginWithCredentials(authorizationHeaderStr, contentTypeHeader,
                    acceptHeader, new UserCredentials(userName, password));

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.d(TAG, "Login_onResponse");
                    showProgress(false);

                    if(response.body().getStatus().getCode() == 200) {
                        //Save Login Token for future use
                        AppPreferences.setLoginToken(LoginActivity.this, response.body().getResult().getToken());
                        openAlarmListScreen();
                    } else {
                        AppUtils.showErrorMessage(mLoginLayout, response.body().getStatus().getMessage(), android.R.color.holo_red_light);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.d(TAG, "Login_onFailure");
                    showProgress(false);
                    AppUtils.showErrorMessage(mLoginLayout, t.getCause().getMessage(), android.R.color.holo_red_light);
                }
            });
        } else {
            //TODO: Show network un-available message in snackbar
            AppUtils.showErrorMessage(mLoginLayout, getString(R.string.network_unavailable), android.R.color.holo_red_light);
        }
    }

    private void openAlarmListScreen() {
        Log.d(TAG, "Open Alarm Screen");
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
