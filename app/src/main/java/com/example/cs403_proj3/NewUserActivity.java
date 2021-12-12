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

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NewUserActivity extends AppCompatActivity {
    TextView txtUsername, txtPass, txtPass2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        this.txtUsername=findViewById(R.id.txtNewUsername);
        this.txtPass=findViewById(R.id.txtPassNew);
        this.txtPass2=findViewById(R.id.txtConfirmPass);
    }

    public void createUser(View view) {
        String user = txtUsername.getText().toString();
        String pass = txtPass.getText().toString();
        String pass2 = txtPass2.getText().toString();
        if (!pass.equals(pass2)) {
            txtPass.setText("");
            txtPass2.setText("");
            Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_LONG).show();
            return;
        }
        NewUserThread nut = new NewUserThread(user, pass);
        nut.setAppContext(getApplicationContext());
        nut.start();
        while(nut.returnCode==-1) {
            Log.d("Response code", "trying not to die");
        }
        nut.interrupt();
        if (nut.getReturnCode()==201) {
            Toast.makeText(getApplicationContext(), "User successfully created. Please log in.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class); //TODO: replace this with whatever activity you want it to go to.
            startActivity(intent);
            finish();
            return;
        }
        else if (nut.getReturnCode()== 400) {
            Toast.makeText(getApplicationContext(), "Invalid login credentials. Either the username is already in use or the username/password is in an invalid format.", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "An error occurred. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }
    }
}
class NewUserThread extends Thread {
    final String WEBSITE_URL_NEW_USER = "https://fast-ocean-54669.herokuapp.com/users/";
    String user, pw;
    Context appContext;
    SharedPreferences sharedPref;
    int returnCode;
    public NewUserThread(String user, String pass) {
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
            URL url = new URL(WEBSITE_URL_NEW_USER);
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
            Log.d("Response code", ""+con.getResponseCode());
            if (con.getResponseCode() == 201) {
               this.returnCode=201;
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