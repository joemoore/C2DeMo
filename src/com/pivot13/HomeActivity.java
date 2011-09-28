package com.pivot13;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.c2dm.C2DMessaging;
import com.google.inject.Inject;
import com.pivot13.util.CurrentTime;
import com.pivotallabs.api.Http;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import roboguice.util.Strings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HomeActivity extends RoboActivity {
    @InjectView(R.id.registerButton)
    Button registerButton;

    @InjectView(R.id.authWithGoogleButton)
    Button authWithGoogleButton;

    @InjectView(R.id.sendNotification)
    Button sendNotification;

    @InjectView(R.id.senderEmail)
    EditText senderEmail;

    @InjectView(R.id.notificationText)
    EditText notificationText;

    @InjectView(R.id.senderPassword)
    EditText senderPassword;

    @Inject
    private CurrentTime currentTime;

    @Inject
    private ExecutorService executorService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        registerButton.setOnClickListener(new RegisterOnClickListener());
        authWithGoogleButton.setOnClickListener(new AuthenticateWithGoogleListener());
        sendNotification.setOnClickListener(new SendNotificationListener());
    }

    private class AuthenticateWithGoogleListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            List<String> strings = Arrays.asList(
                    "Email=" + senderEmail.getText().toString(),
                    "Passwd=" + senderPassword.getText().toString(),
                    "accountType=HOSTED_OR_GOOGLE",
                    "service=ac2dm");

            String paramsAsBody = Strings.join("&", strings);
            Map<String, String> headers = new HashMap<String, String>();
            Http.Response response = doPost(paramsAsBody, headers, "https://www.google.com/accounts/ClientLogin");

            String responseBody = response.getResponseBody();
            String[] responsePieces = responseBody.split("Auth=");
            if (responsePieces.length == 2) {
                String authToken = responsePieces[1];
                saveAuthToken(authToken);
                toast("Authorization Success!");
                Log.v("pivot13", "Auth Token is =" + authToken);
            } else {
                toast("Auth Failed: " + responseBody);
            }
        }

        private void saveAuthToken(String authToken) {
            SharedPreferences.Editor editor = getSharedPreferences("C2DM", 0).edit();
            editor.putString("AuthToken", authToken.trim());
            editor.commit();
        }
    }

    private class RegisterOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            C2DMessaging.register(HomeActivity.this, C2DeMoApplication.C2DM_SENDER_KEY);
        }
    }

    private class SendNotificationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences = getSharedPreferences("C2DM", 0);
            String authToken = sharedPreferences.getString("AuthToken", "");
            String regId = sharedPreferences.getString(C2DMReceiver.REG_ID, "");

            List<String> strings = Arrays.asList(
                    "data.message=" + notificationText.getText().toString(),
                    "registration_id=" + regId,
                    "collapse_key=something");

            String paramsAsBody = Strings.join("&", strings);

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "GoogleLogin auth=" + authToken);
            Http.Response response = doPost(paramsAsBody, headers, "https://android.apis.google.com/c2dm/send");
            if(response.getStatusCode() != 200) {
                toast("Notification failed: " + response.getResponseBody());
            }
        }
    }

    private Http.Response doPost(String paramsAsBody, Map<String, String> headers, String url) {
        Http.Response post = null;
        try {
            headers.put("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            post = new Http().post(url, headers, paramsAsBody);
            Log.v("pivot13", "response for " + url + " was \n" + post.getResponseBody());
        } catch (IOException e) {
            toast("Exception: " + e.getMessage());
        } catch (URISyntaxException e) {
            toast("Exception: " + e.getMessage());
        }
        return post;
    }

    private void toast(String text) {
        Toast.makeText(HomeActivity.this, text, Toast.LENGTH_LONG).show();
    }
}
