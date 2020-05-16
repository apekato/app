package a.apkt.asynctask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import a.apkt.ItemFragment;
import a.apkt.R;
import a.apkt.baseadapter.OpReturnBaseAdapter;
import a.apkt.json.ListOpReturnJson;
import a.apkt.model.Login;
import a.apkt.model.OpReturn.OpReturnType;
import a.apkt.model.OpReturn;
import a.apkt.service.HttpService;
import a.apkt.service.StaticVars;
import a.apkt.service.UserMsgService;

public class HttpAsynctask extends AsyncTask<Void, Void, Boolean>{

    boolean glassfishDown = false;
    private Context context;
    private Activity activity;
    private ItemFragment itemFragment;
    private Fragment fragment;
    private Object object;
    private Object resultObject;
    private String wsname;
//    private View mActivityFormView;
    private View mProgressView;
    private android.support.v7.app.ActionBar actionBar;
    private AbsListView mListView;
    private BaseAdapter mAdapter;
    private List<OpReturn> opReturnList = new ArrayList<OpReturn>();
    private Login login;
    private LinearLayout linearLayout;
    private String objGson;

    // Persist Object from Activity
    public <T> HttpAsynctask(Activity activity, T object, T resultObject, String wsname) {
        this.context = (Context) activity;
        this.activity = activity;
        this.object = object;
        this.resultObject = resultObject;
        this.wsname = wsname;
    }

    // Persist Object from Fragment
    public <T> HttpAsynctask(Activity activity, Fragment fragment, T object, String wsname) {
        this.context = (Context) activity;
        this.activity = activity;
        this.fragment = fragment;
        this.object = object;
        /*this.resultObject = resultObject;*/
        this.wsname = wsname;
    }

    // Get Order List
    public <T> HttpAsynctask(Activity activity, ItemFragment itemFragment, AbsListView mListView, BaseAdapter mAdapter, T object, T resultObject, String wsname, Login login) {
        this.activity = activity;
        this.itemFragment = itemFragment;
        this.context = (Context) activity;
        this.mListView = mListView;
        this.mAdapter = mAdapter;
        this.object = object;
        this.resultObject = resultObject;
        this.wsname = wsname;
        this.login = login;
    }

    protected void onPreExecute() {

        // progress bar shows up in fragments that do not have "AbsListView mListView", which hides the layout's LinearLayout
//        if (fragment != null){
//            linearLayout = (LinearLayout) activity.findViewById(R.id.activity_form);
//        }

//        if (fragment != null || mListView != null){
//            mActivityFormView = activity.findViewById(R.id.container);
//        }else {
//            mActivityFormView = activity.findViewById(R.id.activity_form);
//        }
        mProgressView = activity.findViewById(R.id.activity_progress);
        this.actionBar = ((AppCompatActivity)activity).getSupportActionBar();
        showProgress(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

//            String gson = RsaCypher.packData(activity, object);

            String gson = new Gson().toJson(object);

            gson = URLEncoder.encode(gson, "UTF-8");

            // open URL, using a Reader to convert bytes to chars
            InputStreamReader reader = HttpService.post(activity, wsname, gson);
            if (reader == null){
                glassfishDown = true;
                return false;
            }
            resultObject = new Gson().fromJson(reader, Object.class);

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
    protected void onPostExecute(final Boolean success) {
        showProgress(false);

        if (success) {

            objGson = new Gson().toJson(resultObject);

             if (mListView != null) { // Get Order List
                ListOpReturnJson listOpReturnJson = new Gson().fromJson(objGson, ListOpReturnJson.class);
                ItemFragment.OnItemFragmentInteractionListener mListener = (ItemFragment.OnItemFragmentInteractionListener) activity;

                if (listOpReturnJson != null && listOpReturnJson.getOpReturnList() != null){
                    mAdapter = new OpReturnBaseAdapter(activity, listOpReturnJson.getOpReturnList());
                    mListView.setAdapter(mAdapter);
                    opReturnList = listOpReturnJson.getOpReturnList();
                    mListener.onSetOpReturnItemFragmentInteraction(opReturnList);
                    ListOpReturnJson listOpReturnJson_1 = new Gson().fromJson(objGson, ListOpReturnJson.class);
                    if (login != null){
                        if (listOpReturnJson_1.getType().equals(OpReturnType.OP_RETURN_TYPE_TEXT)){
                            itemFragment.emptyMsg(mAdapter, StaticVars.TITLE_SECTION_WALL);
                        } else if (listOpReturnJson_1.getType().equals(OpReturnType.OP_RETURN_TYPE_NOTARIZATION)){
                            itemFragment.emptyMsg(mAdapter, StaticVars.TITLE_SECTION_NOTARIZE);
                        }
                    } else {
                        if (listOpReturnJson_1.getType().equals(OpReturnType.OP_RETURN_TYPE_TEXT)){
                            itemFragment.emptyMsg(mAdapter, StaticVars.TITLE_SECTION_WALL_PUBLIC);
                        } else if (listOpReturnJson_1.getType().equals(OpReturnType.OP_RETURN_TYPE_NOTARIZATION)){
                            itemFragment.emptyMsg(mAdapter, StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC);
                        }
                    }

                }
                itemFragment.onRefreshComplete();
            }else { // Persist Object from Activity
                Intent returnIntent = new Intent();
                returnIntent.putExtra("resultObject", objGson);
                activity.setResult(activity.RESULT_OK, returnIntent);
                activity.finish();

            }


        } else if (glassfishDown) {
            UserMsgService.showDialogPositButtonMail(activity,
                    context, R.string.alertdialog_internal_problem_title,
                    R.string.alertdialog_internal_problem_msg);

        }
    }

    @Override
    protected void onCancelled() {
        showProgress(false);
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
            int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

            // progress bar shows up in fragments that do not have "AbsListView mListView" by hiding the layout's LinearLayout
            if (fragment != null){
                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                linearLayout.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
            }


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

            // progress bar shows up in fragments that do not have "AbsListView mListView" by hiding the layout's LinearLayout
            if (fragment != null){
                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }


        }
//        if (show) {
//            actionBar.hide();
//        } else {
//            actionBar.show();
//        }
    }

}