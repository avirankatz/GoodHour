package avirankatz.goodhour.goodhourprefs;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import avirankatz.goodhour.helpers.GlobalMethods;


public class GoodHour implements Serializable {
    public Calendar time;
    public boolean isActivated;
    public int id;
    public boolean[] days;

    public GoodHour() {
        this.id = hashCode();
        time = Calendar.getInstance();
        time.setFirstDayOfWeek(Calendar.SUNDAY);
        days = new boolean[7];
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("HH:mm").format(time.getTime());
    }

    boolean isRepeating() {
        for (boolean day : days) {
            if (day) return true;
        }
        return false;
    }

    void scheduleAlarms(Context context) {
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                new Intent(context, AlarmReceiver.class).putExtra("alarmId", id),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (isActivated) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time.getTimeInMillis());
            while (calendar.before(Calendar.getInstance()))
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.SECOND, 0);
            if (!isRepeating()) {
                manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                if (GlobalMethods.DEBUG)
                    Toast.makeText(context, "non repeating alarm set", Toast.LENGTH_LONG).show();
            } else
                setAlarmsForWeekdays(context);
        } else {
            if (!isRepeating())
                manager.cancel(pendingIntent);
            else
                setAlarmsForWeekdays(context);
        }
    }

    void setAlarmsForWeekdays(Context context) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < days.length; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    id + i + 1,
                    new Intent(context, AlarmReceiver.class)
                            .putExtra("alarmId", id + i + 1),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            if (days[i] && isActivated) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                calendar.set(Calendar.DAY_OF_WEEK, i + 1);
                while (calendar.before(Calendar.getInstance()))
                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                manager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent);
                if (GlobalMethods.DEBUG)
                    Toast.makeText(context, "repeating alarm set", Toast.LENGTH_LONG).show();
            } else
                manager.cancel(pendingIntent);
        }
    }
}
