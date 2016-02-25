package OtherHandlers;

/**
 * Created by jeanmendez on 2/24/16.
 */
public class DateHandler {

    String date;
    String time;

    String month;
    String day;
    String year;

    public DateHandler(String datetime) {
        this.date = getDate(datetime);
        this.time = getTime(datetime);

        this.year = getYear(this.date);
        this.month = getMonth(this.date);
        this.day = getDay(this.date);
    }

    public String getDisplayDate() {
        return this.month + " " + this.day + ", " + this.year;
    }

    public String getDisplayTime() {
        String amOrPm = "AM";
        int timeNumber = Integer.parseInt(this.time.substring(0,2));
        int minutes = Integer.parseInt(this.time.substring(3,5));

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
        return date.substring(8, 10);
    }

    private String getMonth(String date) {
        String monthNumber = date.substring(5, 7);
        if(monthNumber.equals("01")) {
            return "Jan";
        }
        if(monthNumber.equals("02")) {
            return "Feb";
        }
        if(monthNumber.equals("03")) {
            return "Mar";
        }
        if(monthNumber.equals("04")) {
            return "Apr";
        }
        if(monthNumber.equals("05")) {
            return "May";
        }
        if(monthNumber.equals("06")) {
            return "Jun";
        }
        if(monthNumber.equals("07")) {
            return "Jul";
        }
        if(monthNumber.equals("08")) {
            return "Aug";
        }
        if(monthNumber.equals("09")) {
            return "Sep";
        }
        if(monthNumber.equals("10")) {
            return "Oct";
        }
        if(monthNumber.equals("11")) {
            return "Nov";
        }
        if(monthNumber.equals("12")) {
            return "Dec";
        }
        return "Jan";
    }

    private String getYear(String date) {
        return date.substring(0, 4);
    }

    private String getTime(String datetime) {
        return datetime.substring(11, 16);
    }

    private String getDate(String datetime) {
        return datetime.substring(0, 10);
    }

}
