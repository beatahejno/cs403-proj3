package com.example.cs403_proj3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    final String WEBSITE_URL_GET_AUTH = "https://fast-ocean-54669.herokuapp.com/auth-token/";
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
        try {
            URL url = new URL(WEBSITE_URL_GET_AUTH);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            HashMap<String, String> params = new HashMap<>();
            params.put("username", user);
            params.put("password", pw);
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            DataInputStream in = new DataInputStream(con.getInputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(params));
            out.flush();
            out.close();
            if (con.getResponseCode()==200) {
                JsonParser jsonParser = new JsonParser();
                JsonElement el =  jsonParser.parse(new InputStreamReader(in, "UTF-8"));
                JsonObject jsob = el.getAsJsonObject();
                String token = jsob.get("auth-token").getAsString();
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putString("auth-token", token);
                prefEditor.apply();
                Toast.makeText(getApplicationContext(),"login successful.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MainActivity.class); //TODO: replace this with whatever activity you want it to go to.
                startActivity(intent);

            } else if (con.getResponseCode()==400) {
                Toast.makeText(getApplicationContext(),"Invalid login.",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"An error occurred. Please try again.",Toast.LENGTH_LONG).show();
            }

        } catch(Exception e) {

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