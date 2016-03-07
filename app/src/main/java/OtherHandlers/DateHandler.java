package OtherHandlers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by jeanmendez on 2/24/16.
 */
public class DateHandler {

    String hour;
    String month;
    String dayOfWeek;
    String dayOfMonth;
    String year;

    public DateHandler(String datetime) throws ParseException {

        String convertedDatetime = convertDatetimeToUTC(datetime);

        this.dayOfWeek = getDay(convertedDatetime);
        this.month = getMonth(convertedDatetime);
        this.dayOfMonth = getDayOfMonth(convertedDatetime);

        this.hour = getTime(convertedDatetime);
        this.year = getYear(convertedDatetime);
    }

    private String convertDatetimeToUTC(String datetime) throws ParseException {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        f.setTimeZone(utc);
        GregorianCalendar cal = new GregorianCalendar(utc);
        cal.setTime(f.parse(datetime));
        return cal.getTime().toString();
    }

    private String getDayOfMonth(String convertedDatetime) {
        return convertedDatetime.substring(8, 10);
    }

    public String getDisplayDate() {
        return this.month + this.dayOfMonth + ", " + this.year;
    }

    public String getDisplayTime() {
        String amOrPm = "AM";
        int timeNumber = Integer.parseInt(this.hour.substring(0,2));
        String minutes = this.hour.substring(3,5);

        if (timeNumber >= 12) {
            amOrPm = "PM";
        }

        if(timeNumber == 0){
            timeNumber = 12;
        }
        else if (timeNumber > 12){
            timeNumber -= 12;
        }
        return timeNumber + ":" + minutes + " " + amOrPm;
    }

    private String getDay(String date) {
        return date.substring(0, 4);
    }

    private String getMonth(String date) {
        return date.substring(4, 8);
    }

    private String getYear(String date) {
        return date.substring(date.length() - 4, date.length());
    }

    private String getTime(String datetime) {
        return datetime.substring(11, 16);
    }

}
