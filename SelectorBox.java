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

        int selected_idx = 0;
        while (true) { 
            String template = "|   %-" + max_length + "s |\n";
            String template2 = "| > %-" + max_length + "s |\n";
            String end = "+---" + "-".repeat(max_length) + "-+\n"; 
            // build string
            String out_str = end;
            int curr_idx = 0;
            for (String option : options) {
                // | <option padded right> |
                if (curr_idx == selected_idx) {
                    out_str += String.format(template2, option);
                } else {
                    out_str += String.format(template, option);
                }
                curr_idx += 1;
            }
            out_str += end;

            System.out.print(out_str);
            System.out.flush();

            // todo: read stdin for up/down arrow/enter input
            if (selected_idx == 0) {
                break;
            }
        }

        

        
        // todo
        return 0;
    }
}
