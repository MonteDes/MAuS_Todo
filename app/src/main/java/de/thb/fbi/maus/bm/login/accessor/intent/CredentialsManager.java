package de.thb.fbi.maus.bm.login.accessor.intent;

import android.util.Log;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Logger;

/**
 * @author Benedikt M.
 *
 * generateStrongPasswordHash(), getSalt() and toHex() addapted from
 * http://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#PBKDF2WithHmacSHA1
 *
 *
 * host: 34.212.28.35
 * port: 4300
 */
public class CredentialsManager {
    private int port;
    private String host;

    private final static int REQUEST_CREATION = 0;
    private final static int REQUEST_CHECK = 1;

    String logger = CredentialsManager.class.getName();

    public CredentialsManager(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void addCredentials(String email, String password) {
        Log.i(logger, "adding credentials");

        Socket con = establishConnection();
        try {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

        //request credential creation
        out.write(REQUEST_CREATION);

        //send credentials to add
        out.write(email);
        out.write(generateStrongPasswordHash(password));

        if(in.readLine().equals(-1))
            Log.i(logger, "credential-set created");

        con.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCredentials(String email, String password) {
        Log.i(logger, "checking credentials");

        Socket con = establishConnection();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

            // status for check
            out.write(REQUEST_CHECK);

            //write email and password to check;
            out.write(email);
            out.write(generateStrongPasswordHash(password));

            if(in.readLine().equals(0)) {
                con.close();
                Log.i(logger, "result: false");
                return false;
            } else {
                con.close();
                Log.i(logger, "result: true");
                return true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Socket establishConnection() {
        try {
            Socket con = new Socket(host, port);
            return con;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
}
