package utils;

/**
 * Interface with one abstract method used by lambda to convert to AM/PM time.
 */
public interface TimePeriodConverter {
    String convertHour(String hour, String period);
}
