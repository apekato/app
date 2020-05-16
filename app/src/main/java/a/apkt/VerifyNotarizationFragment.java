package a.apkt;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import com.google.gson.Gson;

import org.spongycastle.util.encoders.Hex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import a.apkt.json.LoginJson;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.UserMsgService;

public class VerifyNotarizationFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private int mSectionNumber;
    private Activity activity;
    private LoginJson loginJson;
    private View view;
    private ScrollView scrollView;
    private EditText etTxId;
    private OnVerifyNotarizationFragmentIeractionListener mListener;

    public VerifyNotarizationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String loginStringJson = null;
        if (getArguments() != null) {
            loginStringJson = getArguments().getString("loginStringJson");
        }
        loginJson = new Gson().fromJson(loginStringJson, LoginJson.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_verify_notarization, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        etTxId = (EditText) view.findViewById(R.id.et_tx_id);

        return view;
    }

    // pick request code
    private static final int PICK_FILE_REQUEST = 100;

    // Choose file with intent
//    public void takeFile(){
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("*/*");
//        startActivityForResult(intent, PICK_FILE_REQUEST);
//    }
//
//    // Retrieve file from intent
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        String path=null,hashString=null;
//
//        if (requestCode == PICK_FILE_REQUEST) {
//            if (data != null && data.getData() != null) {
//                Uri uri = data.getData();
//                path=uri.toString();
//                Log.d("NOTARIZE", uri.getPath());
//
//                // set persistent Authorization to read the file from data storage
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    getActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
//                }
//
//                // set image
//                /*try {
//                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
//                    Bitmap bitmap = BitmapFactory.decodeStream(is);
//                    if(bitmap==null){
//                        Log.d("NOTARIZE","no image file");
//                        //imageView.setImageDrawable(getResources().getDrawable(R.drawable.eternity_logo));
//                    }else {
//                        //imageView.setImageBitmap(bitmap);
//                    }
//                }catch(Exception e){
//                    Log.d("NOTARIZE","no image file");
//                    //imageView.setImageDrawable(getResources().getDrawable(R.drawable.eternity_logo));
//                }*/
//
//                // read file
//                byte[] bytes=null;
//                try {
//                    InputStream is = getActivity().getContentResolver().openInputStream(uri);
//                    int size = (int) is.available();
//                    bytes = new byte[size];
//                    is.read(bytes, 0, bytes.length);
//                    is.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                // build digest
//                try {
//                    MessageDigest digest = null;
//                    digest = MessageDigest.getInstance("SHA-256");
//                    digest.update(bytes);
//                    hashString = Hex.toHexString(digest.digest());
////                    encodeHexString
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//
//                // run
//                if(path!=null && hashString!=null){
//
//                    Boolean conn = CheckConnectivity.checkNow(activity);
//                    if (conn == true) {
//
////                        NotarizeAsyncTask notarizeAsyncTask = new NotarizeAsyncTask();
////                        notarizeAsyncTask.execute(hashString);
//
//                    }else {
//                        UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
//                                R.string.alertdialog_no_connectivity);
//                    }
//                }
//                else {
////                    dialogFailure();
//                }
//
//            }
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.activity = activity;
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            ((MainActivity) activity).onSectionAttached(mSectionNumber);
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getTxIdFromVerifyNotarizationFragment(){
        return etTxId.getText().toString();
    }

    public void setTxIdFromVerifyNotarizationFragment(){
        etTxId.setText("");
    }

    public interface OnVerifyNotarizationFragmentIeractionListener {

    }

}
