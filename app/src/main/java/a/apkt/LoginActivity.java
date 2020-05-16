package a.apkt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.spongycastle.util.encoders.Hex;

import java.io.InputStreamReader;
import java.net.ConnectException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import a.apkt.backingbean.AuthAux;
import a.apkt.backingbean.LoginBB;
import a.apkt.cypher.RsaCypher;
import a.apkt.gcm.client.GcmClient;
import a.apkt.json.LoginJson;
import a.apkt.model.AppVersion;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.HttpService;
import a.apkt.service.LoginOrSignupService;
import a.apkt.service.StaticVars;
import a.apkt.service.StringUtils;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;
import a.apkt.sqlite.LoginSqlite;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    private ProgressDialog progressDialog;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginAsyncTask loginAsyncTask = null;

    private String mEmail;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private Spinner mEmailSpinner;
    private Button mRegisterButton;
    private Button mLoginButton;
    private TextView mPasswordForgotTextView;
    private View mProgressView;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private Context context = this;
    private LoginActivity loginActivity = this;
    // creates object and ALSO CREATES DATABASE IF IT DOES NOT EXIST
    private LoginSqlite loginSqlite = new LoginSqlite(this);

    private String selectedEmailAccount = null;
    private List<String> emailAccounts;

    private String gcmRegId = null;
    private GcmClient gcmClient;

    private LoginOrSignupService loginReg;

    private TelephonyManager mTelephonyMgr;
    // simSerialNum value is taken from TelephonyManager
    // when the user attempts to login. It will be used to
    // compare if it's equal to the user's registered simSerialNum
    private String mSimSerialNum;

    private boolean isLogin; //PUBLIC LIST CODE

    private String dataHexHashString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //PUBLIC LIST CODE
        Intent itLogin = getIntent();
        if (itLogin != null) {
            Bundle params = itLogin.getExtras();
            if (params != null) {
                isLogin = params.getBoolean("isLogin");
            }
        }
        mProgressView = findViewById(R.id.activity_progress);
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        mEmailView = (EditText) findViewById(R.id.email);
//        mEmailView.setFocusable(false);
        mEmailSpinner = (Spinner) findViewById(R.id.email_spinner);
        mPasswordView = (EditText) findViewById(R.id.password);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mPasswordForgotTextView = (TextView) findViewById(R.id.password_forgot_text_view);
        TextView mResetPasswordTv = (TextView) findViewById(R.id.password_forgot_text_view);
        mResetPasswordTv.setPaintFlags(mResetPasswordTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        LoginBB loginBB = null;

        loginReg = new LoginOrSignupService(this);

        emailAccounts = UtilsService.getRegisteredEmailAddresses(loginActivity);

        String smsPasswordSimSerialNumReg = loginReg.getmPrefs().getString(LoginOrSignupService.SMSPASSWORD_SIMSERIALNUMREG, null);
        String userSmsPasswordRegistration = loginReg.getmPrefs().getString(LoginOrSignupService.USERSMSPASSWORD, null);
        String activityName = loginReg.getmPrefs().getString(LoginOrSignupService.ACTIVITYNAME, null);


        if (activityName != null) {
            Intent it = null;
            switch (activityName){
                case "AUTENTEMAILACTIVITY":
                    it = new Intent(this, AutentEmailActivity.class);
                    break;
                case "AUTENTMOBILENUMACTIVITY":
                    it = new Intent(this, AutentMobileNumActivity.class);
                    break;
                case "AUTENTSMSPASSWORDACTIVITY":
                    it = new Intent(this, AutentSmsPasswordActivity.class);
                    break;
                case "AUTENTUSERNAMEACTIVITY":
                    it = new Intent(this, AutentUserNameActivity.class);
                    break;
                case "AUTENTUSERPASSWORDACTIVITY":
                    it = new Intent(this, AutentUserPasswordActivity.class);
                    break;
                case "SIMSERMOBNUMPASSWORDACTIVITY":
                    it = new Intent(this, SimSerMobNumPasswordActivity.class);
                    break;
                case "SIMSERMOBNUMREGACTIVITY":
                    it = new Intent(this, SimSerMobNumRegActivity.class);
                    break;
                case "PASSWORDFORGOTACTIVITY":
                    it = new Intent(this, PasswordForgotActivity.class);
                    break;
                case "PASSWORDCODEACTIVITY":
                    it = new Intent(this, PasswordCodeActivity.class);
                    break;
                case "PASSWORDRESETACTIVITY":
                    it = new Intent(this, PasswordResetActivity .class);
                    break;
            }
            startActivity(it);
        }else if (loginSqlite.getUserLoginCount(loginSqlite) == 0) {

            if (isLogin){

                loginReg.getmEditor().clear();
                loginReg.getmEditor().commit();

                // To revert emulator code search for keyword EMULATORCODE: uncomment code below and comment code below it
//                if (emailAccounts == null || emailAccounts.size() == 0) {
//                    mEmailSpinner.setVisibility(View.GONE);
//                    mEmailView.setEnabled(false);
//                    mPasswordView.setEnabled(false);
//                    mRegisterButton.setEnabled(false);
//                    mLoginButton.setEnabled(false);
//                    mPasswordForgotTextView.setEnabled(false);
//                    UserMsgService.showDialog(context,
//                            R.string.error_email_nonexistent_title,
//                            R.string.error_email_nonexistent);
//                }
//                if (emailAccounts == null || emailAccounts.size() == 0) {
//                    mEmailSpinner.setVisibility(View.GONE);
//                    mEmailView.setEnabled(true);
//                    mPasswordView.setEnabled(true);
//                    mRegisterButton.setEnabled(true);
//                    mLoginButton.setEnabled(true);
//                    mPasswordForgotTextView.setEnabled(true);
////                    UserMsgService.showDialog(context,
////                            R.string.error_email_nonexistent_title,
////                            R.string.error_email_nonexistent);
//                }
//                else if (emailAccounts.size() == 1) {
//                    mEmailSpinner.setVisibility(View.GONE);
//
//                    selectedEmailAccount = emailAccounts.get(0);
//
//                    mEmailView.setText(selectedEmailAccount);
//
//                } else if (emailAccounts.size() >= 2) {
//
//                    mEmailView.setVisibility(View.GONE);
//
//                    // Create an ArrayAdapter using the string array and a default
//                    // spinner layout
//                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                            context, R.layout.spinner_text_black_color,
//                            emailAccounts);
//                    // Specify the layout to use when the list of choices appears
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    // Apply the adapter to the spinner
//                    mEmailSpinner.setAdapter(adapter);
//                    adapter = null;
//                    mEmailSpinner
//                            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                                @Override
//                                public void onItemSelected(AdapterView<?> arg0,
//                                                           View arg1, int positon, long arg3) {
//                                    selectedEmailAccount = emailAccounts
//                                            .get(positon);
//                                }
//
//                                @Override
//                                public void onNothingSelected(AdapterView<?> arg0) {
//                                    // TODO Auto-generated method stub
//                                }
//                            });
//                }

                findViewById(R.id.login_button).setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });

                findViewById(R.id.register_button).setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                loginReg.getmEditor().putString(LoginOrSignupService.USEREMAIL, selectedEmailAccount);
                                loginReg.getmEditor().commit();
                                Intent it = new Intent(loginActivity, AutentEmailActivity.class);
                                startActivity(it);
                            }
                        });

                findViewById(R.id.password_forgot_text_view).setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                loginReg.getmEditor().putString(LoginOrSignupService.USEREMAIL, selectedEmailAccount);
                                loginReg.getmEditor().commit();
                                Intent it = new Intent(loginActivity, PasswordForgotActivity.class);
                                startActivityForResult(it, StaticVars.PASSWORD_FORGOT_ACTIVITY_RESULT);
                            }
                        });
            } else {
                Boolean conn = CheckConnectivity.checkNow(this);
                if (conn == true) {
                    AppVersionCheckAsyncTask appVersionCheckAsyncTask = new AppVersionCheckAsyncTask();
                    appVersionCheckAsyncTask.execute(gcmRegId);
                } else {
                    UserMsgService.showDialogFinish(this, R.string.alertdialog_no_connectivity_title,
                            R.string.alertdialog_no_connectivity);
                }
            }

        } else {

            boolean registeredGmail = false;

            loginBB = loginSqlite.getUserLogin();

            // get email registered in sqlite
            mEmail = loginBB.getLoginAux().getEmail();
            dataHexHashString = loginBB.getLoginAux().getPassWord();

            mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//            if (ProjService.PROJMODE.equals(ProjService.Mode.HOMOLOG)){
//                mPasswordView.setText(ProjService.homologPassword);
//                if (ProjService.homologEmail.equals(mEmail)){
//                    mSimSerialNum = ProjService.homologSimSerialNum;
//                }else{
//                    mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//                }
//            }else{
//                mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//            }

            // compare email registered in sqlite with a gmail account
            // registered in mobilephone
            for (String account : emailAccounts) {
                if (account.equals(mEmail)) {
                    registeredGmail = true;
                }
            }

            // if registered gmail account matches registered mobilephone gmail
            // account, login will be verified
            // To revert emulator code search for keyword EMULATORCODE: uncomment code "registeredGmail" and comment code "true"
//            if (registeredGmail) {
            if (true) {
                Boolean conn = CheckConnectivity.checkNow(this);
                if (conn == true) {
                    loginAsyncTask = new LoginAsyncTask();
                    gcmClient = new GcmClient(context, this);
                    gcmRegId = gcmClient.checkOrRegisterId();

                    // there are two ways RegNewUserAsyncTask can executed:
                    // 1. bellow, in case gcmRegId was already registered
                    // 2. through gcmClient.checkOrRegisterId(), if gcmRegId was not registered
                    if (gcmRegId != null) {
                        loginAsyncTask.execute(gcmRegId);
                    }
                } else {
                    UserMsgService.showDialogFinish(this,
                            R.string.alertdialog_no_connectivity_title,
                            R.string.alertdialog_no_connectivity);
                }
            } else {

                StringBuilder sb = new StringBuilder();

                sb.append(context
                        .getString(R.string.alertdialog_error_mobile_gmail_not_registered_msg_1));
                sb.append("       ");
                sb.append(mEmail);
                sb.append(context
                        .getString(R.string.alertdialog_error_mobile_gmail_not_registered_msg_2));
                UserMsgService
                        .showDialogFinish(
                                this,
                                context.getString(R.string.alertdialog_error_mobile_gmail_not_registered_title),
                                sb.toString());
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        /*if (loginAsyncTask != null) {
            return;
        }*/

        // Reset errors.
        mPasswordView.setError(null);
        boolean cancel = false;
        View focusView = null;

        // Store values at the time of the login attempt.
        if (!mEmailView.getText().toString().equals("")) { // there is only one
            // email registered
            // in phone
            mEmail = mEmailView.getText().toString();
        } else { // more than one email registered on phone
            mEmail = selectedEmailAccount;
        }


        mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        if (ProjService.PROJMODE.equals(ProjService.Mode.HOMOLOG)){
//            mPasswordView.setText(ProjService.homologPassword);
//            if (ProjService.homologEmail.equals(mEmail)){
//                mSimSerialNum = ProjService.homologSimSerialNum;
//            }else{
//                mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//            }
//        }else{
//            mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//        }


        if (!mPasswordView.getText().toString().equals("")) {
            String mPassword = mPasswordView.getText().toString();
            dataHexHashString = UtilsService.sha512String(mPassword.getBytes());
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(mPasswordView.getText().toString())) {
            mPasswordView
                    .setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mPasswordView.setText("");
            focusView.requestFocus();
        } else {
            Boolean conn = CheckConnectivity.checkNow(this);
            if (conn == true) {
                loginAsyncTask = new LoginAsyncTask();
                gcmClient = new GcmClient(context, this);
                gcmRegId = gcmClient.checkOrRegisterId();

                // there are two ways RegNewUserAsyncTask can executed:
                // 1. bellow, in case gcmRegId was already registered
                // 2. through gcmClient.checkOrRegisterId(), if gcmRegId was not registered
                if (gcmRegId != null) {
                    loginAsyncTask.execute(gcmRegId);
                }
            } else {
                UserMsgService.showDialogFinish(this, R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            }
        }
    }

    public class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {

        private LoginJson loginJson;
        private String loginStringJson;
        private boolean glassfishDown = false;
        private boolean userNonexistent = false;
        private String mSimOperator;
        private String mNetworkCountryIso;
        private String mSubscriberId;


        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(
                    loginActivity,
                    loginActivity.getResources().getText(
                            R.string.progress_login_title),
                    loginActivity.getResources().getText(
                            R.string.progress_login), true, true);
            progressDialog.setCanceledOnTouchOutside(false);
//			progressDialog.setCancelable(false);
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... gcmRegId) {

            try {
                mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
                mSimOperator = mTelephonyMgr.getSimOperator();
                // data below is not available when there is no cell operator service
                mNetworkCountryIso = mTelephonyMgr.getNetworkCountryIso();
                mSubscriberId = mTelephonyMgr.getSubscriberId();

                byte[] encrypted = RsaCypher.encryptData(loginActivity, dataHexHashString);
                byte[] encoded = Base64.encodeBase64(encrypted);

                AuthAux authAux = new AuthAux(
                        null,
                        null,
                        mSimSerialNum,
                        mSimOperator,
                        mNetworkCountryIso,
                        mSubscriberId,
                        gcmRegId[0],
                        StringUtils.getLanguage(context),
                        StaticVars.APP_NAME);
                authAux.setPasswordB(encoded);
                authAux.setEmailB(RsaCypher.encryptData(loginActivity, mEmail));

//                String gson = RsaCypher.packData(loginActivity, authAux);

                String gson = new Gson().toJson(authAux);

                // open URL, using a Reader to convert bytes to chars
                InputStreamReader reader = HttpService.post((Activity) context, "login", gson);
                if (reader == null){
                    glassfishDown = true;
                    return false;
                }
                // parse the JSON back into a TextMessage
                loginJson = new Gson().fromJson(reader,
                        LoginJson.class);

                if (!loginJson.getUserNonexistent()) {
                    // Account exists, return true.
                    return true;

                } else if (loginJson.getUserNonexistent()) {
                    userNonexistent = true;
                    return false;
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
            loginAsyncTask = null;

            progressDialog.dismiss();

            if (success) {
                if (loginJson.getAppVersion() != null
                        &&!loginJson.getAppVersion().equals(BuildConfig.VERSION_NAME)
                        && loginJson.isVerifyVersion()){
                    UserMsgService.updateApp(loginActivity);
                }

                // To revert emulator code search for keyword EMULATORCODE: uncomment if code
//               else if (
//                        loginJson.getSimSerialNumber() == null
//                        || !loginJson.getSimSerialNumber().equals(mSimSerialNum)
////                        && !loginJson.getSimOperator().equals(mTelephonyMgr.getSimOperator())
//                        // data below is not available when there is no cell operator service
////                        && !loginJson.getNetworkCountryIso().equals(mTelephonyMgr.getNetworkCountryIso())
////                        && !loginJson.getSubscriberId().equals(mTelephonyMgr.getSubscriberId())
//                ) {
//
//                    // add user login info into sqlite
//                    boolean added = LoginOrSignupService.sqliteAddUserLogin(
//                            loginSqlite,
//                            loginJson.getId(),
//                            loginJson.getUsername(),
//                            mEmail,
//                            dataHexHashString);
//
//                    String hasSmsPassword = loginReg.getmPrefs().getString(LoginOrSignupService.SMSPASSWORD_SIMSERIALNUMREG, null);
//
//                    loginStringJson = new Gson().toJson(loginJson);
//                    loginReg.getmEditor().putLong(LoginOrSignupService.USERID, loginJson.getId());
//                    loginReg.getmEditor().putString(LoginOrSignupService.LOGINJSON, loginStringJson);
//                    loginReg.getmEditor().commit();
//
//                    if (hasSmsPassword == null) {
//                        Intent it = new Intent(loginActivity, SimSerMobNumRegActivity.class);
//                        startActivity(it);
//                    } else {
//                        Intent it = new Intent(loginActivity, SimSerMobNumPasswordActivity.class);
//                        startActivity(it);
//                    }
//
//                }
                else {

                    // add user login info into sqlite
                    boolean added = LoginOrSignupService.sqliteAddUserLogin(
                            loginSqlite,
                            loginJson.getId(),
                            loginJson.getUsername(),
                            mEmail,
                            dataHexHashString);

                    Intent it = new Intent(loginActivity, MainActivity.class);
                    Bundle params = new Bundle();
                    String loginStringJson = new Gson().toJson(this.loginJson);
                    params.putString("loginStringJson", loginStringJson);
                    it.putExtras(params);
                    startActivity(it);

                    this.loginJson = null;
                }
            } else if (!success) {
                if (userNonexistent) {

                    LoginSqlite loginSqlite = new LoginSqlite(context);
                    loginSqlite.deleteUserLogin();

                    // for every login or new user registration attempt,
                    // an id will be registered in the app's shared preferences.
                    gcmClient.unregisterId();

                    UserMsgService.showDialog(loginActivity,
                            R.string.error_email_invalid_title,
                            R.string.error_email_invalid);
                } else if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(loginActivity,
                            context, R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);
                }
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            loginAsyncTask = null;
            // showProgress(false);
        }
    }

    public LoginAsyncTask getLoginAsyncTask() {
        return loginAsyncTask;
    }

    public class AppVersionCheckAsyncTask extends AsyncTask<String, Void, Boolean> {
        private boolean glassfishDown = false;
        private AppVersion appVersionRes = null;

        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            InputStreamReader reader = null;
            try {
                AppVersion appVersion = new AppVersion();
                appVersion.setAppName(StaticVars.APP_NAME);
                String gson = new Gson().toJson(appVersion);
                reader = HttpService.post((Activity) context, "appVersionCheck", gson);

                if (reader == null){
                    glassfishDown = true;
                    return false;
                }

                // parse the JSON back into a TextMessage
                appVersionRes = new Gson().fromJson(reader,
                        AppVersion.class);
            } catch (ConnectException exception) {
                glassfishDown = true;
                return false;
            } catch (Exception e) {
                e.getMessage();
                glassfishDown = true;
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                //String a = appVersionRes.getAppVersion();
                //if (appVersionRes != null
                //        && appVersionRes.getAppVersion().equals(BuildConfig.VERSION_NAME)
                //        && appVersionRes.isVerifyVersion()){
                    Intent it = new Intent(loginActivity, MainActivity.class);
                    startActivity(it);
                //} else {
                //    UserMsgService.updateApp(loginActivity);
                //}
            } else if (!success) {
                if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(loginActivity,
                            context, R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);
                }
            }

            showProgress(false);
        }
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
        linearLayout.setBackgroundColor(show ? getResources().getColor(R.color.white) : getResources().getColor(R.color.green_8bc34a));
        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(loginActivity, MainActivity.class);
        startActivity(it);
    }
}

