package com.example.mank.cipher;

import android.util.Log;

public class MyCipher {

    final String key= "bnkama91211";
    final int kl = key.length();

    public MyCipher(){}


    public  String encrypt(int text1) {
        String text = String.valueOf(text1);
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                res.append("-01");
            } else {
                int x = c +key.charAt(j);
                if (x > 99) {
                    res.append(x);
                } else if (x >9) {
                    res.append("0"+x);
                } else if (x>-1) {
                    res.append("00"+x);
                } else {
                    Log.d("log-encryption-error", "encrypt: error in encryption");
                }
            }
            j = ++j % kl;
        }
        return res.toString();
    }
    public  String encrypt(float text1) {
        String text = String.valueOf(text1);
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                res.append(" ");
            } else {
                res.append((char) (c + key.charAt(j)));
            }
            j = ++j % kl;
        }
        return res.toString();
    }
    public  String encrypt(String text) {
        StringBuilder res = new StringBuilder();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                res.append("-01");
            } else {
                int x = c +key.charAt(j);
                if (x > 99) {
                    res.append(x);
                } else if (x >9) {
                    res.append("0"+x);
                } else if (x>-1) {
                    res.append("00"+x);
                } else {
                    Log.d("log-encryption-error", "encrypt: error in encryption");
                }
            }
            j = ++j % kl;
        }
        return res.toString();
    }

    public  String decrypt(String text) {
        StringBuilder res = new StringBuilder();
        String tmp = "";
        // text = text.toUpperCase();
        for (int i = 0, j = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '-') {
                res.append(" ");
            } else {
                tmp = ""+text.charAt(i) + text.charAt(i + 1) + text.charAt(i + 2);
                int x = Integer.parseInt(tmp) - key.charAt(j);
                res.append((char) ((x + 255) % 255));
            }
            i += 2;
            j = ++j % kl;
        }
        return res.toString();
    }


}
