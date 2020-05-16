package a.apkt.asynctask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ScrollView;

import org.demoiselle.signer.policy.impl.cades.pkcs7.PKCS7Signer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.List;

import a.apkt.MainActivity;
import a.apkt.R;
import a.apkt.model.DigCert;
import a.apkt.service.StaticVars;
import a.apkt.service.UserMsgService;
import a.apkt.service.UtilsService;
import a.apkt.signer.KeyStorePKCS12;

public class SignerAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private boolean glassfishDown = false;
    private MainActivity mainActivity;
    private Uri uri;
    private String pwd;
    private View mProgressView;
    private ScrollView scrollView;

    public SignerAsyncTask (
            View mProgressView,
            ScrollView scrollView,
            MainActivity mainActivity,
            Uri uri,
            String pwd){
        this.mainActivity = mainActivity;
        this.uri = uri;
        this.pwd = pwd;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {

            List<DigCert> digCertList = UtilsService.getDigCertList(mainActivity);
            DigCert digCert = digCertList.get(0);
            byte[] digCertBytes = digCert.getDigCertBytes();
            InputStream digCertInputStream = UtilsService.convertBytesToInputStream(digCertBytes);
            KeyStore keyStore = KeyStorePKCS12.loadKeystore(mainActivity, digCertInputStream, pwd);
//            KeyStore keyStore = KeyStorePKCS12.loadKeystore(mainActivity, digCertInputStream, "17hinP");
            PrivateKey privateKey = KeyStorePKCS12.loadPrivKey(keyStore);
            PKCS7Signer signer = KeyStorePKCS12.signer(keyStore, privateKey);

            InputStream inputStream = mainActivity.getContentResolver().openInputStream(uri);
            byte[] fileBytes = UtilsService.convertInputStreamToBytes(inputStream);
            byte[] fileSignedBytes = signer.doDetachedSign(fileBytes);

            // get filename from uri
//            String fileName = uri.getLastPathSegment().substring(uri.getLastPathSegment().lastIndexOf("/") + 1);





            String fileName = UtilsService.retrieveFileName(mainActivity, uri);

            // create directory before attempting to a save file in a new directory
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + mainActivity.getString(R.string.app_name));
            directory.mkdirs();

            // save signed file to directory
            String fileDirectorySignedPath = Environment.getExternalStorageDirectory() +
                    File.separator + mainActivity.getString(R.string.app_name) + File.separator + fileName + "signedDettached_" + ".p7s";
            UtilsService.savefileExternalStorage(fileSignedBytes, fileDirectorySignedPath);

            // save original file to directory
//            String fileDirectoryPath = Environment.getExternalStorageDirectory() +
//                    File.separator + mainActivity.getString(R.string.app_name) + File.separator + fileName;
//            UtilsService.savefileExternalStorage(fileBytes, fileDirectoryPath);



//            // save signed file to directory
//            String fileDirectorySignedPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                    File.separator + fileName + "_docAssinado" + ".p7s";
//
////            // save signed file to directory
////            String fileDirectorySignedPath = Environment.getExternalStorageDirectory() +
////                    File.separator + mainActivity.getString(R.string.app_name) + File.separator + fileName + "signedDettached_" + ".p7s";
//
//
//            UtilsService.savefileExternalStorage(fileSignedBytes, fileDirectorySignedPath);
//
//            // save original file to directory
//            String fileDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
//                    File.separator + fileName + "_docOriginal";
//            UtilsService.savefileExternalStorage(fileBytes, fileDirectoryPath);

//            UtilsService.valSignPKCS7CAdESDettached(fileSignedBytes, fileBytes);

        } catch (ConnectException exception) {

            glassfishDown = true;
            return false;

        }catch (CursorIndexOutOfBoundsException e) {
            e.getMessage();
        }
        catch (Exception e) {
            e.getMessage();
            glassfishDown = true;
            return false;
        }

//        List<String> certInfoList = KeyStorePKCS12.certInfo(KeyStorePKCS12.certicateChain(keyStore));
//
//        PrivateKey chavePrivada = KeyStorePKCS12.loadPrivKey(keyStore);
//
//        PKCS7Signer signer = KeyStorePKCS12.signer(keyStore, chavePrivada);
//
//        // code block for signing docs
//        //*************************************************************
//        try {
//
//            File fileSign = new File(Environment.getExternalStorageDirectory() +
//                    File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + "encryption.jpg");
//            Uri uriFileSign = Uri.fromFile(fileSign);
//            InputStream is = activity.getContentResolver().openInputStream(uriFileSign);
//            int fileSize = is.available();
//            byte[] resultFileSign = new byte[fileSize];
//            is.read(resultFileSign);
//            is.close();
//
//            // line of code needed in asynctask
//            byte[] signature = signer.doDetachedSign(resultFileSign);
//
//            OutputStream outFileSign = new FileOutputStream(Environment.getExternalStorageDirectory() +
//                    File.separator + activity.getString(R.string.app_name) + File.separator + "DIRECTORY_DOWNLOADS.p7s");
//
//            outFileSign.write(signature);
//            outFileSign.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //****************************************************************************

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            mainActivity.initItemFragment(StaticVars.TITLE_SECTION_DIGITAL_SIGNATURE - 1);
        }
        else if (!success) {
            if (glassfishDown) {
                UserMsgService.showDialogPositButtonMail(mainActivity,
                        mainActivity, R.string.alertdialog_internal_problem_title,
                        R.string.alertdialog_internal_problem_msg);
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
            int shortAnimTime = mainActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);

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
