package com.pivot13;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowNotification;
import com.xtremelabs.robolectric.shadows.ShadowNotificationManager;
import com.xtremelabs.robolectric.shadows.ShadowToast;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.pivotallabs.robolectricgem.expect.Expect.expect;
import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class C2DMReceiverTest {
    private C2DMReceiver c2DMReceiver;

    @Before
    public void setup() {
        c2DMReceiver = new C2DMReceiver();
    }

    @Test
    public void whenC2DMMessageReceived__shouldUpdateNotificationBar() throws Exception {
        Intent intent = new Intent()
                .putExtra("message", "Main Message Here")
                .putExtra("moreData", "extra data here");
        c2DMReceiver.onMessage(Robolectric.application, intent);

        NotificationManager notificationManager =
                (NotificationManager) Robolectric.application.getSystemService(Context.NOTIFICATION_SERVICE);

        ShadowNotificationManager shadowNotificationManager = shadowOf(notificationManager);
        expect(shadowNotificationManager.size()).toEqual(1);

        ShadowNotification notification = shadowOf(shadowNotificationManager.getNotification(C2DMReceiver.MESSAGE_KEY));
        expect(notification.getLatestEventInfo().getContentTitle()).toEqual("C2DeMo Message");
        expect(notification.getLatestEventInfo().getContentText().toString()).toContain("message: Main Message Here");
        expect(notification.getLatestEventInfo().getContentText().toString()).toContain("moreData: extra data here");
    }

    @Test
    public void successfulRegistration__shouldStoreRegId() throws Exception {
        c2DMReceiver.onRegistered(Robolectric.application, "testRegId");
        SharedPreferences sharedPreferences = Robolectric.application.getSharedPreferences("C2DM", 0);
        expect(sharedPreferences.getString(C2DMReceiver.REG_ID, null)).toEqual("testRegId");
    }

    @Test
    public void successfulRegistration__shouldToastSuccess() throws Exception {
        c2DMReceiver.onRegistered(Robolectric.application, "testRegId");
        expect(ShadowToast.showedToast(C2DMReceiver.REGISTRATION_SUCCESS_MESSAGE)).toBeTrue();
    }

    @Test
    public void successfulRegistration__shouldReplaceExistingRegId() throws Exception {
        c2DMReceiver.onRegistered(Robolectric.application, "testRegId");
        assertTrue("Implement your own tests or delete this", true);
    }

    @Test
    public void failedRegistration__shouldToastReason() throws Exception {
        c2DMReceiver.onError(Robolectric.application, C2DMReceiver.ERR_ACCOUNT_MISSING);
        expect(ShadowToast.showedToast(C2DMReceiver.REGISTRATION_ERROR_MESSAGE + ": " + C2DMReceiver.ERR_ACCOUNT_MISSING)).toBeTrue();
    }

    @Test
    public void testOnUnregistered() throws Exception {
        c2DMReceiver.onUnregistered(Robolectric.application);
        assertTrue("Implement your own tests or delete this", true);
    }
}
