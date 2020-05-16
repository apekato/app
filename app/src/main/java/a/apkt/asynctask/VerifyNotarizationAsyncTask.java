package a.apkt.asynctask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import a.apkt.ItemFragment;
import a.apkt.MainActivity;
import a.apkt.OpReturnEditActivity;
import a.apkt.R;
import a.apkt.VerifyNotarizationActivity;
import a.apkt.model.Tx;
import a.apkt.model.OpReturn;
import a.apkt.service.ProjService;
import a.apkt.service.StaticVars;

public class VerifyNotarizationAsyncTask extends AsyncTask<String, Void, Boolean> {

    private Activity activity;
    private View mProgressView;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private MenuItem actionAddItem;
    private Tx resultObject;
    private String txId;
    private boolean iOExceptionWrongFormat = false;
    private boolean fileNotFoundException = false;
    private ItemFragment itemFragment;
    private OpReturn opReturn;

    public VerifyNotarizationAsyncTask(Activity activity, MenuItem actionAddItem, String txId){
        this.activity = activity;
        this.actionAddItem = actionAddItem;
        this.txId = txId;
    }

    public VerifyNotarizationAsyncTask(Activity activity, ItemFragment itemFragment, OpReturn opReturn){
        this.activity = activity;
        this.itemFragment = itemFragment;
        this.opReturn = opReturn;
        txId = opReturn.getTxId();
    }


    protected void onPreExecute() {
        if (activity.getClass() == MainActivity.class){
            mProgressView = activity.findViewById(R.id.activity_progress);
            if (itemFragment != null){
                linearLayout = (LinearLayout) activity.findViewById(R.id.linear_layout);
            } else {
                scrollView = (ScrollView) activity.findViewById(R.id.scroll_view);
            }
            showProgress(true);
        }

    }

    @Override
    protected Boolean doInBackground(String... params) {

        try {

            String url_ = ProjService.BLOCK_CYPHER_ENDPOINT + "txs/" + txId + "?token=" + ProjService.BLOCK_CYPHER_TOKEN;

            URL url = new URL(url_);
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader =
                    new InputStreamReader(in);
            resultObject = new Gson().fromJson(reader, Tx.class);

        } catch (FileNotFoundException exception) {
            fileNotFoundException = true;
            return false;
        } catch (IOException exception) {
            iOExceptionWrongFormat = true;
            return false;
        }catch (Exception exception) {
            exception.printStackTrace(); // show exception details
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {


        if (success) {

            if (activity.getClass() == MainActivity.class && itemFragment == null){
                showProgress(false);
                MainActivity mainActivity = (MainActivity) activity;
                mainActivity.setVerifyNotarizationTx(resultObject);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                activity.startActivityForResult(intent, StaticVars.OPEN_DOCUMENT_VERIFY_NOTARIZATION_ACTIVITY_RESULT);
            } else if (activity.getClass() == MainActivity.class && itemFragment != null) {
                showProgress(false);
                String txBlockCypherJson = new Gson().toJson(resultObject);
                String opReturnJson = new Gson().toJson(opReturn);
                Intent intent = new Intent(activity, OpReturnEditActivity.class);
                Bundle args = new Bundle();
                args.putString("opReturnJson", opReturnJson);
                args.putString("txBlockCypherJson", txBlockCypherJson);
                //            args.putString("loginStringJson", loginStringJson);
                intent.putExtras(args);
                activity.startActivity(intent);
            } else if (activity.getClass() == VerifyNotarizationActivity.class) { // doesn't use showProgress() because it uses SwipeRefreshLayout
                    VerifyNotarizationActivity verifyNotarizationActivity = (VerifyNotarizationActivity) activity;
                    verifyNotarizationActivity.setTx(resultObject);
            } else if (activity.getClass() == OpReturnEditActivity.class) { // doesn't use showProgress() because it uses SwipeRefreshLayout
                OpReturnEditActivity OpReturnEditActivity = (OpReturnEditActivity) activity;
                OpReturnEditActivity.setTx(resultObject);
            }
        }
        else if (!success) {
            showProgress(false);
            if (fileNotFoundException) {
                Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_tx_id_invalid), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            } else if (iOExceptionWrongFormat){
                Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_tx_id_wrong_format), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }
        }
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = activity.getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (itemFragment != null){
            linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }else {
            scrollView.setVisibility(show ? View.GONE : View.VISIBLE); // scrollView from VerifyNotarizationFragment
            actionAddItem.setEnabled(show ? false : true); // actionAddItem from VerifyNotarizationFragment
        }
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
