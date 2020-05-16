package a.apkt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLEncoder;

import a.apkt.cypher.RsaCypher;
import a.apkt.gcm.client.GcmClient;
import a.apkt.json.LoginJson;
import a.apkt.json.RegUserPostJson;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.HttpService;
import a.apkt.service.LoginOrSignupService;
import a.apkt.service.StringUtils;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;
import a.apkt.sqlite.LoginSqlite;

public class AutentUserNameActivity extends Activity {

    private String mUserName;
    private EditText mUserNameView;
    private String dataHexHashString;
    private String paramEmail;
    private String paramMobileNumComplete;
    private String paramUserType;
    private Intent itParams;
    private RegUserPostJson regUserPostJson;
    //	private View mRegisterFormView;
//	private View mRegisterStatusView;
//	private TextView mRegisterStatusMessageView;
    private RegNewUserAsyncTask regNewUserAsyncTask = null;
    private Context context = this;
    private AutentUserNameActivity autentUserNameActivity = this;
    private LoginSqlite loginSqlite = new LoginSqlite(this);
    private String gcmRegId = null;
    private GcmClient gcmClient;
    private LoginOrSignupService loginReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autent_user_name);

//		mRegisterFormView = findViewById(R.id.autent_user_name_form);
//		mRegisterStatusView = findViewById(R.id.register_status);
//		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        regUserPostJson = new RegUserPostJson(mTelephonyMgr);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "AUTENTUSERNAMEACTIVITY");
        loginReg.getmEditor().commit();
        dataHexHashString = loginReg.getmPrefs().getString(LoginOrSignupService.USERPASSWORD, null);
        paramEmail = loginReg.getmPrefs().getString(LoginOrSignupService.USEREMAIL, null);
        paramMobileNumComplete = loginReg.getmPrefs().getString(LoginOrSignupService.USERMOBILENUM, null);

//        regUserPostJson = UtilsService.getHomologSimSerialNum(paramEmail, regUserPostJson);

//		itParams = getIntent();
//		if (itParams != null) {
//			Bundle params = itParams.getExtras();
//			if (params != null) {
//				paramPassword = params.getString("password");
//				paramEmail = params.getString("email");
//				paramMobileNumComplete = params.getString("mobileNumComplete");
//				paramUserType = params.getString("usertype");
//			}
//		}

        // strips away non-numeral characters
        paramMobileNumComplete =
                StringUtils.stripsNonNumeralCharsFromMobNum(paramMobileNumComplete);

        regUserPostJson.
                setMobileNumState(StringUtils.getSubStringMobileNumState(paramMobileNumComplete));
        regUserPostJson.
                setMobileNum(StringUtils.getSubStringMobileNum(paramMobileNumComplete));

        mUserNameView = (EditText) findViewById(R.id.autent_user_name);
        // mascara para mUsernameView
        InputFilter[] FilterArrayUsername = new InputFilter[2];
        FilterArrayUsername[0] = new InputFilter.LengthFilter(15); // Limit

        FilterArrayUsername[1] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                CharSequence cs = source;
                // 'end !=0' deals with the case when user deletes character
//                if (start == 0 && end != 0) {

                    // convert whitespace in first character in string to empty character
//                    if (dstart == 0 &&  cs.charAt(0) == ' '){
//                        cs = "";
//                    }
                    // convert first character to uppercase
//                    if (dstart == 0){
//                        String s = cs.toString();
//                        cs = s.toUpperCase();
//                    }
                    // convert character to uppercase after whitespace
//                    if (dstart > 0 && dest.charAt(dend - 1) == ' '){
//                        String s = cs.toString();
//                        cs = s.toUpperCase();
//                    }
                    // convert double whitespaces into a whitespace
//                    if (dstart > 0 && dest.charAt(dend - 1) == ' ' && cs.charAt(0) == ' '){
//                        cs = "";
//                    }

//                    if (source.charAt(0) == ' ' || source.charAt(0) == '\\'
//                            || source.charAt(0) == '|'
//                            || source.charAt(0) == '^') {
//                        return "";
//                    }
//                }
                return cs;
            }
        };
        mUserNameView.setFilters(FilterArrayUsername);

        findViewById(R.id.autent_user_name_button).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        attemptAutentUserName();
                    }
                });
    }

    public void attemptAutentUserName() {

        if (regNewUserAsyncTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);

        // Store values at the time of the registration attempt.
        mUserName = mUserNameView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mUserName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            regUserPostJson.setUsername(mUserName);

            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
//			mRegisterStatusMessageView
//					.setText(R.string.cadastrar_progress_cadastrando);
//			showProgress(true);

            Boolean conn = CheckConnectivity.checkNow(this);
            if (conn == true) {

                // removes all values from the sharedPreferences, once commit is called.
                loginReg.getmEditor().clear();
                loginReg.getmEditor().commit();

                regNewUserAsyncTask = new RegNewUserAsyncTask();
                gcmClient = new GcmClient(context, autentUserNameActivity);
                gcmRegId = gcmClient.checkOrRegisterId();

                // there are two ways RegNewUserAsyncTask can executed:
                // 1. bellow, in case gcmRegId was already registered
                // 2. through gcmClient.checkOrRegisterId(), if gcmRegId was not registered
                if (gcmRegId != null) {
                    regNewUserAsyncTask.execute(gcmRegId);
                }
            } else {
                UserMsgService.showDialog(this,
                        R.string.no_connectivity_title,
                        R.string.no_connectivity);
            }
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegNewUserAsyncTask extends AsyncTask<String, Void, Boolean> {

        boolean glassfishDown = false;
        private ProgressDialog progressDialog;
        private LoginJson loginJson;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(AutentUserNameActivity.this,
                    getResources().getString(R.string.alertdialog_registration_progress_title),
                    getResources().getString(R.string.alertdialog_registration_progress_msg),
                    true, true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... gcmRegId) {

            try {

                regUserPostJson.setGcmRegId(gcmRegId[0]);
                regUserPostJson.setCurrencyCode(StringUtils.getCurrencyCode());
                regUserPostJson.setLang(StringUtils.getLanguage(context));
                regUserPostJson.setEmailB(RsaCypher.encryptData(autentUserNameActivity, paramEmail));
                byte[] encrypted = RsaCypher.encryptData(autentUserNameActivity, dataHexHashString);
                byte[] encoded = Base64.encodeBase64(encrypted);
                regUserPostJson.setPasswordB(encoded);

                String gson = new Gson().toJson(regUserPostJson);

                gson = URLEncoder.encode(gson, "UTF-8");

                InputStreamReader reader = HttpService.post((Activity) context, "regUser", gson);
                if (reader == null){
                    glassfishDown = true;
                    return false;
                }
                // parse the JSON back into a TextMessage
                loginJson = new Gson().fromJson(reader,
                        LoginJson.class);

                // Code is used to verify SimSerialNumber or Email or Username is alredy registered
                // Affected classes:
                // AutentUserNameActivity (ANDROID), CadastrarMobileInfoWs (SERVER), AuthentUniqueVarsWS (SERVER), AuthentUniqueVarsDaoJpa (SERVER), LoginBB (SERVER), AuthentUniqueVarsDaoJpaTest (SERVER).
                if (loginJson.getUsernameExists() != null
                        || loginJson.getEmailExists() != null
                        || loginJson.getSimSerialNumExists() != null) {
                    return false;
                }

            } catch (ConnectException exception) {
                glassfishDown = true;
                return false;
            } catch (Exception exception) {
                exception.printStackTrace(); // show exception details
                return false;
            }

            return true;
        }

        //		@Override
        protected void onPostExecute(final Boolean success) {
            regNewUserAsyncTask = null;
//			showProgress(false);
            progressDialog.dismiss();

            if (success) {

                // if sqlite is empty, it will add user login info
                boolean added = LoginOrSignupService.sqliteAddUserLogin(
                        loginSqlite,
                        loginJson.getId(),
                        loginJson.getUsername(),
                        paramEmail,
                        dataHexHashString);
                if (loginSqlite.getUserLoginCount(loginSqlite) > 0) {

                    loginSqlite.deleteUserLogin();

                    // if sqlite is empty, it will add user login info
                    added = LoginOrSignupService.sqliteAddUserLogin(
                            loginSqlite,
                            loginJson.getId(),
                            loginJson.getUsername(),
                            paramEmail,
                            dataHexHashString);
                }

                String loginStringJson = new Gson().toJson(this.loginJson);
                Intent it = new Intent(autentUserNameActivity, MainActivity.class);
                Bundle params = new Bundle();
                params.putString("loginStringJson", loginStringJson);
                it.putExtras(params);
                startActivity(it);

            } else if (!success) {

                if (glassfishDown) {
                    UserMsgService.showDialogPositButtonMail(autentUserNameActivity, context,
                            R.string.alertdialog_internal_problem_title,
                            R.string.alertdialog_internal_problem_msg);


                }
                // Code is used to verify SimSerialNumber or Email or Username is alredy registered
                // Affected classes:
                // AutentUserNameActivity (ANDROID), CadastrarMobileInfoWs (SERVER), AuthentUniqueVarsWS (SERVER), AuthentUniqueVarsDaoJpa (SERVER), LoginBB (SERVER), AuthentUniqueVarsDaoJpaTest (SERVER).
                else if (loginJson != null) {

                    if (loginJson.getUsernameExists() != null
                            || loginJson.getEmailExists() != null
                            || loginJson.getSimSerialNumExists() != null) {

                        // for every login or new user registration attempt,
                        // an id will be registered in the app's shared preferences.
                        gcmClient.unregisterId();

                        // code block below to be used for identifying which
                        // unique var has already been registered.
                        StringBuilder sb = new StringBuilder();

                        if (loginJson.getSimSerialNumExists() != null) {
                            sb.append(context.getString(R.string.alertdialog_error_duplicated_simserialnum_msg));
                        }

                        if (loginJson.getEmailExists() != null) {
                            sb.append(context.getString(R.string.alertdialog_error_duplicated_email_msg));
                        }

                        if (loginJson.getUsernameExists() != null) {
                            sb.append(context.getString(R.string.alertdialog_error_duplicated_username_msg));
                        }

                        UserMsgService.showDialogFinish(autentUserNameActivity,
                                context.getString(R.string.alertdialog_error_duplicated_simserialnum_email_username_title),
                                sb.toString());
                    }
                } else {
                    UserMsgService.showDialogPositButtonMailUserReg(autentUserNameActivity,
                            context, R.string.alertdialog_user_registration_problem_title,
                            R.string.alertdialog_user_registration_problem_msg);
                }
            }
        }

        @Override
        protected void onCancelled() {
            regNewUserAsyncTask = null;
//			showProgress(false);
        }
    }

    public RegNewUserAsyncTask getRegNewUserAsyncTask() {
        return regNewUserAsyncTask;
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

    /**
     * Shows the progress UI and hides the login form.
     */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//	private void showProgress(final boolean show) {
//		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//		// for very easy animations. If available, use these APIs to fade-in
//		// the progress spinner.
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//			int shortAnimTime = getResources().getInteger(
//					android.R.integer.config_shortAnimTime);
//
//			mRegisterStatusView.setVisibility(View.VISIBLE);
//			mRegisterStatusView.animate().setDuration(shortAnimTime)
//					.alpha(show ? 1 : 0)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mRegisterStatusView.setVisibility(show ? View.VISIBLE
//									: View.GONE);
//						}
//					});
//
//			mRegisterFormView.setVisibility(View.VISIBLE);
//			mRegisterFormView.animate().setDuration(shortAnimTime)
//					.alpha(show ? 0 : 1)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mRegisterFormView.setVisibility(show ? View.GONE
//									: View.VISIBLE);
//						}
//					});
//		} else {
//			// The ViewPropertyAnimator APIs are not available, so simply show
//			// and hide the relevant UI components.
//			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
//			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//		}
//	}

}
