// this file is for shit that is good to have that doesnt go anywhere else

import java.util.ArrayList;
import java.util.Optional;

public class Utils {
    /*
     * Returns `nth` formatted properly for any n
     *
     */
    public static String format_position(int n) {
        int last_digit = n % 10;
        if (n > 10 && n < 13) // 11 and 12
            return String.format("%dth", n);
        if (last_digit == 1)
            return String.format("%dst", n);
        if (last_digit == 2)
            return String.format("%dnd", n);
        if (last_digit == 3)
            return String.format("%drd", n);
        return String.format("%dth", n);
    }

    public static Optional<Tuple<String, String>> split_once(String s, String split) {
        String[] parts = s.split(split, -1);
        if (parts.length == 1) {
            System.out.println(String.format("No = in specifier for %s.", parts[0]));
            return Optional.empty();
        } else if (parts.length > 2) {
            System.out.println(String.format("Too many = in specifier for %s.", parts[0]));
            return Optional.empty();
        }
        return Optional.of(new Tuple<>(parts[0], parts[1]));
    }

    public static <A, B> B find(ArrayList<Tuple<A, B>> list, A target) {
        for (Tuple<A, B> entry : list) {
            if (entry.a == target) {
                return entry.b;
            } 
        }
        return list.get(0).b;
    }
}
