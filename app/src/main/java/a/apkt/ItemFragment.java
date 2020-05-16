package a.apkt;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a.apkt.asynctask.HttpAsynctask;
import a.apkt.asynctask.VerifyNotarizationAsyncTask;
import a.apkt.baseadapter.DigCertBaseAdapter;
import a.apkt.json.ListOpReturnJson;
import a.apkt.model.DigCert;
import a.apkt.model.Login;
import a.apkt.model.OpReturn.OpReturnType;
import a.apkt.model.OpReturn;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.StaticVars;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener{

    private static final String LOG_TAG = "ItemFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mSectionNumber;
    private String mParam2;

    private OnItemFragmentInteractionListener mListener;

    private Activity activity;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    /*private ListAdapter mAdapter;*/
    private BaseAdapter mAdapter;

    private Login login;
    private View view;
    private HttpAsynctask httpAsynctask;
    private boolean isLoggedin; //PUBLIC LIST CODE
    private List<DigCert> digCertList = null;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String loginStringJson = null;

        if (getArguments() != null) {
            loginStringJson = getArguments().getString("loginStringJson");
        }

        login = new Gson().fromJson(loginStringJson, Login.class);

        boolean sf = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_item, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);

        // Retrieve the SwipeRefreshLayout and ListView instances
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        // BEGIN_INCLUDE (change_colors)
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);
        // END_INCLUDE (change_colors)


        try {
            initBaseAdapter(mSectionNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set the adapter
//        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    // BEGIN_INCLUDE (setup_views)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the adapter
        mListView.setAdapter(mAdapter);

        // BEGIN_INCLUDE (setup_refreshlistener)
        /**
         * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * refresh" gesture, SwipeRefreshLayout invokes
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                try {
                    initiateRefresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // END_INCLUDE (setup_refreshlistener)
    }
    // END_INCLUDE (setup_views)

    // BEGIN_INCLUDE (initiate_refresh)
    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    private void initiateRefresh() throws IOException {
        Log.i(LOG_TAG, "initiateRefresh");
        initBaseAdapter(mSectionNumber);

    }
    // END_INCLUDE (initiate_refresh)

    // BEGIN_INCLUDE (refresh_complete)
    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which updates the data in the
     * ListAdapter and turns off the progress bar.
     */
    public void onRefreshComplete() {
        Log.i(LOG_TAG, "onRefreshComplete");

        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }
    // END_INCLUDE (refresh_complete)

    public void initBaseAdapter(int number) throws IOException {
        isLoggedin = mListener.onLogin();
        if (isLoggedin){
            switch (number) {
                case StaticVars.TITLE_SECTION_WALL:
                    ListOpReturnJson listOpReturnJson = new ListOpReturnJson();
                    listOpReturnJson.setUserId(login.getId());
                    listOpReturnJson.setType(OpReturnType.OP_RETURN_TYPE_TEXT);
                    listOpReturnJson.setAuthAux(UtilsService.loadAuthAux(activity));
                    if (CheckConnectivity.checkNow(activity)) {
                        httpAsynctask = new HttpAsynctask(activity, this, mListView,   mAdapter, listOpReturnJson, listOpReturnJson, "opReturnList", login);
                        httpAsynctask.execute();
                    }else {
                        UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                                R.string.alertdialog_no_connectivity);
                    }
                    break;
                case StaticVars.TITLE_SECTION_NOTARIZE:
                    ListOpReturnJson listOpReturnJson_1 = new ListOpReturnJson();
                    listOpReturnJson_1.setUserId(login.getId());
                    listOpReturnJson_1.setType(OpReturnType.OP_RETURN_TYPE_NOTARIZATION);
                    listOpReturnJson_1.setAuthAux(UtilsService.loadAuthAux(activity));
                    if (CheckConnectivity.checkNow(activity)) {
                        httpAsynctask = new HttpAsynctask(activity, this, mListView,   mAdapter, listOpReturnJson_1, listOpReturnJson_1, "opReturnList", login);
                        httpAsynctask.execute();
                    }else {
                        UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                                R.string.alertdialog_no_connectivity);
                    }
                    break;
                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE:
                    mSwipeRefreshLayout.setEnabled(false);
//                    activity.deleteFile(StaticVars.DIGCERTLIST);
                    File file = UtilsService.openFileInternalStorage(activity, StaticVars.DIGCERTLIST);

                    digCertList = (List<DigCert>) UtilsService.getObjectFromFile(file);

                    if (digCertList == null){
                        digCertList = new ArrayList<>();
                    }
                    mAdapter = new DigCertBaseAdapter(activity, digCertList);

                    if (digCertList.size() == 0){
                        emptyMsg(mAdapter, StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE);
                    }
                    break;
            }
        } else if (!isLoggedin) {
            switch (number) {
                case StaticVars.TITLE_SECTION_WALL_PUBLIC:
                    ListOpReturnJson listOpReturnJson = new ListOpReturnJson();
                    listOpReturnJson.setType(OpReturnType.OP_RETURN_TYPE_TEXT);
                    if (CheckConnectivity.checkNow(activity)) {
                        httpAsynctask = new HttpAsynctask(activity, this, mListView,   mAdapter, listOpReturnJson, listOpReturnJson, "opReturnList", login);
                        httpAsynctask.execute();
                    }else {
                        UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                                R.string.alertdialog_no_connectivity);
                    }
                    break;
                case StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC:
                    ListOpReturnJson listOpReturnJson_1 = new ListOpReturnJson();
                    listOpReturnJson_1.setType(OpReturnType.OP_RETURN_TYPE_NOTARIZATION);
                    listOpReturnJson_1.setAuthAux(UtilsService.loadAuthAux(activity));
                    if (CheckConnectivity.checkNow(activity)) {
                        httpAsynctask = new HttpAsynctask(activity, this, mListView,   mAdapter, listOpReturnJson_1, listOpReturnJson_1, "opReturnList", login);
                        httpAsynctask.execute();
                    }else {
                        UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                                R.string.alertdialog_no_connectivity);
                    }
                    break;
//                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC:
//                    mSwipeRefreshLayout.setEnabled(false);
////                    activity.deleteFile(StaticVars.DIGCERTLIST);
//                    File file = UtilsService.openFileInternalStorage(activity, StaticVars.DIGCERTLIST);
//
//                    digCertList = (List<DigCert>) UtilsService.getObjectFromFile(file);
//
//                    if (digCertList == null){
//                        digCertList = new ArrayList<>();
//                    }
//                    mAdapter = new DigCertBaseAdapter(activity, digCertList);
//
//                    if (digCertList.size() == 0){
//                        emptyMsg(mAdapter, StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC);
//                    }
//                    break;
            }
        }
    }

    public void emptyMsg(BaseAdapter mAdapter, int mSectionNumber){
        TextView emptyMsg = (TextView) view.findViewById(R.id.empty_msg_textView);
        if (mAdapter.getCount() == 0) {
            setEmptyListMsg(mSectionNumber, emptyMsg);
        } else {
            emptyMsg.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.activity = activity;

            mListener = (OnItemFragmentInteractionListener) activity;

            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ((MainActivity) activity).onSectionAttached(mSectionNumber);

            switch (mSectionNumber) {
            }

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void setEmptyListMsg(int number, TextView emptyMsg) {
        emptyMsg.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        if (isLoggedin){
            switch (number) {
                case StaticVars.TITLE_SECTION_WALL:
                    emptyMsg.setText(activity.getResources().getText(R.string.empty_list_msg));
                    break;
                case StaticVars.TITLE_SECTION_NOTARIZE:
                    emptyMsg.setText(activity.getResources().getText(R.string.empty_list_notarization));
                    break;
                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE:
                    emptyMsg.setText(activity.getResources().getText(R.string.empty_list_digital_certificate));
                    break;
            }
        } else if (!isLoggedin) {
            switch (number) {
                case StaticVars.TITLE_SECTION_WALL_PUBLIC:
                    emptyMsg.setText(activity.getResources().getText(R.string.empty_list_msg));
                    break;
                case StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC:
                    emptyMsg.setText(activity.getResources().getText(R.string.empty_list_notarization));
                    break;
//                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC:
//                    emptyMsg.setText(activity.getResources().getText(R.string.empty_list_digital_certificate));
//                    emptyMsg.setVisibility(View.VISIBLE);
//                    mListView.setVisibility(View.GONE);
//                    break;
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (null != mListener) {

            Intent intent;
            Bundle args;

            if (isLoggedin){
                switch (mSectionNumber) {
                    case StaticVars.TITLE_SECTION_WALL:
                        opReturnOnItemClick(position);
                        break;
                    case StaticVars.TITLE_SECTION_NOTARIZE:
                        opReturnOnItemClick(position);
                        break;
                    case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE:
                        digitalCertificateOnItemClick(position);
                        break;
                }
            } else if (!isLoggedin) {
                switch (mSectionNumber) {
                    case StaticVars.TITLE_SECTION_WALL_PUBLIC:
                        opReturnOnItemClick(position);
                        break;
                    case StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC:
                        opReturnOnItemClick(position);
                        break;
//                    case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC:
//                        digitalCertificateOnItemClick(position);
//                    break;
                }
            }
        }
    }

    public void digitalCertificateOnItemClick(int position){
        DigCert digCert = digCertList.get(position);
        String digCertJson = new Gson().toJson(digCert);
        Intent intent = new Intent(activity, DigCertActivity.class);
        Bundle args = new Bundle();
        args.putString("digCertJson", digCertJson);
        intent.putExtras(args);
        activity.startActivityForResult(intent, StaticVars.DIG_CERT_ACTIVITY_RESULT);
    }

    public void opReturnOnItemClick(int position){
        //        mListener = (OnItemFragmentInteractionListener) activity;
        List<OpReturn> opReturnList = mListener.onOpReturnItemFragmentInteraction();
        if (opReturnList.size() > 0) {
            OpReturn opReturn = opReturnList.get(position);
//            String opReturnJson = new Gson().toJson(opReturn);
//            Intent intent = new Intent(activity, OpReturnEditActivity.class);
//            Bundle args = new Bundle();
//            args.putString("opReturnJson", opReturnJson);
//            args.putString("loginStringJson", loginStringJson);
//            intent.putExtras(args);
//            activity.startActivity(intent);

            if (CheckConnectivity.checkNow(activity)) {
                VerifyNotarizationAsyncTask verifyNotarizationAsyncTask = new VerifyNotarizationAsyncTask(activity, this, opReturn);
                verifyNotarizationAsyncTask.execute();
            }else {
                UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                        R.string.alertdialog_no_connectivity);
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemFragmentInteractionListener {
        // TODO: Update argument type and name
        public List<OpReturn> onOpReturnItemFragmentInteraction();

        public void onSetOpReturnItemFragmentInteraction(List<OpReturn> opReturnList);

        public boolean onLogin();

    }

}
