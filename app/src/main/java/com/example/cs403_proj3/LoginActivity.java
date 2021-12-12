package com.example.cs403_proj3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    TextView txtUsername;
    TextView txtPassword;
    SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtUsername=findViewById(R.id.txtUsername);
        txtPassword=findViewById(R.id.txtPassword);
        this.sharedPref = getSharedPreferences("LOGIN_APP", Context.MODE_PRIVATE);
    }

    public void login(View view) {
        String user = this.txtUsername.getText().toString();
        String pw = this.txtPassword.getText().toString();
        this.txtPassword.setText("");
        LoginThread loginThread = new LoginThread(user, pw);
        loginThread.setAppContext(getApplicationContext());
        loginThread.sharedPref=this.sharedPref;
        loginThread.start();
        while(loginThread.getReturnCode()==-1) {
            Log.d("Info","Attempting to connect...");
        }
        loginThread.interrupt();
        if (loginThread.getReturnCode()==200) {
            Toast.makeText(getApplicationContext(), "login successful.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class); //TODO: replace this with whatever activity you want it to go to.
            startActivity(intent);

        }
        else if (loginThread.getReturnCode()== 400) {
            Toast.makeText(getApplicationContext(), "Invalid login.", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
        }
    }
}

class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params) throws Exception {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        if (resultString.length() > 0) {
            return resultString.substring(0, resultString.length() - 1);
        } else {
            return "";
        }
    }
}

class LoginThread extends Thread {
    final String WEBSITE_URL_GET_AUTH = "https://fast-ocean-54669.herokuapp.com/auth-token/";
    String user, pw;
    Context appContext;
    SharedPreferences sharedPref;
    int returnCode;
    public LoginThread(String user, String pass) {
        this.user=user;
        this.pw=pass;
        this.returnCode=-1;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }

    public int getReturnCode() {
        return this.returnCode;
    }

    public void run() {
        try {
            URL url = new URL(WEBSITE_URL_GET_AUTH);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            HashMap<String, String> params = new HashMap<>();
            params.put("username", user);
            params.put("password", pw);
            con.setDoOutput(true);
            con.setDoInput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());

            out.writeBytes(ParameterStringBuilder.getParamsString(params));
            out.flush();
            out.close();

            if (con.getResponseCode() == 200) {
                DataInputStream in = new DataInputStream(con.getInputStream());
                JsonParser jsonParser = new JsonParser();
                JsonElement el = jsonParser.parse(new InputStreamReader(in, "UTF-8"));
                JsonObject jsob = el.getAsJsonObject();
                String token = jsob.get("token").getAsString();
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putString("auth-token", token);
                prefEditor.putBoolean("login", false);
                prefEditor.apply();
                this.returnCode=200;
            } else if (con.getResponseCode() == 400) {
                this.returnCode=400;
            } else {
                this.returnCode=401;
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}