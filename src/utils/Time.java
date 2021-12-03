package utils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Public time class used to handle time functionality.
 */
public class Time {
    /**
     * Lambda function used to convert military time into AM/PM time.
     */
    public static TimePeriodConverter timePeriodConverter = (hour, period) -> {
        int hourInt = Integer.parseInt(hour);
        if(hourInt == 12) {
            if(period == "AM") return "00";
            else return hour;
        } else {
            if(period == "AM") return hour;
            else return String.valueOf((hourInt+12));
        }
    };

    /**
     * Lambda function used to convert military time into AM/PM time.
     */
    public static PeriodToMilitary periodToMilitary = (String hour) -> {
        int hourInt = Integer.parseInt(hour);
        if(hourInt < 12) return hour;
        if(hourInt == 0) return "12";
        if(hourInt > 12) return String.valueOf(hourInt-12);
        return "12";
    };

    /**
     * Converts UTC LocalDateTime objects into the user's local time zone.
     * @param utcString
     * @return
     */
    public static LocalDateTime convertUTCToLocal(String utcString){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime utcLocalDateTime = LocalDateTime.parse(utcString, format);
        final ZoneId utcZone = ZoneId.of("UTC");
        final ZoneId localZone = ZoneId.systemDefault();
        return utcLocalDateTime.atZone(utcZone).withZoneSameInstant(localZone).toLocalDateTime();
    }

    /**
     * Takes LocalDate object and two String params to build a LocalDateTime object.
     * @param appointmentDate LocalDate object to combine with hour and minute strings.
     * @param hour String representing hour to be used.
     * @param min String representing minute to be used.
     * @return Returns LocalDateTime combining appointmentDate with inputted hour and minute.
     */
    public static LocalDateTime buildLocalDateTime(LocalDate appointmentDate, String hour, String min) {
        try {
            String timeString = hour + ":" + min;
            LocalTime localTime = LocalTime.parse(timeString);
            LocalDateTime localDateTime = LocalDateTime.of(appointmentDate, localTime);
            return localDateTime;
        } catch(Exception e) {System.out.println("Exception caught while calling buildLocalDateTime in Time: " + e.getMessage());}
        return null;
    }

    /**
     * Determines if one LocalDateTime object comes before or after the other.
     * @param start LocalDateTime of appointment start time.
     * @param end LocalDateTime of appointment end time.
     * @return Returns true if inputted start time is before the end time, false if not.
     */
    public static Boolean startBeforeEnd(LocalDateTime start, LocalDateTime end) {
        if(end.isAfter(start)) {
            return true;
        }
        return false;
    }

    /**
     * Determines if start and end LocalDateTime objects are within opening hour constraints.
     * @param start LocalDateTime of appointment start time.
     * @param end LocalDateTime of appointment end time.
     * @return Returns true if appointment start and end times are within opening hour constraints, false if not.
     */
    public static Boolean isOpenHours(LocalDateTime start, LocalDateTime end) {
        // Creates local and EST ZoneId objects
        final ZoneId estZone = ZoneId.of("America/New_York");
        final ZoneId localZone = ZoneId.systemDefault();
        // Converts selected start and end times to EST for comparison against opening hours
        LocalTime startEST = start.atZone(localZone).withZoneSameInstant(estZone).toLocalTime();
        LocalTime endEST = end.atZone(localZone).withZoneSameInstant(estZone).toLocalTime();
        // Creates LocalTime objects to represent opening hours
        LocalTime openingTime = LocalTime.of(8, 0);
        LocalTime closingTime = LocalTime.of(22, 0);
        if((( startEST.isAfter(openingTime) && startEST.isBefore(closingTime) ) || startEST.equals(openingTime)) && (( endEST.isBefore(closingTime) && endEST.isAfter(openingTime) ) || endEST.equals(closingTime))) {
            return true;
        }
        return false;
    }

    /**
     * Determines if an appointment is within 15 minutes of current time.
     * @param utcStartString String representing UTC time of appointment.
     * @return Returns true if appointment is within 15 minutes of current time.
     */
    public static Boolean isUpcoming(String utcStartString){
        Timestamp ts = Timestamp.from(Instant.now());
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final ZoneId utcZone = ZoneId.of("UTC");
        final ZoneId localZone = ZoneId.systemDefault();
        LocalDateTime utcStartTime = LocalDateTime.parse(utcStartString, format);
        LocalDateTime currentLocalDateTime = ts.toLocalDateTime();
        LocalDateTime localDateTimePlus = ts.toLocalDateTime().plusMinutes(15);
        LocalDateTime localStartTime = utcStartTime.atZone(utcZone).withZoneSameInstant(localZone).toLocalDateTime();
        int comparison = localStartTime.compareTo(localDateTimePlus);
        if(!currentLocalDateTime.isAfter(localStartTime) && comparison < 1){
            return true;
        }
        return false;
    }
}
