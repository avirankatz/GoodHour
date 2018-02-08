package avirankatz.goodhour.goodhourprefs;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;


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

    public boolean isRepeating() {
        for (boolean day : days) {
            if (day) return true;
        }
        return false;
    }
}
