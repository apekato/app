package a.apkt;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import a.apkt.service.LoginOrSignupService;
import a.apkt.service.UtilsService;

public class AutentMobileNumActivity extends Activity {

    private String mMobileNumComplete;
    private EditText mMobileNumCompleteView;
    //	private Intent itEmail;
    private String mobNumComplete;
    private AutentMobileNumActivity autentMobileNumActivity = this;
    private LoginOrSignupService loginReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autent_mobile_num);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "AUTENTMOBILENUMACTIVITY");
        loginReg.getmEditor().commit();

        mMobileNumCompleteView = (EditText) findViewById(R.id.autent_mobile_num);

        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        mMobileNumComplete = mTelephonyMgr.getLine1Number();

        InputFilter[] FilterArray = new InputFilter[2];
        FilterArray[0] = new InputFilter.LengthFilter(14); // Limits length of
        // the text
        FilterArray[1] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                boolean lamp = false;
                if (dend == 8 || dend == 3 || dend == 1) {
                    lamp = true;
                }

                if (dstart == 0) {

                    char h;
                    if (lamp == true) {
                        lamp = false;
                        return "";
                    } else if (source != ""){
                        h = source.charAt(0);
                        return "(" + h;
                    }

                } else if (dstart == 2) {

                    char h;
                    if (lamp == true) {
                        lamp = false;
                        return "";
                    } else {
                        h = source.charAt(0);
                        return h + ")";
                    }

                }
                // delete ")" and enter a number
                else if (dend == 3 && dstart == 3) {
                    char h;
                    h = source.charAt(0);
                    return ")" + h;
                }
                else if (dstart == 7) {

                    char h;
                    if (lamp == true) {
                        lamp = false;
                        return "";
                    } else {
                        h = source.charAt(0);
                        return h + "-";
                    }
                }
                // delete "-" and enter a number
                else if (dend == 8 && dstart == 8) {
                    char h;
                    h = source.charAt(0);
                    return "-" + h;
                }

                // 'end !=0' deals with the case when user deletes a character
                if (start == 0 && end != 0) {
                    if (source.charAt(0) == ' '
                            || source.charAt(0) == '/'
                            || source.charAt(0) == '('
                            || source.charAt(0) == ')'
                            || source.charAt(0) == 'N'
                            || source.charAt(0) == ','
                            || source.charAt(0) == '#'
                            || source.charAt(0) == '*'
                            || source.charAt(0) == '+'
                            || source.charAt(0) == '-'
                            || source.charAt(0) == '.'
                            || source.charAt(0) == ';') {
                        return "";
                    }
                }

                return null;
            }
        };

        mMobileNumCompleteView.setFilters(FilterArray);

        findViewById(R.id.autent_mobile_num_button).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                    attemptAutentMobileNum();

                    }
                });

    }

    public void attemptAutentMobileNum() {

        // Reset errors.
        mMobileNumCompleteView.setError(null);

        // Store values at the time of the login attempt.
        mMobileNumComplete = mMobileNumCompleteView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mMobileNumComplete)) {
            mMobileNumCompleteView.setError(getString(R.string.error_field_required));
            focusView = mMobileNumCompleteView;
            cancel = true;
        } else if (mMobileNumComplete.length() < 13) {
            mMobileNumCompleteView.setError(getString(R.string.error_mobilenum_incomplete));
            focusView = mMobileNumCompleteView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            AlertDialog alert = new AlertDialog.Builder(
                    autentMobileNumActivity)
                    .setTitle(R.string.confirmation_cel_num_title)
                    .setMessage(R.string.confirmation_cel_num)
                            // .setView(input)

                    .setPositiveButton(this.getText(R.string.action_confirmar).toString(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog,
                                        int whichButton) {

                                    mobNumComplete = mMobileNumCompleteView
                                            .getText().toString()
                                            .replace("(", "")
                                            .replace(")", "")
                                            .replace("-", "");

                                    String genPassword = UtilsService
                                            .generate();
                                    SmsManager smsManager = SmsManager.getDefault();
                                    String smsmsg = getText(R.string.sms_password) + " " + genPassword;
                                    smsManager.sendTextMessage(mobNumComplete, null, smsmsg, null, null);

                                    loginReg.getmEditor().putString(LoginOrSignupService.USERMOBILENUM, mobNumComplete);
                                    loginReg.getmEditor().putString(LoginOrSignupService.USERSMSPASSWORD, genPassword);
                                    loginReg.getmEditor().commit();

                                    Intent it = new Intent(autentMobileNumActivity, AutentSmsPasswordActivity.class);
                                    startActivity(it);
                                }
                            }
                    )
                    .setNegativeButton(this.getText(R.string.action_cancel).toString(),
                                        new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int whichButton) {
                                                // Do nothing.
                                            }
                                        }
                                ).show();
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

