package com.irineu.nodebr61;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("nodebr61-lib");
    }

    LinearLayout layout;
    Button btnPasteValid;
    Button btnActivate;
    TextView txtName;
    TextView txtEmail;
    TextView txtKey;
    CryptoUtils cryptoUtils = new CryptoUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPasteValid = findViewById(R.id.btnPasteValid);
        btnActivate = findViewById(R.id.btnActivate);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtKey = findViewById(R.id.txtKey);

        layout = (LinearLayout)findViewById(R.id.myLinearLayout);

        boolean rootDetected = false;

        if(!rootDetected && checkRoot()){
            Snackbar.make(layout, "Root Detectado (Java)", Snackbar.LENGTH_SHORT).show();
            rootDetected = true;
        }

        if(!rootDetected && nativeCheckRoot()){
            Snackbar.make(layout, "Root Detectado (C++)", Snackbar.LENGTH_SHORT).show();
            rootDetected = true;
        }

        if(rootDetected){
            exitIn(5);
            return;
        }

        btnActivate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        hideKb();

                        try {
                            String validKey = generateSerial();
                            String typedKey = txtKey.getText().toString();

                            if(!typedKey.equalsIgnoreCase(validKey)){
                                Snackbar.make(layout, "Invalid Product Key", Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            String response = cryptoUtils.checkRemoteKey(generateValidationPayload());
                            Map<String, String> data = new Gson().fromJson(response, Map.class);

                            if(!data.containsKey("status") || !data.get("status").equals("ok")){
                                Snackbar.make(layout, "Invalid Product Key", Snackbar.LENGTH_SHORT).show();
                                return;
                            }

                            openSecretActivity();

                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            Snackbar.make(layout, "Invalid Product Key", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }


        });

        btnPasteValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        hideKb();

                        try {
                            txtKey.setText(generateSerial());
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    public void openSecretActivity() {
        Intent intent = new Intent(MainActivity.this, ProtectedActivity.class);
        startActivity(intent);
    }

    private void hideKb() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
    }

    private void exitIn(int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(i * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.exit(1);
            }
        }).start();
    }

    private String generateSerial() throws GeneralSecurityException, IOException {
        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        Map<String, Object> postData = Map.of("name", name, "email", email);

        String payload = new Gson().toJson(postData);

        byte [] digest = cryptoUtils.enc(payload);

        return CryptoUtils.bytesToHex(digest);
    }

    private String generateValidationPayload() throws GeneralSecurityException, IOException {
        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String key = txtKey.getText().toString();
        Map<String, Object> postData = Map.of("name", name, "email", email, "key", key);

       return new Gson().toJson(postData);
    }

    public native boolean nativeCheckRoot();

    public boolean checkRoot(){
        for(String pathDir : System.getenv("PATH").split(":")){
            if(new File(pathDir, "su").exists()) {
                return true;
            }
        }
        return false;
    }
}