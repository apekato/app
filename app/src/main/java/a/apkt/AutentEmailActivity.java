package a.apkt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import a.apkt.service.LoginOrSignupService;
import a.apkt.service.UtilsService;

public class AutentEmailActivity extends Activity {

    private Button mEmailButton;
    private String selectedEmailAccount = null;
    private List<String> emailAccounts;
    private AutentEmailActivity autentEmailActivity = this;
    private LoginOrSignupService loginReg;
    private EditText mEmailView;
    private Spinner mEmailSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autent_email);

        loginReg = new LoginOrSignupService(this);
        loginReg.getmEditor().putString(LoginOrSignupService.ACTIVITYNAME, "AUTENTEMAILACTIVITY");
        loginReg.getmEditor().commit();

        mEmailView = (EditText) findViewById(R.id.editText1);
        mEmailSpinner = (Spinner) findViewById(R.id.spinner1);

        mEmailButton = (Button) findViewById(R.id.autent_email_button);

        emailAccounts = UtilsService.getRegisteredEmailAddresses(autentEmailActivity);

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

            mEmailSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
//						 mEmailView.setText(emailAccounts.get(arg2));
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

                loginReg.getmEditor().putString(LoginOrSignupService.USEREMAIL, selectedEmailAccount);
                loginReg.getmEditor().commit();
                Intent it = new Intent(autentEmailActivity, AutentMobileNumActivity.class);//
                startActivity(it);
            }
        });
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
