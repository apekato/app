package a.apkt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.ConnectException;

import a.apkt.backingbean.AuthAux;
import a.apkt.cypher.RsaCypher;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.HttpService;
import a.apkt.service.LoginOrSignupService;
import a.apkt.service.UserMsgService;

public class PasswordCodeActivity extends Activity {

    private String passwordResetCode;
    private EditText mPasswordResetCodeView;
    private Button mAutentPasswordCodeView;
    private LoginOrSignupService loginReg;
    private Activity activity = this;
    private PasswordCodeAsyncTask passwordCodeAsyncTask;
    private String userEmail;
    private View mProgressView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_code);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "PASSWORDCODEACTIVITY");
        loginReg.getmEditor().commit();
        userEmail = loginReg.getmPrefs().getString(LoginOrSignupService.USERRESETPASSWORD_EMAIL, null);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressView = activity.findViewById(R.id.activity_progress);
        mPasswordResetCodeView = (EditText) findViewById(R.id.autent_password_reset_code);

        mAutentPasswordCodeView = (Button) findViewById(R.id.autent_password_reset_code_button);

        mPasswordResetCodeView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.autent_password_reset_code
                                || id == EditorInfo.IME_NULL) {
                            attemptSmsAutentPassword();
                            return true;
                        }
                        return false;
                    }
                });

        mAutentPasswordCodeView
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        attemptSmsAutentPassword();
                    }
                });

    }

    public void attemptSmsAutentPassword() {

        // Reset errors.
        mPasswordResetCodeView.setError(null);

        // Store values at the time of the login attempt.
        passwordResetCode = mPasswordResetCodeView.getText().toString();
        passwordResetCode = passwordResetCode.replaceAll("\n", "");
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(passwordResetCode)) {
            mPasswordResetCodeView.setError(getString(R.string.error_field_required));
            focusView = mPasswordResetCodeView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mPasswordResetCodeView.setText("");
            focusView.requestFocus();
        } else {

            Boolean conn = CheckConnectivity.checkNow(activity);
            if (conn == true) {

                passwordCodeAsyncTask  = new PasswordCodeAsyncTask ();
                passwordCodeAsyncTask .execute();

            } else {
                UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            }

        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class PasswordCodeAsyncTask extends AsyncTask<String, Void, Boolean> {

        private boolean glassfishDown = false;

        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... gcmRegId) {

            try {

                AuthAux authAux = new AuthAux();
                authAux.setEmailB(RsaCypher.encryptData(activity, userEmail));
                authAux.setPasswordResetCodeB(RsaCypher.encryptData(activity, passwordResetCode));

                String gson = new Gson().toJson(authAux);

                // open URL, using a Reader to convert bytes to chars
                InputStreamReader reader = HttpService.post((Activity) activity, "passwordCode", gson);
                if (reader == null){
                    glassfishDown = true;
                    return false;
                }
                // parse the JSON back into a TextMessage
                String ok = new Gson().fromJson(reader,
                        String.class);

                if (ok != null && ok.equals(getString(R.string.ok))) {
                    return true;
                }

            } catch (ConnectException exception) {

                glassfishDown = true;
                return false;

            } catch (Exception e) {
                e.getMessage();
                glassfishDown = true;
                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            passwordCodeAsyncTask  = null;
             showProgress(false);

            if (success) {
                loginReg.getmEditor().putString(LoginOrSignupService.USERRESETPASSWORD_EMAIL, userEmail);
                loginReg.getmEditor().putString(LoginOrSignupService.USERRESETPASSWORD_CODE, passwordResetCode);
                loginReg.getmEditor().commit();
                Intent it = new Intent(activity, PasswordResetActivity.class);
                startActivity(it);
            }
            else if (!success) {
                if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(activity,
                            activity, R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);
                }else {
                    UserMsgService.showDialog(activity,
                            R.string.alertdialog_password_code_activity_msg_error_title,
                            R.string.alertdialog_password_code_activity_msg_error);
                }
            }
        }

        @Override
        protected void onCancelled() {
            activity = null;
             showProgress(false);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);

//            mActivityFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mActivityFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mActivityFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });

            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        // removes all values from the sharedPreferences, once commit is called.
        loginReg.getmEditor().clear();
        loginReg.getmEditor().commit();
        onStop();
    }
}
