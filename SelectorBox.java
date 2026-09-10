import java.util.List;

public class SelectorBox {
    // returns index of selected choice.
    public static int get_option(List<String> options) {
        int max_length = 0;
        for (String option : options) {
            if (max_length < option.length()) {
                max_length = option.length();
            }
        }
        // build string
        String out_str = "";
        for (String option : options) {
            // | <option padded right> | 
            out_str += "|  |";
        }


        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // do nothing
        }
        // for (;;) {}
        
        // todo
        return 0;
    }
}
