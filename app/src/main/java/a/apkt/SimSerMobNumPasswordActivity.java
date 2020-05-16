package a.apkt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLEncoder;

import a.apkt.json.LoginJson;
import a.apkt.json.RegUserPostJson;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.HttpService;
import a.apkt.service.LoginOrSignupService;
import a.apkt.service.StringUtils;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;

public class SimSerMobNumPasswordActivity extends Activity {

    private String mSmsPassword;
    private Long paramUserId;
    private String paramSmsPassword;
    private String paramMobileNumComplete;
    private String mMobileNum;
    private String mMobileNumState;
    private EditText mSmsPasswordView;
    private Button mAutentPasswordButtonView;
    //    private Intent itParams;
    private SimSerMobNumPasswordAsyncTask mSimSerMobNumPasswordAsyncTask = null;
    private LoginJson loginJson;
    private Context context = this;
    private SimSerMobNumPasswordActivity simSerMobNumPasswordActivity = this;
    private RegUserPostJson regUserPostJson;
    private LoginOrSignupService loginReg;
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_ser_mob_num_password);

        // set activity_autent_password layout fields bellow as invisible
        // since they are not used here, but are used in
        // AutentPasswordActivity.java
        EditText mUserPasswordView = (EditText) findViewById(R.id.user_password);
        mUserPasswordView.setVisibility(View.GONE);
        EditText mUserConfirmPasswordView = (EditText) findViewById(R.id.user_password_confirm);
        mUserConfirmPasswordView.setVisibility(View.GONE);
        Button mAutentUserPasswordButtonView = (Button) findViewById(R.id.autent_user_password_button);
        mAutentUserPasswordButtonView.setVisibility(View.GONE);

        mSmsPasswordView = (EditText) findViewById(R.id.autent_sms_password);

        // passwords for test: delete when not used
//        mSmsPasswordView.setText("test");

        mAutentPasswordButtonView = (Button) findViewById(R.id.autent_password_button);

        // gets parameters from AutentMobileNumActivity
//        itParams = getIntent();
//        if (itParams != null) {
//            Bundle params = itParams.getExtras();
//            if (params != null) {
//                paramUserId = params.getLong("userId");
//                paramMobileNumComplete = params.getString("mobileNumComplete");
//                paramSmsPassword = params.getString("password");
//            }
//        }

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "SIMSERMOBNUMPASSWORDACTIVITY");
        loginReg.getmEditor().commit();
        paramSmsPassword = loginReg.getmPrefs().getString(LoginOrSignupService.SMSPASSWORD_SIMSERIALNUMREG, null);
        paramUserId = loginReg.getmPrefs().getLong(LoginOrSignupService.USERID, -1);
        paramMobileNumComplete = loginReg.getmPrefs().getString(LoginOrSignupService.MOBILENUM_SIMSERIALNUMREG, null);
        String loginJson = loginReg.getmPrefs().getString(LoginOrSignupService.LOGINJSON, null);
        this.loginJson = new Gson().fromJson(loginJson, LoginJson.class);

        // strips away non-numeral characters
        paramMobileNumComplete = StringUtils.stripsNonNumeralCharsFromMobNum(paramMobileNumComplete);
        // extracts only mobileNumState
        mMobileNumState = StringUtils.getSubStringMobileNumState(paramMobileNumComplete);
        // extracts only mobileNum
        mMobileNum = StringUtils.getSubStringMobileNum(paramMobileNumComplete);

        mSmsPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.autent_sms_password
                                || id == EditorInfo.IME_NULL) {
                            attemptSmsAutentPassword();
                            return true;
                        }
                        return false;
                    }
                });

        mAutentPasswordButtonView
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        attemptSmsAutentPassword();
                    }
                });
    }

    public void attemptSmsAutentPassword() {

        if (mSimSerMobNumPasswordAsyncTask != null) {
            return;
        }

        // Reset errors.
        mSmsPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mSmsPassword = mSmsPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mSmsPassword)) {
            mSmsPasswordView.setError(getString(R.string.error_field_required));
            focusView = mSmsPasswordView;
            cancel = true;
        } else if (mSmsPassword.length() < 4) {
            mSmsPasswordView
                    .setError(getString(R.string.error_password_invalid));
            focusView = mSmsPasswordView;
            cancel = true;
        } else if (!TextUtils.equals(mSmsPassword, paramSmsPassword)) {
            mSmsPasswordView
                    .setError(getString(R.string.error_password_incorrect));
            focusView = mSmsPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mSmsPasswordView.setText("");
            focusView.requestFocus();
        } else {

            Boolean conn = CheckConnectivity.checkNow(this);
            if (conn == true) {
                getTelephonyManager();

                // removes all values from the sharedPreferences, once commit is called.
                loginReg.getmEditor().clear();
                loginReg.getmEditor().commit();

                mSimSerMobNumPasswordAsyncTask = new SimSerMobNumPasswordAsyncTask();
                mSimSerMobNumPasswordAsyncTask.execute();
            } else {
                UserMsgService.showDialog(this,
                        R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            }

        }
    }

    public RegUserPostJson getTelephonyManager() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        regUserPostJson = new RegUserPostJson(mTelephonyMgr);
        regUserPostJson.setId(paramUserId);
        regUserPostJson.setMobileNumState(mMobileNumState);
        regUserPostJson.setMobileNum(mMobileNum);
        try {
            regUserPostJson.setAuthAux(UtilsService.loadAuthAux(this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return regUserPostJson;
    }

    public class SimSerMobNumPasswordAsyncTask extends
            AsyncTask<Void, Void, Boolean> {

        boolean glassfishDown = false;
        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    SimSerMobNumPasswordActivity.this, getResources()
                            .getString(R.string.alertdialog_registration_progress_title),
                    getResources().getString(R.string.alertdialog_registration_progress_msg),
                    true, true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {


//                regUserPostJson.setAuthAux(UtilsService.loadAuthAux(activity));
                String gson = new Gson().toJson(regUserPostJson);

                gson = URLEncoder.encode(gson, "UTF-8");

                // open URL, using a Reader to convert bytes to chars
                InputStreamReader reader = HttpService.post((Activity)context, "simSerMobNumReg", gson);
                if (reader == null){
                    glassfishDown = true;
                    return false;
                }
                loginJson =
                        new Gson().fromJson(reader, LoginJson.class);

            } catch (ConnectException exception) {
                glassfishDown = true;
                return false;
            } catch (Exception exception) {
                // in this case, if an exception is thrown, a message used when glassfish is down will be displayed to the user
                glassfishDown = true;
                exception.printStackTrace(); // show exception details
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSimSerMobNumPasswordAsyncTask = null;
            progressDialog.dismiss();

            if (success) {

                Intent it = new Intent(activity, MainActivity.class);
                Bundle params = new Bundle();
                String loginStringJson = new Gson().toJson(SimSerMobNumPasswordActivity.this.loginJson);
                params.putString("loginStringJson", loginStringJson);
                it.putExtras(params);
                startActivity(it);

                SimSerMobNumPasswordActivity.this.loginJson = null;

            } else if (!success) {

                if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(simSerMobNumPasswordActivity, context,
                            R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mSimSerMobNumPasswordAsyncTask = null;
        }

    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // removes all values from the sharedPreferences, once commit is called.
        loginReg.getmEditor().clear();
        loginReg.getmEditor().commit();
        onStop();
    }

}


