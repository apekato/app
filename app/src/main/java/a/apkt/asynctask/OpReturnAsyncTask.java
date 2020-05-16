package a.apkt.asynctask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.ScrollView;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.ConnectException;

import a.apkt.R;
import a.apkt.json.LoginJson;
import a.apkt.json.OpReturnJson;
import a.apkt.model.OpReturn;
import a.apkt.service.AndroidService;
import a.apkt.service.HttpService;
import a.apkt.service.StaticVars;
import a.apkt.service.StringUtils;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;

public class OpReturnAsyncTask extends AsyncTask<String, Void, Boolean> {
    private boolean glassfishDown = false;
    private OpReturn opReturnRes;
    private View mProgressView;
    private ScrollView scrollView;
    private Activity activity;
    private LoginJson loginJson;
    private String text;
    private String email;
    private String type;
    private Context context;;


    public OpReturnAsyncTask(
            View mProgressView,
            ScrollView scrollView,
            Activity activity,
            LoginJson loginJson,
            String text,
            String email,
            String type) {
        this.mProgressView = mProgressView;
        this.scrollView = scrollView;
        this.activity = activity;
        this.loginJson = loginJson;
        this.text = text;
        this.email = email;
        this.type = type;
    }

    protected void onPreExecute() {
        if (mProgressView != null && scrollView != null){
            AndroidService.showProgress(activity, scrollView, mProgressView, true);
//            showProgress(true);
        }
    }

    @Override
    protected Boolean doInBackground(String... gcmRegId) {

        try {
            Long loginId = null;
            if (loginJson != null){
                loginId = loginJson.getId();
            }
            context = activity;
            OpReturn opReturn = new OpReturn(text, type, email, loginId, StringUtils.getLanguage(context));
            OpReturnJson opReturnJson = new OpReturnJson(opReturn, UtilsService.loadAuthAux(activity));

            String gson = new Gson().toJson(opReturnJson);

            // open URL, using a Reader to convert bytes to chars
            InputStreamReader reader = HttpService.post(activity, "opReturnRequest", gson);
            if (reader == null){
                glassfishDown = true;
                return false;
            }
            // parse the JSON back into a TextMessage
            opReturnRes = new Gson().fromJson(reader, OpReturn.class);

            if (opReturnRes != null) {
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
        if (mProgressView != null && scrollView != null){
            AndroidService.showProgress(activity, scrollView, mProgressView, false);
//            showProgress(false);
        }

        if (success) {
            final String uriString = "bitcoin:" + opReturnRes.getAddress() + "?amount=" + opReturnRes.getFee()/*+"&message=Payment&label=Satoshi&extra=other-param"*/;
            final Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.ask_choose_wallet)), StaticVars.SELECT_BITCOIN_WALLET_ACTIVITY_RESULT);
        }
        else if (!success) {
            if (glassfishDown) {
                UserMsgService.showDialogPositButtonMail(activity,
                        activity, R.string.alertdialog_internal_problem_title,
                        R.string.alertdialog_internal_problem_msg);
            }
        }
    }

//    /**
//     * Shows the progress UI and hides the login form.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    public void showProgress(final boolean show) {
//
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);
//
////            mActivityFormView.setVisibility(show ? View.GONE : View.VISIBLE);
////            mActivityFormView.animate().setDuration(shortAnimTime).alpha(
////                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
////                @Override
////                public void onAnimationEnd(Animator animation) {
////                    mActivityFormView.setVisibility(show ? View.GONE : View.VISIBLE);
////                }
////            });
//
//            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                    scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
//
//        }
//    }
}
