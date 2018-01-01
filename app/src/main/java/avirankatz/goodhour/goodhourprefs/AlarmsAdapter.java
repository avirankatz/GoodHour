package avirankatz.goodhour.goodhourprefs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.List;

import avirankatz.goodhour.R;
import avirankatz.goodhour.helpers.GlobalMethods;

public class AlarmsAdapter extends ArrayAdapter<GoodHour> {


    private List<GoodHour> hours;

    AlarmsAdapter(@NonNull Context context, int resource, int textViewResourceId, List<GoodHour> objects) {
        super(context, resource, textViewResourceId, objects);
        this.hours = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        GoodHour goodHour = getItem(position);
        if (goodHour != null) {
            setupTextView((TextView) v.findViewById(R.id.good_hours_list_item_time), goodHour);
            setupSwitch((Switch) v.findViewById(R.id.good_hours_list_item_switch), goodHour);
            setupDeleteButton((ImageButton) v.findViewById(R.id.good_hours_list_item_delete), goodHour);
            setupDayButtons(v, goodHour);
        }
        return v;
    }

    private void setupDayButtons(View v, final GoodHour goodHour) {
        for (int i = 0; i < goodHour.days.length; i++) {
            ToggleButton toggleButton;
            switch (i) {
                case 0:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day0);
                    break;
                case 1:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day1);
                    break;
                case 2:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day2);
                    break;
                case 3:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day3);
                    break;
                case 4:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day4);
                    break;
                case 5:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day5);
                    break;
                case 6:
                    toggleButton = v.findViewById(R.id.good_hours_list_item_day6);
                    break;
                default:
                    return;
            }
            toggleButton.setChecked(goodHour.days[i]);
            final int finalI = i;
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!compoundButton.isPressed()) return;
                    goodHour.days[finalI] = b;
                    GlobalMethods.saveGoodHoursToFile(getContext(), hours);
                    setAlarm(goodHour, null);
                }
            });
        }
    }

    private void setupDeleteButton(final ImageButton imageButton, final GoodHour goodHour) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goodHour.isActivated = false;
                setAlarm(goodHour, null);
                remove(goodHour);
                notifyDataSetChanged();
                GlobalMethods.saveGoodHoursToFile(getContext(), hours);
            }
        });
    }

    private void setupSwitch(final Switch aSwitch, final GoodHour goodHour) {
        aSwitch.setChecked(goodHour.isActivated);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isActivated) {
                if (!compoundButton.isPressed()) return;
                goodHour.isActivated = isActivated;
                setAlarm(goodHour, aSwitch.getRootView());
                GlobalMethods.saveGoodHoursToFile(getContext(), hours);
            }
        });
    }

    private void setAlarm(@NonNull GoodHour goodHour, View view) {
        final AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                goodHour.id,
                new Intent(getContext(), AlarmReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (goodHour.isActivated) {
            Calendar calendar = goodHour.time;
            if (!goodHour.isRepeating())
                manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            else
                setAlarmsForWeekdays(goodHour, goodHour.isActivated);
            if (view != null)
                Snackbar.make(
                        view,
                        String.format("נקבעה שעה טובה ל-%s",
                                goodHour.toString()),
                        2000
                ).show();
        } else {
            if (!goodHour.isRepeating())
                manager.cancel(pendingIntent);
            else
                setAlarmsForWeekdays(goodHour, goodHour.isActivated);
        }
    }

    private void setAlarmsForWeekdays(GoodHour goodHour, boolean isActivated) {
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        for (int i = 0; i < goodHour.days.length; i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getContext(),
                    goodHour.id + i + 1,
                    new Intent(getContext(), AlarmReceiver.class),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            boolean day = goodHour.days[i];
            if (day && isActivated) {
                goodHour.time.set(Calendar.DAY_OF_WEEK, i + 1);
                long alarmTime = goodHour.time.getTimeInMillis();
                if (goodHour.time.before(Calendar.getInstance()))
                    alarmTime += 1000 * 60 * 60 * 24 * 7;
                manager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        1000 * 60 * 60 * 24 * 7,
                        pendingIntent);
            } else
                manager.cancel(pendingIntent);
        }
    }

    private void setupTextView(final TextView hourView, final GoodHour goodHour) {
        if (goodHour == null) return;
        hourView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Calendar time = goodHour.time;
                int hour = time.get(Calendar.HOUR_OF_DAY);
                int minute = time.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND, 0);
                        if (calendar.before(Calendar.getInstance()))
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                        goodHour.time = calendar;
                        setAlarm(goodHour, view.getRootView());
                        notifyDataSetChanged();
                        GlobalMethods.saveGoodHoursToFile(getContext(), hours);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();
            }
        });
    }
}
