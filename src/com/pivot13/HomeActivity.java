package com.pivot13;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.c2dm.C2DMessaging;
import com.google.inject.Inject;
import com.pivot13.util.CurrentTime;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class HomeActivity extends RoboActivity {
    @InjectView(R.id.registerButton)
    Button registerButton;

    @Inject
    private CurrentTime currentTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        registerButton.setOnClickListener(new RegisterOnClickListener());


    }

    private class RegisterOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            C2DMessaging.register(HomeActivity.this, C2DeMoApplication.C2DM_SENDER_KEY);
        }
    }
}
