package com.xiaoeryu.rc4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.xiaoeryu.rc4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'rc4' library on application startup.
    static {
        System.loadLibrary("rc4");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        String key = "rc4test";
        String content = "0123456789";
        byte[] contentbyte = content.getBytes();
        RC4Encrypt(key, contentbyte);

        RC4Encrypt(key, contentbyte);

    }

    /**
     * A native method that is implemented by the 'rc4' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    void rc4_init(byte[] s, String key, int len){
        int i =0, j=0;
        byte[] k = new byte[256];
        byte[] keyarray = key.getBytes();
        byte tmp = 0;
        for (i = 0; i < 256; ++i) {
            s[i] = (byte) i;   // Initialization vectors
            k[i] = keyarray[i % len];    // Initialization a temporary vetor according to the key
        }
        for (i = 0; i < 256; ++i) {     // Scramble the state vector according to the key
            j = (j + s[i] + k[i]) % 256;
            tmp = s[i];
            s[i] = s[j];
            s[j] = tmp;
        }
    }
    void rc4_crypt(byte[] s, byte[] Data, int Len){
        int i = 0, j = 0, t = 0;
        int k = 0;
        byte tmp;
        for (k = 0; k < Len; ++k) {
            i = (i + 1) % 256;
            j = (j + (s[i]&0xff)) % 256;
            tmp = s[i];
            s[i] = s[j];
            s[j] = tmp;     // Obtain a new state vector according to the key
            t = ((s[i]&0xff) + (s[j]&0xff)) % 256;
            Data[k] ^= s[t];
        }
    }
    void RC4Encrypt(String key, byte[] content){
        // void rc4_init(String s, String key, int len)
        int Len = key.length();
        byte[] s = new byte[256];
        rc4_init(s, (String )key, Len);
        rc4_crypt(s, content, content.length);

    }
}