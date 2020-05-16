package a.apkt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.InputStreamReader;
import java.net.ConnectException;

import a.apkt.backingbean.AuthAux;
import a.apkt.cypher.RsaCypher;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.HttpService;
import a.apkt.service.LoginOrSignupService;
import a.apkt.service.StringUtils;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;

public class PasswordResetActivity extends Activity {

    private String mUserPassword;
    private String mUserConfirmPassword;
    private String userEmail;
    private String passwordCode;
    private EditText mUserPasswordView;
    private EditText mUserConfirmPasswordView;
    private Button mAutentUserPasswordButtonView;
    private Activity activity = this;
    private LoginOrSignupService loginReg;
    private PasswordResetAsyncTask passwordResetAsyncTask;
    private View mProgressView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressView = activity.findViewById(R.id.activity_progress);

        mUserPasswordView = (EditText) findViewById(R.id.user_password);
        mUserConfirmPasswordView = (EditText) findViewById(R.id.user_password_confirm);
        mAutentUserPasswordButtonView = (Button) findViewById(R.id.autent_user_password_button);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "PASSWORDRESETACTIVITY");
        loginReg.getmEditor().commit();
        userEmail = loginReg.getmPrefs().getString(LoginOrSignupService.USERRESETPASSWORD_EMAIL, null);
        passwordCode = loginReg.getmPrefs().getString(LoginOrSignupService.USERRESETPASSWORD_CODE, null);

        mUserPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.user_password
                                || id == EditorInfo.IME_NULL) {
//					attemptUserAutentPassword();
                            return true;
                        }
                        return false;
                    }
                });

        mUserConfirmPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.user_password_confirm
                                || id == EditorInfo.IME_NULL) {
                            attemptUserAutentPassword();
                            return true;
                        }
                        return false;
                    }
                });

        mAutentUserPasswordButtonView
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {

                        attemptUserAutentPassword();

                    }
                });

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                // 'end !=0' deals with the case when user deletes character
                if (start == 0 && end != 0) {

                    String okchars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_!@#$%^*()-_+=,.?;:~`´";

                    if (okchars.contains(source)){
                        return source;
                    }else {
                        return "";
                    }
                }

                return "";
            }
        };

        mUserPasswordView.setFilters(filterArray);
        mUserConfirmPasswordView.setFilters(filterArray);
    }

    public void attemptUserAutentPassword() {

        // Reset errors.
        mUserPasswordView.setError(null);
        mUserConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUserPassword = mUserPasswordView.getText().toString();
        mUserConfirmPassword = mUserConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mUserPassword)) {
            mUserPasswordView
                    .setError(getString(R.string.error_field_required));
            focusView = mUserPasswordView;
            cancel = true;
        } else if (mUserPassword.length() < 8) {
            mUserPasswordView
                    .setError(getString(R.string.error_password_invalid));
            focusView = mUserPasswordView;
            cancel = true;
        } else if (!StringUtils.containsDigit(mUserPassword)) {
            mUserPasswordView
                    .setError(getString(R.string.error_password_digit));
            focusView = mUserPasswordView;
            cancel = true;
        } else if (!StringUtils.containsUpperCase(mUserPassword)) {
            mUserPasswordView
                    .setError(getString(R.string.error_password_uppercase));
            focusView = mUserPasswordView;
            cancel = true;
            // } else if
            // (!mUserPassword.matches("[.!@#$%^&*()-_+=?<>\"';:,`~´{}]*"))
        } else if (!StringUtils.containsSpecialCharacter(mUserPassword)) {
            mUserPasswordView
                    .setError(getString(R.string.error_password_specialchar));
            focusView = mUserPasswordView;
            cancel = true;
        } else if (!TextUtils.equals(mUserPassword, mUserConfirmPassword)) {
            mUserConfirmPasswordView
                    .setError(getString(R.string.error_password_confirm_new));
            focusView = mUserConfirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUserConfirmPasswordView.setText("");
            mUserPasswordView.setText("");
            focusView.requestFocus();
        } else {

            Boolean conn = CheckConnectivity.checkNow(activity);
            if (conn == true) {

                passwordResetAsyncTask  = new PasswordResetAsyncTask();
                passwordResetAsyncTask.execute();

            } else {
                UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            }
        }
    }

    public class PasswordResetAsyncTask extends AsyncTask<String, Void, Boolean> {

        private boolean glassfishDown = false;

        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... gcmRegId) {

            try {

                AuthAux authAux = new AuthAux();
                authAux.setEmailB(RsaCypher.encryptData(activity, userEmail));

                String dataHexHashString = UtilsService.sha512String(mUserPassword.getBytes());
                byte[] encrypted = RsaCypher.encryptData(activity, dataHexHashString);
                byte[] encoded = Base64.encodeBase64(encrypted);

                authAux.setPasswordB(encoded);

                authAux.setPasswordResetCodeB(RsaCypher.encryptData(activity, passwordCode));

                String gson = new Gson().toJson(authAux);

                // open URL, using a Reader to convert bytes to chars
                InputStreamReader reader = HttpService.post(activity, "passwordReset", gson);
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
            passwordResetAsyncTask = null;
             showProgress(false);

            if (success) {
                // removes all values from the sharedPreferences, once commit is called.
                loginReg.getmEditor().clear();
                loginReg.getmEditor().commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.alertdialog_password_reset_activity_msg_title);
                builder.setMessage(R.string.alertdialog_password_reset_activity_msg);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                dialog.dismiss();
                                Intent it = new Intent(activity, LoginActivity.class);
                                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(it);
                            }
                        }).show();


            }
            else if (!success) {
                if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(activity,
                            activity, R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);
                }else {

                }
            }
        }

        @Override
        protected void onCancelled() {
            activity = null;
            // showProgress(false);
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

