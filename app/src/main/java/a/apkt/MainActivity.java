package a.apkt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.log4j.chainsaw.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import a.apkt.asynctask.OpReturnAsyncTask;
//import a.apkt.asynctask.SignerAsyncTask;
import a.apkt.asynctask.SignerAsyncTask;
import a.apkt.asynctask.VerifyNotarizationAsyncTask;
import a.apkt.backingbean.LoginBB;
import a.apkt.json.ListDigCertJson;
import a.apkt.json.ListOpReturnJson;
import a.apkt.json.LoginJson;
import a.apkt.model.DigCert;
import a.apkt.model.OpReturn.OpReturnType;
import a.apkt.model.OpReturn;
import a.apkt.model.Tx;
import a.apkt.model.TxOutput;
import a.apkt.service.CheckConnectivity;
import a.apkt.service.StaticVars;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;
import a.apkt.signer.KeyStorePKCS12;
import a.apkt.sqlite.LoginSqlite;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ItemFragment.OnItemFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private ActionBar actionBar;
    private MenuItem actionAddItem;
    private MenuItem actionImportItem;
    private MenuItem actionPostItem;
    private MenuItem actionWriteItem;
    private MenuItem actionVerifyItem;

    private long paramLoginId;
    private Activity activity;
    private String loginStringJson;
    private LoginJson loginJson;
    private ListOpReturnJson listOpReturnJson= new ListOpReturnJson();
    private boolean isLoggedin; //PUBLIC LIST CODE

    private String selectedEmailAccount = null;
    private Tx verifyNotarizationTx;
    private TextView mEmailView;

    private TextView mPwdView;
    private String pwd = null;
    private Uri uri;

    private Fragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public void loadLoginJson() {
        if (loginJson != null){
            paramLoginId = loginJson.getId();
            LoginBB loginBB = new LoginBB(loginJson);
            loginStringJson = new Gson().toJson(loginBB.getLoginAux());
            isLoggedin = true;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
       /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
       FragmentManager fragmentManager = getSupportFragmentManager();
       fragmentManager.beginTransaction()
               .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
               .commit();*/
        if (isLoggedin){
            if (
                    position == StaticVars.TITLE_SECTION_WALL - 1
                            || position == StaticVars.TITLE_SECTION_NOTARIZE - 1
                            || position == StaticVars.TITLE_SECTION_DIGITAL_SIGNATURE - 1
                            || position == StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE - 1
                    ) {
                initItemFragment(position);
            } else if (
                    position == StaticVars.TITLE_SECTION_VERIFY_NOTARIZATION - 1
                            ) {
                initVerifyNotarizationFragment(position);
            } else if (
                    position == StaticVars.TITLE_SECTION_EXIT -1 /*Identification*/
                    ) {
                exit();
            }

        } else if (!isLoggedin) {
            if (
                    position == StaticVars.TITLE_SECTION_WALL_PUBLIC - 1
                            || position == StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC - 1
//                            || position == StaticVars.TITLE_SECTION_DIGITAL_SIGNATURE_PUBLIC - 1
//                            || position == StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC - 1
                    ) {
                initItemFragment(position);
            } else if (
                    position == StaticVars.TITLE_SECTION_VERIFY_NOTARIZATION_PUBLIC - 1
                    ) {
                initVerifyNotarizationFragment(position);
            }
            //    To activate Login search keyword ACTIVATELOGIN: uncomment code section containing TITLE_SECTION_LOGIN
            else if (
                    position == StaticVars.TITLE_SECTION_LOGIN - 1
                    ) {
                Intent it = new Intent(activity, LoginActivity.class);
                Bundle params = new Bundle();
                params.putBoolean("isLogin", true);
                it.putExtras(params);
                startActivity(it);
                finish();
            }
        }
    }

    public void initItemFragment(int position) {
        // Create new fragment and transaction
        newFragment = new ItemFragment();

        initFragment(position, newFragment);
    }

    public void initVerifyNotarizationFragment(int position) {
        // Create new fragment and transaction
        Fragment newFragment = new VerifyNotarizationFragment();

        initFragment(position, newFragment);
    }

    public void initFragment(int position, Fragment newFragment) {
        Intent itLogin = getIntent();
        if (itLogin != null) {
            Bundle params = itLogin.getExtras();
            if (params != null && loginJson == null) {
                loginJson = new Gson().fromJson(params.getString("loginStringJson"), LoginJson.class);
            }
        }

        loadLoginJson();

        Bundle args = new Bundle();
        args.putInt("section_number", position + 1);
        if (loginStringJson != null){
            args.putString("loginStringJson", loginStringJson);
        }

        newFragment.setArguments(args);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.container, newFragment);

        // Commit the transaction
        transaction.commit();
    }

    public void exit(){
        LoginSqlite loginSqlite = new LoginSqlite(this);
        loginSqlite.deleteUserLogin();
        finish();
    }

    public void onSectionAttached(int number) {
        if (isLoggedin){
            switch (number) {
                case StaticVars.TITLE_SECTION_DIGITAL_SIGNATURE:
                    mTitle = getString(R.string.title_section_digital_signatures);
                    break;
                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE:
                    mTitle = getString(R.string.title_section_digital_certificates);
                    break;
                case StaticVars.TITLE_SECTION_WALL:
                    mTitle = getString(R.string.title_section_wall);
                    break;
                case StaticVars.TITLE_SECTION_NOTARIZE:
                    mTitle = getString(R.string.title_section_notarizations);
                    break;
                case StaticVars.TITLE_SECTION_VERIFY_NOTARIZATION:
                    mTitle = getString(R.string.title_section_verify_notarization);
                    break;
            }
        } else if (!isLoggedin) {
            switch (number) {
//                case StaticVars.TITLE_SECTION_DIGITAL_SIGNATURE_PUBLIC:
//                    mTitle = getString(R.string.title_section_digital_signatures);
//                    break;
//                case StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE_PUBLIC:
//                    mTitle = getString(R.string.title_section_digital_certificates);
//                    break;
                case StaticVars.TITLE_SECTION_WALL_PUBLIC:
                    mTitle = getString(R.string.title_section_wall);
                    break;
                case StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC:
                    mTitle = getString(R.string.title_section_notarizations);
                    break;
                case StaticVars.TITLE_SECTION_VERIFY_NOTARIZATION_PUBLIC:
                    mTitle = getString(R.string.title_section_verify_notarization);
                    break;
                //    To activate Login search keyword ACTIVATELOGIN: uncomment code section containing TITLE_SECTION_LOGIN
                case StaticVars.TITLE_SECTION_LOGIN:
                    mTitle = getString(R.string.title_section_login);
                    break;
            }
        }
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        // code block to setup ic_drawer
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_white);
        // end of code block to setup ic_drawer
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            actionAddItem = menu.findItem(R.id.action_add);
            actionImportItem = menu.findItem(R.id.action_import);
            actionPostItem = menu.findItem(R.id.action_post);
            actionWriteItem = menu.findItem(R.id.action_write);
            actionVerifyItem = menu.findItem(R.id.action_verify);
            restoreActionBar();
            setActionItemVisible();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void setActionItemVisible() {
        if (
                mTitle.equals(getString(R.string.title_section_digital_signatures))
        ) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
            actionAddItem.setVisible(true);
            actionImportItem.setVisible(false);
            actionPostItem.setVisible(false);
            actionWriteItem.setVisible(false);
            actionVerifyItem.setVisible(false);
        } else if (
                mTitle.equals(getString(R.string.title_section_digital_certificates))
        ) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
            actionAddItem.setVisible(false);
            actionImportItem.setVisible(true);
            actionPostItem.setVisible(false);
            actionWriteItem.setVisible(false);
            actionVerifyItem.setVisible(false);
        } else if (
                mTitle.equals(getString(R.string.title_section_wall))
                ) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
            actionAddItem.setVisible(false);
            actionImportItem.setVisible(false);
            actionPostItem.setVisible(false);
            actionWriteItem.setVisible(true);
            actionVerifyItem.setVisible(false);
        } else if (
                mTitle.equals(getString(R.string.title_section_notarizations))
                ) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
            actionAddItem.setVisible(true);
            actionImportItem.setVisible(false);
            actionPostItem.setVisible(false);
            actionWriteItem.setVisible(false);
            actionVerifyItem.setVisible(false);
        } else if (
                mTitle.equals(getString(R.string.title_section_verify_notarization))
                ) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue_actionbar)));
            actionAddItem.setVisible(true);
            actionImportItem.setVisible(false);
            actionPostItem.setVisible(false);
            actionWriteItem.setVisible(false);
            actionVerifyItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (actionAddItem != null && id == actionAddItem.getItemId()) {
            if (mTitle.toString().equals(getString(R.string.title_section_digital_signatures))) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, StaticVars.OPEN_DOCUMENT_SIGNATURE_ACTIVITY_RESULT);
                return true;
            } else
            if (mTitle.toString().equals(getString(R.string.title_section_notarizations))) {

                // add a view to AlertDialog
                FrameLayout frameView = new FrameLayout(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.layout_email,
                        frameView);

                mEmailView = (TextView) dialoglayout
                        .findViewById(R.id.email);
                Spinner mEmailSpinner = (Spinner) dialoglayout
                        .findViewById(R.id.email_spinner);

                mEmailSpinner.setVisibility(View.GONE);

                // keyword search to undo this conde change: GET_EMAIL_WITH_SPINNER. email spinner not used because AndroidManifest permission GET_ACCOUNTS requires privacy policy in order to publish on Google Play
//                emailAccounts = UtilsService.getRegisteredEmailAddresses(activity);
//                if (emailAccounts == null) {
//                    mEmailSpinner.setVisibility(View.GONE);
//                    mEmailView.setEnabled(false);
//                    UserMsgService.showDialog(activity,
//                            R.string.error_email_nonexistent_title,
//                            R.string.error_email_nonexistent);
//                } else if (emailAccounts.size() == 1) {
//                    mEmailSpinner.setVisibility(View.GONE);
//
//                    selectedEmailAccount = emailAccounts.get(0);
//
//                    mEmailView.setText(selectedEmailAccount);
//
//                } else if (emailAccounts.size() >= 2) {
//
//                    mEmailView.setVisibility(View.GONE);
//
//                    // Create an ArrayAdapter using the string array and a default
//                    // spinner layout
//                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                            activity, R.layout.spinner_text_black_color,
//                            emailAccounts);
//                    // Specify the layout to use when the list of choices appears
//                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    // Apply the adapter to the spinner
//                    mEmailSpinner.setAdapter(adapter);
//                    mEmailSpinner
//                            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//                                @Override
//                                public void onItemSelected(AdapterView<?> arg0,
//                                                           View arg1, int positon, long arg3) {
//                                    selectedEmailAccount = emailAccounts
//                                            .get(positon);
//                                }
//
//                                @Override
//                                public void onNothingSelected(AdapterView<?> arg0) {
//                                    // TODO Auto-generated method stub
//                                }
//                            });
//                }

//                new AlertDialog.Builder(activity)
//                        .setTitle(
//                                R.string.alertdialog_email_address_title)
//                        .setMessage(
//                                R.string.alertdialog_email_address)
//                        .setView(frameView)
//                        .setPositiveButton(getText(R.string.ok).toString(),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int whichButton) {
                                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                        intent.setType("*/*");
                                        startActivityForResult(intent, StaticVars.OPEN_DOCUMENT_NOTARIZE_ACTIVITY_RESULT);


//                                    }
//                                })
//                        .setNegativeButton(getText(R.string.action_cancel).toString(),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int whichButton) {
//                                        dialog.dismiss();
//                                    }
//                                }).show();
                return true;
            } else if (mTitle.toString().equals(getString(R.string.title_section_verify_notarization))) {
                VerifyNotarizationFragment verifyNotarizationFragment =
                        (VerifyNotarizationFragment) getFragmentManager().findFragmentById((R.id.container));

                String txId = verifyNotarizationFragment.getTxIdFromVerifyNotarizationFragment();
                if (CheckConnectivity.checkNow(this)) {
                    VerifyNotarizationAsyncTask verifyNotarizationAsyncTask = new VerifyNotarizationAsyncTask(activity, actionAddItem, txId);
                    verifyNotarizationAsyncTask.execute();
                }else {
                    UserMsgService.showDialog(this, R.string.alertdialog_no_connectivity_title,
                            R.string.alertdialog_no_connectivity);
                }
                return true;
            }
        } else if (actionImportItem!= null && id == actionImportItem.getItemId()) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            startActivityForResult(intent, StaticVars.OPEN_DOCUMENT_DIGITAL_CERTIFICATE_ACTIVITY_RESULT);
            return true;
        } else if (actionWriteItem != null && id == actionWriteItem.getItemId()){

            // add a view to AlertDialog
            FrameLayout frameView = new FrameLayout(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.layout_email,
                    frameView);

            mEmailView = (TextView) dialoglayout
                    .findViewById(R.id.email);
            Spinner mEmailSpinner = (Spinner) dialoglayout
                    .findViewById(R.id.email_spinner);

            mEmailSpinner.setVisibility(View.GONE);

            new AlertDialog.Builder(activity)
                    .setTitle(
                            R.string.alertdialog_email_address_title)
                    .setMessage(
                            R.string.alertdialog_email_address)
                    .setView(frameView)
                    .setPositiveButton(getText(R.string.ok).toString(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    Intent intent = new Intent(activity, OpReturnActivity.class);
                                    Bundle params = new Bundle();

                                    if (loginJson == null){
                                        loginJson = new LoginJson();
                                    }

                                    // keyword search to undo this code change: GET_EMAIL_WITH_SPINNER.
                                    // Get selectedEmailAccount from EmailView because email spinner is not used since AndroidManifest permission GET_ACCOUNTS requires privacy policy in order to publish on Google Play
                                    // delete or comment code below when email spinner is used
                                    selectedEmailAccount = mEmailView.getText().toString();
                                    loginJson.setEmail(selectedEmailAccount);
                                    LoginBB loginBB = new LoginBB(loginJson);
                                    loginStringJson = new Gson().toJson(loginBB.getLoginAux());
                                    loginJson = null;
                                    params.putString("loginStringJson", loginStringJson);
                                    intent.putExtras(params);
                                    if (isLoggedin){
                                        startActivityForResult(intent, StaticVars.OP_RETURN_ACTIVITY_RESULT);
                                    } else {
                                        startActivityForResult(intent, StaticVars.OP_RETURN_ACTIVITY_PUBLIC_RESULT);
                                    }
                                }
                            })
                    .setNegativeButton(getText(R.string.action_cancel).toString(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.dismiss();
                                }
                            }).show();
            return true;
        }
//    else if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == StaticVars.OPEN_DOCUMENT_SIGNATURE_ACTIVITY_RESULT) {
            if (data != null && data.getData() != null) {
                uri = data.getData();

                // add a view to AlertDialog
                FrameLayout frameView = new FrameLayout(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.layout_password,
                        frameView);

                mPwdView = (TextView) dialoglayout
                        .findViewById(R.id.password);

                new AlertDialog.Builder(activity)
                        .setTitle(
                                R.string.alertdialog_import_digital_certificate_title)
                        .setMessage(
                                R.string.alertdialog_import_digital_certificate)
                        .setView(frameView)
                        .setPositiveButton(getText(R.string.ok).toString(),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        MainActivity mainActivity = (MainActivity) activity;
                                        pwd = mPwdView.getText().toString();
                                        SignerAsyncTask signerAsyncTask = new SignerAsyncTask(null, null, mainActivity, uri, pwd);
                                        signerAsyncTask.execute();
                                        uri = null;
                                    }
                                })
                        .setNegativeButton(getText(R.string.action_cancel).toString(),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dialog.dismiss();
                                    }
                                }).show();

            }
        }else
        if (requestCode == StaticVars.OPEN_DOCUMENT_DIGITAL_CERTIFICATE_ACTIVITY_RESULT) {
            if (data != null && data.getData() != null) {
                uri = data.getData();

                // add a view to AlertDialog
                FrameLayout frameView = new FrameLayout(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.layout_password,
                        frameView);

                mPwdView = (TextView) dialoglayout
                        .findViewById(R.id.password);

                boolean isPfx = false;

                // Retrieve a file's MIME type
                String mimeType = UtilsService.retrieveMimeType(activity, uri);
                if (mimeType.endsWith("x-pkcs12")){
                    isPfx = true;
                }

                if (isPfx) {

                    new AlertDialog.Builder(activity)
                            .setTitle(
                                    R.string.alertdialog_import_digital_certificate_title)
                            .setMessage(
                                    R.string.alertdialog_import_digital_certificate)
                            .setView(frameView)
                            .setPositiveButton(getText(R.string.ok).toString(),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {

                                            pwd = mPwdView.getText().toString();
                                            try {

                                                File certFile = UtilsService.openFileInternalStorage(activity, StaticVars.DIGCERTLIST);
                                                List<DigCert> digCertList = (List<DigCert>) UtilsService.getObjectFromFile(certFile);

                                                String certName = UtilsService.retrieveFileName(activity, uri);

                                                // open InputStream to load keystore
                                                InputStream digCertLoadKeystoreStream = activity.getContentResolver().openInputStream(uri);

                                                KeyStore keyStore = KeyStorePKCS12.loadKeystore(activity, digCertLoadKeystoreStream, pwd);
//                                                 KeyStore keyStore = KeyStorePKCS12.loadKeystore(activity, digCertLoadKeystoreStream, "17hinP");

                                                // open InputStream to convert it to bytes
                                                InputStream digCertStream = activity.getContentResolver().openInputStream(uri);
                                                byte[] digCertBytes = UtilsService.convertInputStreamToBytes(digCertStream);

                                                // for verification of existing certificate in internal storage
                                                boolean existDigCert = false;

                                                // initialize list if empty
                                                if (digCertList == null) {
                                                    digCertList = new ArrayList<>();
                                                } else {// if not empty, check digCertBytes sha256 hash matches an existing digCert
                                                    InputStream digCertStreamSha256 = activity.getContentResolver().openInputStream(uri);
                                                    String digCertSha256 = UtilsService.sha256Doc(activity, digCertStreamSha256);

                                                    Iterator iterator = digCertList.iterator();
                                                    int index = 0;

                                                    while (iterator.hasNext()) {
                                                        DigCert digCertIt = (DigCert) iterator.next();
                                                        InputStream digCertItStream = UtilsService.convertBytesToInputStream(digCertIt.getDigCertBytes());
                                                        String digCertSha256Iterator = UtilsService.sha256Doc(activity, digCertItStream);
                                                        if (digCertSha256.equals(digCertSha256Iterator)) {
                                                            existDigCert = true;
                                                        }
                                                    }
                                                }
                                                if (!existDigCert) {

                                                    DigCert digCert = KeyStorePKCS12.certInfo(KeyStorePKCS12.certicateChain(keyStore));
                                                    digCert.setFileName(certName);
                                                    digCert.setDigCertBytes(digCertBytes);
                                                    digCertList.add(digCert);
                                                    UtilsService.saveObjectInternalStorage(activity, digCertList, StaticVars.DIGCERTLIST);
                                                }
                                                initItemFragment(StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE - 1);
                                                uri = null;
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                                uri = null;
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                uri = null;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                uri = null;
                                            }
                                        }
                                    })
                            .setNegativeButton(getText(R.string.action_cancel).toString(),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                } else {
                    Toast toast = Toast.makeText(activity, activity.getString(R.string.toast_file_invalid), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }

            }
        }
        else if (requestCode == StaticVars.OP_RETURN_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                initItemFragment(StaticVars.TITLE_SECTION_WALL - 1);
            }
        }
        else if (requestCode == StaticVars.OP_RETURN_ACTIVITY_PUBLIC_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                initItemFragment(StaticVars.TITLE_SECTION_WALL_PUBLIC - 1);
            }
        }
        else if (requestCode == StaticVars.NOTARIZE_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                initItemFragment(StaticVars.TITLE_SECTION_NOTARIZE - 1);
            }
        }
        else if (requestCode == StaticVars.NOTARIZE_PUBLIC_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                initItemFragment(StaticVars.TITLE_SECTION_NOTARIZE_PUBLIC - 1);
            }
        }
        else if (requestCode == StaticVars.OPEN_DOCUMENT_NOTARIZE_ACTIVITY_RESULT) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                String path =uri.toString();
                InputStream is = null;
                try {
                    is = activity.getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String dataHexHashString = UtilsService.sha256Doc(activity, is);

                // run
                if(path!=null && dataHexHashString!=null){
                    Boolean conn = CheckConnectivity.checkNow(activity);
                    if (conn) {

                        // keyword search to undo this code change: GET_EMAIL_WITH_SPINNER.
                        // Get selectedEmailAccount from EmailView because email spinner is not used since AndroidManifest permission GET_ACCOUNTS requires privacy policy in order to publish on Google Play
                        // delete or comment code below when email spinner is used
                        selectedEmailAccount = mEmailView.getText().toString();

                        OpReturnAsyncTask opReturnAsyncTask = new OpReturnAsyncTask(
                                null, // View
                                null, // ScrollView
                                activity,
                                loginJson,
                                dataHexHashString,
                                selectedEmailAccount,
                                OpReturnType.OP_RETURN_TYPE_NOTARIZATION);
                        opReturnAsyncTask.execute();

                    }else {
                        UserMsgService.showDialog(activity, R.string.alertdialog_no_connectivity_title,
                                R.string.alertdialog_no_connectivity);
                    }
                }
            }
        } else if (requestCode == StaticVars.OPEN_DOCUMENT_VERIFY_NOTARIZATION_ACTIVITY_RESULT) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                InputStream is = null;
                try {
                    is = activity.getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String dataHexHashString = UtilsService.sha256Doc(activity, is);

                if (dataHexHashString != null){
                    VerifyNotarizationFragment verifyNotarizationFragment =
                            (VerifyNotarizationFragment) getFragmentManager().findFragmentById((R.id.container));
                    // set EditText et_tx_id to null, so that when user returns to fragment the editText will be empty
                    verifyNotarizationFragment.setTxIdFromVerifyNotarizationFragment();
                    boolean isDataHexEqual = false;
                    for (TxOutput to : verifyNotarizationTx.getOutputs()){
                        String dataHex= to.getDataHex();
                        if (dataHexHashString.equals(dataHex)){
                            isDataHexEqual = true;
                        }
                    }

                    Intent intent = new Intent(activity, VerifyNotarizationActivity.class);
                    Bundle args = new Bundle();
                    String verifyNotarizationTxJson = new Gson().toJson(verifyNotarizationTx);
                    args.putString("verifyNotarizationTxJson", verifyNotarizationTxJson);
                    args.putString("dataHexHashString", dataHexHashString);
                    args.putBoolean("isDataHexEqual", isDataHexEqual);
                    intent.putExtras(args);
                    activity.startActivity(intent);
                }
            }
        } else if (requestCode == StaticVars.SELECT_BITCOIN_WALLET_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK // RESULT_CANCELED works with testnet wallet on testnet network
                    || resultCode == RESULT_CANCELED // RESULT_CANCELED works with AirBitz wallet and other wallets on main network
                    ) {

                Intent it = new Intent(activity, OpReturnRqstdActivity.class);
                Bundle args = new Bundle();
                args.putString("titleSection", getResources().getText(R.string.title_section_notarizations).toString());
                it.putExtras(args);
                if (isLoggedin){
                    startActivityForResult(it, StaticVars.NOTARIZE_RESULT);
                } else {
                    startActivityForResult(it, StaticVars.NOTARIZE_PUBLIC_RESULT);
                }

            }
        } else if (requestCode == StaticVars.DIG_CERT_ACTIVITY_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                initItemFragment(StaticVars.TITLE_SECTION_DIGITAL_CERTIFICATE- 1);
            }
        }
    }

    @Override
    public List<OpReturn> onOpReturnItemFragmentInteraction() {
        return listOpReturnJson.getOpReturnList();
    }

    @Override
    public void onSetOpReturnItemFragmentInteraction(List<OpReturn> opReturnList) {
        listOpReturnJson.setOpReturnList(opReturnList);
    }

    @Override
    public String onActionBarSetTitle() {
        if (loginJson != null){
            return loginJson.getUsername();
        } else {
            return null;
        }
    }

    //PUBLIC LIST CODE
    @Override
    public boolean onLogin() {
        return isLoggedin;
    }

    public void setVerifyNotarizationTx(Tx verifyNotarizationTx){
        this.verifyNotarizationTx = verifyNotarizationTx;
    }

}

