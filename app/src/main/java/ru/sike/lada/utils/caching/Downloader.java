package ru.sike.lada.utils.caching;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Downloader {

    public static byte[] Download(URL pUrl) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = pUrl.openStream ();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            e.printStackTrace ();
            baos.reset();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
        }
        return baos.toByteArray();
    }

    public static byte[] Download(String pUrl) {
        byte[] result = null;
        try {
            result = Download(new URL(pUrl.replace("https://", "http://")));
        } catch (MalformedURLException Ex) {
            Ex.printStackTrace();
        }
        return result;
    }

    public static String getMD5(byte[] input) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input);
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash)
                sb.append(String.format("%02x", b&0xff));
            result = sb.toString();
        } catch (NoSuchAlgorithmException Ex) {
            Ex.printStackTrace();
        }
        return result;
    }
}
