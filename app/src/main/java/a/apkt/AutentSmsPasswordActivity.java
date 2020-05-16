package a.apkt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import a.apkt.service.LoginOrSignupService;

public class AutentSmsPasswordActivity extends Activity {

    private String mSmsPassword;
    //	private String mUserPassword;
//	private String mUserConfirmPassword;
    private String genPassword;
    //	private String paramEmail;
//	private String paramMobileNumComplete;
//	private String paramUserType;
    private EditText mSmsPasswordView;
    private Button mAutentPasswordButtonView;
    private EditText mUserPasswordView;
    private EditText mUserConfirmPasswordView;
    private Button mAutentUserPasswordButtonView;
    //	private Intent itParams;
    private LoginOrSignupService loginReg;
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autent_sms_password);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "AUTENTSMSPASSWORDACTIVITY");
        loginReg.getmEditor().commit();
        genPassword = loginReg.getmPrefs().getString(LoginOrSignupService.USERSMSPASSWORD, null);
        String mobNumComplete = loginReg.getmPrefs().getString(LoginOrSignupService.USERMOBILENUM, null);

//        genPassword = "qqqq";
//        genPassword = PasswordGenerator
//                .generate();
//        SmsManager smsManager = SmsManager.getDefault();
//        String smsmsg = getText(R.string.sms_password) + " " + genPassword;
//        smsManager.sendTextMessage(mobNumComplete, null, smsmsg, null, null);


        mSmsPasswordView = (EditText) findViewById(R.id.autent_sms_password);

        //passwords for test: delete when not used
//		mSmsPasswordView.setText("test");

        mAutentPasswordButtonView = (Button) findViewById(R.id.autent_password_button);

        mUserPasswordView = (EditText) findViewById(R.id.user_password);
        mUserPasswordView.setVisibility(View.GONE);

        mUserConfirmPasswordView = (EditText) findViewById(R.id.user_password_confirm);
        mUserConfirmPasswordView.setVisibility(View.GONE);

        mAutentUserPasswordButtonView = (Button) findViewById(R.id.autent_user_password_button);
        mAutentUserPasswordButtonView.setVisibility(View.GONE);

        // gets parameters from AutentMobileNumActivity
//		itParams = getIntent();
//		if (itParams != null) {
//			Bundle params = itParams.getExtras();
//			if (params != null) {
//				paramSmsPassword = params.getString("password");
//				paramEmail = params.getString("email");
//				paramMobileNumComplete = params.getString("mobileNumComplete");
//				paramUserType = params.getString("usertype");
//			}
//		}

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
        } else if (!TextUtils.equals(mSmsPassword, genPassword)) {
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

            Intent it = new Intent(activity, AutentUserPasswordActivity.class);
//			Bundle params = new Bundle();
//			params.putString("mobileNumComplete", paramMobileNumComplete);
//			params.putString("email", paramEmail);
//			params.putString("usertype", paramUserType);
//			it.putExtras(params);
            startActivity(it);
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
