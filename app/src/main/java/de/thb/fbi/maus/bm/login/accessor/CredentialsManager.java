package de.thb.fbi.maus.bm.login.accessor;

import android.util.Log;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Benedikt M.
 *
 * generateStrongPasswordHash(), getSalt() and toHex() addapted from
 * http://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#PBKDF2WithHmacSHA1
 *
 *
 * host: 54.202.56.214
 * port: 4300
 */
public class CredentialsManager {
    private int port;
    private String host;

    private final static int REQUEST_CREATION = 0;
    private final static int REQUEST_CHECK = 1;
    private final static int RESPONSE_CRED_ADDED = 2;

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
            OutputStream out = con.getOutputStream();

            //request credential creation
            out.write((REQUEST_CREATION + "\r\n").getBytes());
            out.flush();

            //send credentials to add
            out.write((email + "\r\n").getBytes());
            out.flush();
            out.write((password + "\r\n").getBytes());
            out.flush();

            if (Integer.parseInt(in.readLine()) == RESPONSE_CRED_ADDED)
                Log.i(logger, "credential-set created");

            con.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCredentials(String email, String password) {
        Log.i(logger, "checking credentials");

        Socket con = establishConnection();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            OutputStream out = con.getOutputStream();

            // status for check
            out.write((REQUEST_CHECK + "\r\n").getBytes());
            out.flush();

            //write email and password to check;
            out.write((email + "\r\n").getBytes());
            out.flush();
            out.write((password + "\r\n").getBytes());
            out.flush();

            if (Integer.parseInt(in.readLine()) == 0) {
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
        }

        return false;
    }

    public Socket establishConnection() {
        try {
            return new Socket(host, port);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}