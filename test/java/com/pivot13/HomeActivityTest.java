package com.pivot13;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.TextView;
import com.google.inject.Inject;
import com.pivot13.test.support.RobolectricTestRunnerWithInjection;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.inject.InjectView;

import static com.pivotallabs.robolectricgem.expect.Expect.expect;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunnerWithInjection.class)
public class HomeActivityTest {
    @Inject
    HomeActivity activity;

    @InjectView(R.id.registerButton)
    Button registerButton;


    @Before
    public void setup() throws Exception {
        activity.onCreate(null);
    }

    @Test
    public void shouldHaveATitle() {
        final HomeActivity activity = new HomeActivity();
        activity.onCreate(null);
        TextView title = (TextView) activity.findViewById(R.id.title);
        assertEquals("C2DeMo", title.getText());
    }

    @Test
    public void shouldHaveARegisterButton() throws Exception {
        expect(registerButton).toBeVisible();
    }

    @Test
    public void clickingRegisterButton__shouldRegisterWithC2DMService() throws Exception {
        expect(Robolectric.getShadowApplication().peekNextStartedService()).toBeNull();
        Robolectric.clickOn(registerButton);
        Intent intent = Robolectric.getShadowApplication().peekNextStartedService();
        expect(intent.<Parcelable>getParcelableExtra("app")).not.toBeNull();
        expect(intent.getStringExtra("sender")).toEqual(C2DeMoApplication.C2DM_SENDER_KEY);
        expect(intent.getAction()).toEqual("com.google.android.c2dm.intent.REGISTER");
    }
}
