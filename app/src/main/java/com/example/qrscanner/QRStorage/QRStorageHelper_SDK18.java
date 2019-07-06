package com.example.qrscanner.QRStorage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.KeyChain;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.example.qrscanner.R;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class QRStorageHelper_SDK18 implements QRStorageInterface{
    private static final String LOG_TAG = QRStorageHelper.class.getSimpleName();
    private static final String KEY_ALGORITHM_RSA = "RSA";

    private static final String KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";

    private SharedPreferences preferences;
    private String alias = null;

    @Override
    public boolean init(Context context) {

        preferences = context.getSharedPreferences(ApplicationPreferences.PREFERENCES_FILE, Context.MODE_PRIVATE);
        alias = context.getString(R.string.app_package);
        KeyStore ks;

        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            //Use null to load Keystore with default parameters.
            ks.load(null);

            // Check if Private and Public already keys exists. If so we don't need to generate them again
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, null);
            if (privateKey != null && ks.getCertificate(alias) != null) {
                PublicKey publicKey = ks.getCertificate(alias).getPublicKey();
                if (publicKey != null) {
                    // All keys are available.
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }

        // Create a start and end time, for the validity range of the key pair that's about to be
        // generated.
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        end.add(Calendar.YEAR, 10);

        // Specify the parameters object which will be passed to KeyPairGenerator
        AlgorithmParameterSpec spec;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            spec = new android.security.KeyPairGeneratorSpec.Builder(context)
                    // Alias - is a key for your KeyPair, to obtain it from Keystore in future.
                    .setAlias(alias)
                    // The subject used for the self-signed certificate of the generated pair
                    .setSubject(new X500Principal("CN=" + alias))
                    // The serial number used for the self-signed certificate of the generated pair.
                    .setSerialNumber(BigInteger.valueOf(1337))
                    // Date range of validity for the generated pair.
                    .setStartDate(start.getTime()).setEndDate(end.getTime())
                    .build();
        } else {
            spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build();
        }

        // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
        // and the KeyStore. This example uses the AndroidKeyStore.
        KeyPairGenerator kpGenerator;
        try {
            kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            kpGenerator.initialize(spec);
            // Generate private/public keys
            kpGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            try {
                if (ks != null)
                    ks.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }

        // Check if device support Hardware-backed keystore
        try {
            boolean isHardwareBackedKeystoreSupported;
            if (android.os.Build.VERSION.SDK_INT < 23) {
                isHardwareBackedKeystoreSupported = KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA);
            } else {
                PrivateKey privateKey = (PrivateKey) ks.getKey(alias, null);
                KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA);
                KeyFactory keyFactory = KeyFactory.getInstance(privateKey.getAlgorithm(), "AndroidKeyStore");
                KeyInfo keyInfo = keyFactory.getKeySpec(privateKey, KeyInfo.class);
                isHardwareBackedKeystoreSupported = keyInfo.isInsideSecureHardware();
            }
            Log.d(LOG_TAG, "Hardware-Backed Keystore Supported: " + isHardwareBackedKeystoreSupported);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | InvalidKeySpecException | NoSuchProviderException e) {
        }

        return true;
    }

    @Override
    public void setData(String key, byte[] data) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

            ks.load(null);
            if (ks.getCertificate(alias) == null) return;

            PublicKey publicKey = ks.getCertificate(alias).getPublicKey();

            if (publicKey == null) {
                Log.d(LOG_TAG, "Error: Public key was not found in Keystore");
                return;
            }

            String value = encrypt(publicKey, data);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
                | InvalidKeySpecException | KeyStoreException | CertificateException | IOException e) {
            try {
                if (ks != null)
                    ks.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
    }

    @Override
    public byte[] getData(String key) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            ks.load(null);
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, null);
            return decrypt(privateKey, preferences.getString(key, null));
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | UnrecoverableEntryException | InvalidKeyException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException e) {
            try {
                if (ks != null)
                    ks.deleteEntry(alias);
            } catch (Exception e1) {
                // Just ignore any errors here
            }
        }
        return null;
    }

    @Override
    public void remove(String key) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    @SuppressLint("TrulyRandom")
    private static String encrypt(PublicKey encryptionKey, byte[] data) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, InvalidKeySpecException {

        Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        byte[] encrypted = cipher.doFinal(data);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    private static byte[] decrypt(PrivateKey decryptionKey, String encryptedData) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException {
        if (encryptedData == null)
            return null;
        byte[] encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
        return cipher.doFinal(encryptedBuffer);
    }

}
