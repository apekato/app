package a.apkt.cypher;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class AesCypher {
    private static final String ALGO = "AES";

    private static String encrypt(byte[] keyValue, String Data) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    private static String decrypt(byte[] keyValue, String encryptedData) throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static byte[] setKeyValue() throws NoSuchAlgorithmException{
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(128);
        SecretKey k = kg.generateKey();
        return k.getEncoded();
    }

//    public static String packData(
//            AuthAux authAux,
//            Object object
//    ) {
//        try {
//            authAux.setObject(object);
//            keyValue = setKeyValue();
//            CypherJson cypherJson = new CypherJson();
//            authAux.setAesKey(keyValue);
//            String encdata = encrypt(new Gson().toJson(authAux));
//            return encdata;
//        } catch (Exception ex) {
//            Logger.getLogger(AesCypher.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    public static Object unpackData(byte[] aesKeyValue, byte[] b) {
        try {

            String decdata = decrypt(aesKeyValue, new String(b));
            return decdata;
//            AuthAux authAux = new Gson().fromJson(new String(b), AuthAux.class);
//            TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
//            LoginSqlite loginSqlite = new LoginSqlite(activity);
//            LoginBB loginBB = loginSqlite.getUserLogin();
//            String mSimSerialNum;
//            if (ProjService.PROJMODE.equals(ProjService.Mode.HOMOLOG)){
//                if (ProjService.homologEmail.equals(loginBB.getLoginAux().getEmail())){
//                    mSimSerialNum = ProjService.homologSimSerialNum;
//                }else{
//                    mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//                }
//            }else{
//                mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//            }
//            if (authAux.getSimSerialNumber().equals(mSimSerialNum)
//                    && authAux.getSimOperator().equals(mTelephonyMgr.getSimOperator())
//                    && authAux.getNetworkCountryIso().equals(mTelephonyMgr.getSimOperator())
//                    && authAux.getSubscriberId().equals(mTelephonyMgr.getSubscriberId())){
//                String gson = new Gson().toJson(authAux.getObject());
//                return gson;
//            }
//
//
//            AuthAux authAux = new Gson().fromJson(new String(b), AuthAux.class);
//            TelephonyManager mTelephonyMgr = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
//            LoginSqlite loginSqlite = new LoginSqlite(activity);
//            LoginBB loginBB = loginSqlite.getUserLogin();
//            String mSimSerialNum;
//            if (ProjService.PROJMODE.equals(ProjService.Mode.HOMOLOG)){
//                if (ProjService.homologEmail.equals(loginBB.getLoginAux().getEmail())){
//                    mSimSerialNum = ProjService.homologSimSerialNum;
//                }else{
//                    mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//                }
//            }else{
//                mSimSerialNum = mTelephonyMgr.getSimSerialNumber();
//            }
//            if (authAux.getSimSerialNumber().equals(mSimSerialNum)
//                    && authAux.getSimOperator().equals(mTelephonyMgr.getSimOperator())
//                    && authAux.getNetworkCountryIso().equals(mTelephonyMgr.getSimOperator())
//                    && authAux.getSubscriberId().equals(mTelephonyMgr.getSubscriberId())){
//                String gson = new Gson().toJson(authAux.getObject());
//                return gson;
//            }
        } catch (Exception ex) {
            Logger.getLogger(AesCypher.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}