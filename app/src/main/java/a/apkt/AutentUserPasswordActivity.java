package a.apkt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.codec.digest.DigestUtils;

import a.apkt.service.LoginOrSignupService;
import a.apkt.service.StringUtils;
import a.apkt.service.UtilsService;

public class AutentUserPasswordActivity extends Activity {

    private String mUserPassword;
    private String mUserConfirmPassword;
    private String paramEmail;
    private String paramMobileNumComplete;
    private String paramUserType;
    private EditText mUserPasswordView;
    private EditText mUserConfirmPasswordView;
    private Button mAutentUserPasswordButtonView;
    //	private Intent itParams;
    private LoginOrSignupService loginReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autent_user_password);

        mUserPasswordView = (EditText) findViewById(R.id.user_password);
        mUserConfirmPasswordView = (EditText) findViewById(R.id.user_password_confirm);
        mAutentUserPasswordButtonView = (Button) findViewById(R.id.autent_user_password_button);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "AUTENTUSERPASSWORDACTIVITY");
        loginReg.getmEditor().commit();

//		itParams = getIntent();
//		if (itParams != null) {
//			Bundle params = itParams.getExtras();
//			if (params != null) {
//				paramEmail = params.getString("email");
//				paramMobileNumComplete = params.getString("mobileNumComplete");
//				paramUserType = params.getString("usertype");
//			}
//		}

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

            String dataHexHashString = UtilsService.sha512String(mUserConfirmPassword.getBytes());
            loginReg.getmEditor().putString(LoginOrSignupService.USERPASSWORD, dataHexHashString);
            loginReg.getmEditor().commit();

            Intent it = new Intent(this, AutentUserNameActivity.class);
//			Bundle params = new Bundle();
//			params.putString("password", mUserConfirmPassword);
//			params.putString("mobileNumComplete", paramMobileNumComplete);
//			params.putString("email", paramEmail);
//			params.putString("usertype", paramUserType);
//			it.putExtras(params);
            startActivity(it);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.autent_user_password, menu);*/
        return true;
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

