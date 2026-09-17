public class Utils {
    /* Returns `nth` formatted properly for any n
     *
     */
    public static String format_position(int n) {
        int last_digit = n % 10;
        if (n > 10 && n < 13) return String.format("%dth", n);
        if (last_digit == 1) return String.format("%dst", n);
        if (last_digit == 2) return String.format("%dnd", n);
        if (last_digit == 3) return String.format("%drd", n);
        return String.format("%dth", n);
    }
}
