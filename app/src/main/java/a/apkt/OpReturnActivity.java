package a.apkt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import a.apkt.asynctask.OpReturnAsyncTask;
import a.apkt.json.LoginJson;
import a.apkt.model.OpReturn.OpReturnType;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.StaticVars;
import a.apkt.service.UserMsgService;

public class OpReturnActivity extends AppCompatActivity {

    private TextView tvCharacterCounter;
    private String curmsg = "";
    private static final Integer MAX_LENGTH = 80;
    private EditText etMessage;
    private MenuItem actionSendItem;
    private Activity activity = this;
    private View mProgressView;
    private ScrollView scrollView;
    private LoginJson loginJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_return);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressView = activity.findViewById(R.id.activity_progress);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));

        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            String loginStringJson = null;
            if (params != null) {
                loginStringJson = params.getString("loginStringJson");
            }
            loginJson = new Gson().fromJson(loginStringJson, LoginJson.class);
        }

        setTextViews();
    }

    public void setTextViews() {
        tvCharacterCounter = (TextView) findViewById(R.id.tvCharacterCounter);
        etMessage = (EditText) findViewById(R.id.et_message);
        curmsg = etMessage.getText().toString();
        etMessage.addTextChangedListener(new TextWatcher() {

            String oldText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = curmsg;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateTextViews();
            }

            @Override
            public void afterTextChanged(Editable s) {
                curmsg = s.toString();
                if (calcRemainingBytes() < 0) {
                    curmsg = oldText;
                    etMessage.setText(curmsg);
                    etMessage.setSelection(curmsg.length());
                }
                tvCharacterCounter.setText(calcRemainingBytes() + " " + activity.getResources().getText(R.string.tv_characters_available));
            }
        });
    }

    private Integer calcRemainingBytes() {
        try {
            int length = curmsg.getBytes("UTF-8").length;
            return MAX_LENGTH-length;
        }
        catch (Exception ex) {
            return MAX_LENGTH;
        }
    }

    public void validateTextViews() {
        if (etMessage.getText().toString().matches("")) {
            actionSendItem.setEnabled(false);
        } else {
            actionSendItem.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_op_return, menu);
        actionSendItem = menu.findItem(R.id.action_send);
        actionSendItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_send) {

            Boolean conn = CheckConnectivity.checkNow(this);
            if (conn == true) {

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("bitcoin:17uPJEkDU3WtQp83oDuiQbnMnneA3wrtrd"));

                if (isAvailable(i)) {
                    OpReturnAsyncTask opReturnAsyncTask = new OpReturnAsyncTask(
                            mProgressView,
                            scrollView,
                            activity,
                            loginJson,
                            curmsg,
                            loginJson.getEmail(), // email
                            OpReturnType.OP_RETURN_TYPE_TEXT
                    );
                    opReturnAsyncTask.execute();
                } else {
                    AlertDialog d = new AlertDialog.Builder(activity)
                            .setTitle(getString(R.string.alertdialog_bitcoin_wallet_title))
                            .setMessage(R.string.alertdialog_bitcoin_wallet_msg)
                            .setNegativeButton(R.string.button_title_no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(R.string.button_title_yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://bitcoin.org/en/choose-your-wallet")));
                                }
                            })
                            .create();
                    d.show();
                }

            }else {
                UserMsgService.showDialog(this, R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

//    public class OpReturnAsyncTask extends AsyncTask<String, Void, Boolean> {
//
//        private boolean glassfishDown = false;
//        private OpReturn opReturnRes;
//
//        protected void onPreExecute() {
//            showProgress(true);
//        }
//
//        @Override
//        protected Boolean doInBackground(String... gcmRegId) {
//
//            try {
//                Long loginId = null;
//                if (loginJson != null){
//                    loginId = loginJson.getId();
//                }
//                OpReturn opReturn = new OpReturn(curmsg, OpReturnType.OP_RETURN_TYPE_TEXT, loginId);
//                OpReturnJson opReturnJson = new OpReturnJson(opReturn, UtilsService.loadAuthAux(activity));
//
//                String gson = new Gson().toJson(opReturnJson);
//
//                // open URL, using a Reader to convert bytes to chars
//                InputStreamReader reader = HttpService.post(activity, "opReturnRequest", gson);
//
//                // parse the JSON back into a TextMessage
//                opReturnRes = new Gson().fromJson(reader, OpReturn.class);
//
//                if (opReturnRes != null) {
//                    return true;
//                }
//
//            } catch (ConnectException exception) {
//
//                glassfishDown = true;
//                return false;
//
//            } catch (Exception e) {
//                e.getMessage();
//                glassfishDown = true;
//                return false;
//            }
//
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            showProgress(false);
//
//            if (success) {
//                final String uriString = "bitcoin:" + opReturnRes.getAddress() + "?amount=" + opReturnRes.getFee()/*+"&message=Payment&label=Satoshi&extra=other-param"*/;
//                final Uri uri = Uri.parse(uriString);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivityForResult(Intent.createChooser(intent, getString(R.string.ask_choose_wallet)), StaticVars.SELECT_BITCOIN_WALLET_ACTIVITY_RESULT);
//            }
//            else if (!success) {
//                if (glassfishDown) {
//                    UserMsgService.showDialogPositButtonMail(activity,
//                            activity, R.string.alertdialog_internal_problem_title,
//                            R.string.alertdialog_internal_problem_msg);
//                }
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StaticVars.SELECT_BITCOIN_WALLET_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK // RESULT_CANCELED works with testnet wallet on testnet network
                    || resultCode == RESULT_CANCELED // RESULT_CANCELED works with AirBitz wallet and other wallets on main network
                    ) {

                Intent it = new Intent(activity, OpReturnRqstdActivity.class);
                Bundle args = new Bundle();
                args.putString("titleSection", getResources().getText(R.string.title_section_wall).toString());
                it.putExtras(args);
                activity.startActivity(it);
                Intent returnIntent = new Intent();
                activity.setResult(activity.RESULT_OK, returnIntent);
                activity.finish();
                finish();
            }
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
}
