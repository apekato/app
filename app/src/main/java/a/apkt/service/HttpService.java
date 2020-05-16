package a.apkt.service;

import android.app.Activity;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HttpService {

    public static InputStreamReader post(Activity activity, String wsName, String gsonString) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        InputStreamReader reader = null;

        if (ProjService.URLTYPE.equals(ProjService.Url.HTTPS)){
            reader = postHttps(activity, wsName, gsonString);
        }else if (ProjService.URLTYPE.equals(ProjService.Url.HTTP)) {
            reader = postHttp(wsName, gsonString);
        }
        return reader;
    }

    private static InputStreamReader postHttps(Activity activity, String wsName, String gsonString) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException{

        Resources resources = activity.getResources();

        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = activity.getResources().getAssets().open("server.cer");
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        String url_ = ProjService.Url.HTTPS.concat(wsName);

        URL url = new URL(url_);
        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());

        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");

        OutputStream os = urlConnection.getOutputStream();
        os.write(gsonString.getBytes());
        os.flush();


        InputStream in = urlConnection.getInputStream();
        InputStreamReader reader =
                new InputStreamReader(in);

        return reader;
    }

    public static InputStreamReader postHttp(String wsName, String gsonString) throws IOException {
        try{
            String url_ = ProjService.Url.HTTP.concat(wsName);
            URL url = new URL(url_);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");

            OutputStream os = urlConnection.getOutputStream();
            os.write(gsonString.getBytes());
            os.flush();

            InputStream in = urlConnection.getInputStream();
            InputStreamReader reader =
                    new InputStreamReader(in);

            return reader;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public static InputStreamReader postByteHttp(String wsName, byte[] bytes) throws IOException {

        String url_ = ProjService.Url.HTTP.concat(wsName);

        URL url = new URL(url_);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");

        OutputStream os = urlConnection.getOutputStream();
        os.write(bytes);
        os.flush();

        InputStream in = urlConnection.getInputStream();
        InputStreamReader reader =
                new InputStreamReader(in);

        return reader;
    }

    public static InputStreamReader get(Activity activity, String wsName, String gsonString) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        InputStreamReader reader = null;

        if (ProjService.URLTYPE.equals(ProjService.Url.HTTPS)){
            reader = getHttps(activity, wsName, gsonString);
        }else if (ProjService.URLTYPE.equals(ProjService.Url.HTTP)) {
            reader = getHttp(wsName, gsonString);
        }
        return reader;
    }

    private static InputStreamReader getHttps(Activity activity, String wsName, String gsonString) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException{

        Resources resources = activity.getResources();

        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        InputStream caInput = activity.getResources().getAssets().open("server.cer");
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        String url_ = ProjService.Url.HTTPS.concat(wsName).concat("/").concat(gsonString);

        URL url = new URL(url_);
        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
        urlConnection.setSSLSocketFactory(context.getSocketFactory());

        InputStream in = urlConnection.getInputStream();
        InputStreamReader reader =
                new InputStreamReader(in);

        return reader;
    }

    private static InputStreamReader getHttp(String wsName, String gsonString) throws IOException {

        String url_ = ProjService.Url.HTTP.concat(wsName).concat("/").concat(gsonString);

        URL url = new URL(url_);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

        InputStream in = urlConnection.getInputStream();
        InputStreamReader reader =
                new InputStreamReader(in);

        return reader;
    }

}
