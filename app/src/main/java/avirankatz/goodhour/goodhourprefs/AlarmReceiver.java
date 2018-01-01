package avirankatz.goodhour.goodhourprefs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import avirankatz.goodhour.GoodHourActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent intent1 = new Intent(context, GoodHourActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
