package avirankatz.goodhour.goodhourprefs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import avirankatz.goodhour.GoodHourActivity;
import avirankatz.goodhour.helpers.GlobalMethods;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarmId", 0);
        if (alarmId == 0) {
            if (GlobalMethods.DEBUG)
                Toast.makeText(context, "alarm id is 0", Toast.LENGTH_LONG).show();
            scheduleAlarms(context);
        }
        if (alarmId != 0) {
            setNextAlarm(intent.getIntExtra("alarmId", 0), context);
            Intent intent1 = new Intent(context, GoodHourActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

    private void scheduleAlarms(Context context) {
        for (GoodHour goodHour : GlobalMethods.loadGoodHoursFromFile(context)) {
            goodHour.scheduleAlarms(context);
        }

    }

    private void setNextAlarm(int alarmId, Context context) {
        if (alarmId != 0) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.add(Calendar.WEEK_OF_YEAR, 1);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    alarmId,
                    new Intent(context, AlarmReceiver.class)
                            .putExtra("alarmId", alarmId),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            manager.set(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.getTimeInMillis(),
                    pendingIntent);
        }
    }
}
