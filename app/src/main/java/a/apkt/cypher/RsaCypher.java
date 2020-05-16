package a.apkt.cypher;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaCypher {
    private static final String PUBLIC_KEY_FILE = "brew";
    private static final String PRIVATE_KEY_FILE = "malt";

    public static void main(String[] args) throws IOException {

        try {
//            System.out.println("-------GENRATE PUBLIC and PRIVATE KEY-------------");
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(4096); //1024 used for normal securities
//            KeyPair keyPair = keyPairGenerator.generateKeyPair();
//            PublicKey publicKey = keyPair.getPublic();
//            PrivateKey privateKey = keyPair.getPrivate();
//            System.out.println("\n------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------\n");
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
//            RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
//
//            System.out.println("\n--------SAVING PUBLIC KEY AND PRIVATE KEY TO FILES-------\n");
//
            RsaCypher rsaObj = new RsaCypher();
//
//            rsaObj.saveKeys(PUBLIC_KEY_FILE, rsaPubKeySpec.getModulus(), rsaPubKeySpec.getPublicExponent());
//            rsaObj.saveKeys(PRIVATE_KEY_FILE, rsaPrivKeySpec.getModulus(), rsaPrivKeySpec.getPrivateExponent());

            //Encrypt Data using Public Key
//            byte[] encryptedData = rsaObj.encryptData(activity, "Hello java programmers!!!");

            //Descypt Data using Private Key
//            rsaObj.decryptData(encryptedData);

//        } catch (InvalidKeySpecException ex) {
//            Logger.getLogger(Rsa.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (NoSuchAlgorithmException e) {
//            System.out.println(e);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static List<BigInteger> genKeys(Activity activity){
        try{
            //-------GENRATE PUBLIC and PRIVATE KEY-------------
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096); //1024 used for normal securities
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            // ------- PULLING OUT PARAMETERS WHICH MAKES KEYPAIR----------
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec rsaPubKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            RSAPrivateKeySpec rsaPrivKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

            FileOutputStream fos = activity.openFileOutput("RSAPrivateKeySpec", Context.MODE_PRIVATE);
            fos.write(rsaPrivKeySpec.toString().getBytes());
            fos.close();

            List<BigInteger> list = new ArrayList<>();
            list.add(0, rsaPubKeySpec.getModulus());
            list.add(1, rsaPubKeySpec.getPublicExponent());

            return list;
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }

    private void saveKeys(String fileName, BigInteger mod, BigInteger exp) throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            System.out.println("Generating " + fileName + "...");
            fos = new FileOutputStream("/usr/" + fileName);
            System.out.println("path: " + new File(fileName).getAbsolutePath());
            oos = new ObjectOutputStream(new BufferedOutputStream(fos));
            oos.writeObject(mod);
            oos.writeObject(exp);
            System.out.println(fileName + " generated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                oos.close();
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    public static byte[] encryptData(Activity activity, String data) throws IOException {
//        System.out.println("\n----------------ENCRYPTION STARTED------------");
//        System.out.println("Data Before Encryption :" + data);
        byte[] dataToEncrypt = data.getBytes();
        byte[] encryptedData = null;
        try {
            PublicKey pubKey = readPublicKeyFromFile(activity, PUBLIC_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            encryptedData = cipher.doFinal(dataToEncrypt);

//            System.out.println("Encryted Data: " + new String(encryptedData));


        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("----------------ENCRYPTION COMPLETED------------");
        return encryptedData;
    }

    public static byte[] decryptData(byte[] data) throws IOException {
//        System.out.println("\n----------------DECRYPTION STARTED------------");
        byte[] descryptedData = null;
        try {
            PrivateKey privateKey = readPrivateKeyFromFile(PRIVATE_KEY_FILE);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            descryptedData = cipher.doFinal(data);
            System.out.println("Decrypted Data: " + new String(descryptedData));
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("----------------DECRYPTION COMPLETED------------");
        return descryptedData;
    }

    private static PublicKey readPublicKeyFromFile(Activity activity, String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            InputStream caInput = activity.getResources().getAssets().open("brew");
//            fis = (FileInputStream) activity.getResources().getAssets().open("brew");
            ois = new ObjectInputStream(caInput);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();
            //Get Public Key
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey publicKey = fact.generatePublic(rsaPublicKeySpec);
            return publicKey;
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return null;
    }

    private static PrivateKey readPrivateKeyFromFile(String fileName) throws IOException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(new File("/usr/" + fileName));
            ois = new ObjectInputStream(fis);
            BigInteger modulus = (BigInteger) ois.readObject();
            BigInteger exponent = (BigInteger) ois.readObject();
            //Get Private Key
            RSAPrivateKeySpec rsaPrivateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(rsaPrivateKeySpec);
            return privateKey;
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RsaCypher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                ois.close();
                if (fis != null) {
                    fis.close();
                }
            }
        }
        return null;
    }
}