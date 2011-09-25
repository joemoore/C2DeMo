package com.pivot13;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.google.android.c2dm.C2DMBaseReceiver;

import java.io.IOException;

/**
 * NOTE: C2DM is supported in API v.2.2 and above but is safely ignored in lower versions.
 *
 * C2DMBaseReceiver.runIntentInService demands that this class exist at the same package
 * defined in the AndroidManifest.package; ex: com.pivot13.C2DMReceiver
 *
 * Read and understand http://code.google.com/android/c2dm/, but note that the XML listed in the Manifest section
 * is wrong!  Use the pattern you see in AndroidManifest.xml of this project instead.
 */
public class C2DMReceiver extends C2DMBaseReceiver {

    public static final int MESSAGE_KEY = 1;
    public static final String REG_ID = "regId";
    public static final CharSequence REGISTRATION_SUCCESS_MESSAGE = "C2DM Registration Successful!";
    public static final CharSequence REGISTRATION_ERROR_MESSAGE = "C2DM Registration Error: ";


    /**
     * The senderId is a google email address, such as a gmail address or a apps-for-your-domain gmail address.
     * Use the same address when registering a device with C2DM.
     */
    public C2DMReceiver() {
        super(C2DeMoApplication.C2DM_SENDER_KEY);
    }

    /**
     * Called when a C2DM message is received.
     * context: a Context
     * intent: this is the Intent broadcast by Android.  This Intent has extras with the format described
     * at http://code.google.com/android/c2dm/#push.
     *
     * Getting data from the intent: if a C2DM message sent by a server with params "data.myInfo"
     * retrieve that data by calling intent.getExtras().getString("myInfo") -- *not* "data.myInfo"
     *
     * Setting Notifications in the Notification Bar: this is done by passing PendingIntents to a NotificationManager.
     * See http://developer.android.com/reference/android/app/PendingIntent.html
     *
     * Handling the "Clear All" button in the Notification Bar: set PendingIntent.deleteIntent. This will be called
     * automatically by Android. You will need to handle the broadcast of that Intent yourself.
     */
    @Override
    protected void onMessage(Context context, Intent intent) {

        String message = intent.getStringExtra("message");
        String moreData = intent.getStringExtra("moreData");
        String notificationText = new StringBuilder()
                .append("message: ").append(message).append("; ")
                .append("moreData: ").append(moreData).toString();


        Notification notification = new Notification(android.R.drawable.ic_dialog_alert, "Ticker Ticker ticker!! " + notificationText, SystemClock.currentThreadTimeMillis());

        notification.setLatestEventInfo(context, "C2DeMo Message", notificationText, PendingIntent.getBroadcast(context, 0, new Intent(), 0));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(MESSAGE_KEY, notification);
        Log.v("pivot13", "************ about to notify!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    /**
     * @param context
     * @param registrationId - key Google uses to identify this device.
     * @throws IOException
     *
     * See http://code.google.com/android/c2dm/#handling_reg
     *
     * Overview: You will need to broadcast an Intent to register with the C2DM service; for example, when a
     * user logs in to the application -- see http://code.google.com/android/c2dm/#registering.  This method is
     * called if that registration succeeds.
     *
     * Warning: Failure to unregister a device before re-registering might result in devices with multiple
     * registration_ids and might receiving duplicate notifications.
     */
    @Override
    public void onRegistered(final Context context, String registrationId) throws IOException {
        super.onRegistered(context, registrationId);
        SharedPreferences.Editor editor = getSharedPreferences("C2DM", 0).edit();
        editor.putString(REG_ID, registrationId);
        editor.commit();
        Log.v("pivot13", registrationId);
        
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, REGISTRATION_SUCCESS_MESSAGE, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param context
     * @param errorId one of C2DMBaseReceiver.ERR_SERVICE_NOT_AVAILABLE, etc.
     *
     * Called on Registration error. See http://code.google.com/android/c2dm/#handling_reg
     */
    @Override
    public void onError(final Context context, final String errorId) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String errorMessage = new StringBuilder().append(REGISTRATION_ERROR_MESSAGE).append(": ").append(errorId).toString();
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param context
     *
     * Overview: You will need to broadcast an Intent to unregister with the C2DM service; for example, when a
     * user logs out to the application -- see http://code.google.com/android/c2dm/#unregistering.  This method is
     * called if that un-registration succeeds.
     *
     * Warning: Failure to unregister a device before re-registering might result in devices with multiple
     * registration_ids and might receiving duplicate notifications.
     */
    @Override
    public void onUnregistered(Context context) {
        super.onUnregistered(context);
    }
}
