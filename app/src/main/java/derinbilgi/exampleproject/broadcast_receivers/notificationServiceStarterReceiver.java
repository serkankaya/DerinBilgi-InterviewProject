package derinbilgi.exampleproject.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public final class notificationServiceStarterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationEventReceiver.setupAlarm(context);
    }
}