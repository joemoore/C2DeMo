package com.pivot13;

import android.content.Intent;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.EditText;
import com.google.inject.Inject;
import com.pivot13.test.support.RobolectricTestRunnerWithInjection;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.tester.org.apache.http.FakeHttpLayer;
import com.xtremelabs.robolectric.tester.org.apache.http.TestHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import roboguice.inject.InjectView;

import static com.pivotallabs.robolectricgem.expect.Expect.expect;

@RunWith(RobolectricTestRunnerWithInjection.class)
public class HomeActivityTest {
    @Inject
    HomeActivity activity;

    @InjectView(R.id.registerButton)
    Button registerButton;

    @InjectView(R.id.authWithGoogleButton)
    Button authWithGoogle;

    @InjectView(R.id.senderEmail)
    EditText senderEmail;

    @InjectView(R.id.senderPassword)
    EditText senderPassword;


    @Before
    public void setup() throws Exception {
        activity.onCreate(null);
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

    @Test
    public void shouldHaveAuthenticateWithGoogleButton() throws Exception {
        expect(authWithGoogle).toBeVisible();
    }

    @Test
    public void shouldHaveSenderEmail() throws Exception {
        expect(senderEmail).toBeVisible();
    }

    @Test
    public void shouldHaveSenderPassword() throws Exception {
        expect(senderPassword).toBeVisible();
    }

    @Test
    public void clickingAuthWithGoogleButton__shouldRegisterAsASenderWithGoogle() throws Exception {
        FakeHttpLayer.RequestMatcherBuilder builder = new FakeHttpLayer.RequestMatcherBuilder();
        //POST https://www.google.com/accounts/ClientLogin -d Email=<email> -d Passwd=<password> -d accountType=HOSTED_OR_GOOGLE -d service=ac2dm
        builder.host("www.google.com")
                .path("accounts/ClientLogin")
                .method(HttpPost.METHOD_NAME)
                .param("Email", "example@gmail.com")
                .param("Passwd", "password")
                .param("accountType", "HOSTED_OR_GOOGLE")
                .param("service", "ac2dm");

        Robolectric.getFakeHttpLayer().addHttpResponseRule(builder, new TestHttpResponse());

        senderEmail.setText("example@gmail.com");
        senderPassword.setText("password");
        Robolectric.clickOn(authWithGoogle);
        expect(Robolectric.getFakeHttpLayer()).toHaveMadeRequestMatching(builder);
    }
}
