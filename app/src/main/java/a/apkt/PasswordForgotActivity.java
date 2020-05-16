package a.apkt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.List;

import a.apkt.backingbean.AuthAux;
import a.apkt.cypher.RsaCypher;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.HttpService;
import a.apkt.service.LoginOrSignupService;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;

public class PasswordForgotActivity extends Activity {

    private Button mEmailButton;
    private String selectedEmailAccount = null;
    private List<String> emailAccounts;
    private PasswordForgotActivity passwordForgotActivity = this;
    private LoginOrSignupService loginReg;
    private EditText mEmailView;
    private Spinner mEmailSpinner;
    private PasswordForgotAsyncTask passwordForgotAsyncTask ;
    private View mProgressView;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgot);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "PASSWORDFORGOTACTIVITY");
        loginReg.getmEditor().commit();

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressView = passwordForgotActivity.findViewById(R.id.activity_progress);

        mEmailView = (EditText) findViewById(R.id.editText1);
        mEmailSpinner = (Spinner) findViewById(R.id.spinner1);

        mEmailButton = (Button) findViewById(R.id.autent_email_button);

        emailAccounts = UtilsService.getRegisteredEmailAddresses(passwordForgotActivity);

        if(emailAccounts == null) {
            mEmailButton.setVisibility(View.GONE);
            mEmailSpinner.setVisibility(View.GONE);
            mEmailView.setVisibility(View.GONE);

//            UserMsgService.showDialogFinish(autentEmailActivity,
//                    R.string.autent_gmail_message_title,
//                    R.string.autent_gmail_message);
        }else
        if (emailAccounts.size() == 1){
            mEmailButton.setEnabled(true);
            mEmailSpinner.setVisibility(View.GONE);

            selectedEmailAccount = emailAccounts.get(0);

            mEmailView.setText(selectedEmailAccount);
        }else
        if(emailAccounts.size() >= 2){
            mEmailButton.setEnabled(true);
            mEmailView.setVisibility(View.GONE);

//				 Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    R.layout.spinner_text_black_color, emailAccounts);
//				 Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				 Apply the adapter to the spinner
            mEmailSpinner.setAdapter(adapter);

            mEmailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                    selectedEmailAccount = emailAccounts.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
        }

        mEmailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Boolean conn = CheckConnectivity.checkNow(passwordForgotActivity);
                if (conn == true) {

                    AuthAux authAux = new AuthAux();
                    try {
                        authAux.setEmailB(RsaCypher.encryptData(passwordForgotActivity, selectedEmailAccount));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String gson = new Gson().toJson(authAux);
                    PasswordForgotAsyncTask passwordForgotAsyncTask = new PasswordForgotAsyncTask();
                    passwordForgotAsyncTask.execute();

                } else {
                    UserMsgService.showDialog(passwordForgotActivity, R.string.alertdialog_no_connectivity_title,
                            R.string.alertdialog_no_connectivity);
                }

            }
        });
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class PasswordForgotAsyncTask extends AsyncTask<String, Void, Boolean> {

        private boolean glassfishDown = false;

        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... gcmRegId) {

            try {

                AuthAux authAux = new AuthAux();
                authAux.setEmailB(RsaCypher.encryptData(passwordForgotActivity, selectedEmailAccount));

                String gson = new Gson().toJson(authAux);

                // open URL, using a Reader to convert bytes to chars
                InputStreamReader reader = HttpService.post(passwordForgotActivity, "passwordForgot", gson);
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
            passwordForgotAsyncTask  = null;
             showProgress(false);

            if (success) {
                loginReg.getmEditor().putString(LoginOrSignupService.USERRESETPASSWORD_EMAIL, selectedEmailAccount);
                loginReg.getmEditor().commit();
                Intent it = new Intent(passwordForgotActivity, PasswordCodeActivity.class);
                startActivity(it);
            }
            else if (!success) {
                if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(passwordForgotActivity,
                            passwordForgotActivity, R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);
                }else {
                    UserMsgService.showDialog(passwordForgotActivity,
                            R.string.error_email_invalid_forgot_password_title,
                            R.string.error_email_invalid_forgot_password);
                }
            }
        }

        @Override
        protected void onCancelled() {
            passwordForgotActivity = null;
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
            int shortAnimTime = passwordForgotActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);

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

}

