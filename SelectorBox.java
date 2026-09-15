import java.io.IOException;
import java.io.Reader;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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
        print_options(options, max_length, selected_idx);
        while (true) { 
            // move cursor (ansi code)
            System.out.println("\u001b[" + Integer.toString(options.size() + 3) + "F\r");
            print_options(options, max_length, selected_idx);
            // read raw keystrokes
            try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
                terminal.enterRawMode();
                try (Reader reader = terminal.reader()) {
                    boolean quit_reader_loop = false;
                    while (!quit_reader_loop) {
                        int ch = reader.read();

                        if (ch == '\r') { // enter
                            return selected_idx;
                        } else if (ch == 27) { // escape key code
                            // next two chars tell us what's the key
                            int next1 = reader.read();
                            int next2 = reader.read();
                            // System.out.println("" + next1 + " " + next2);
                            if (next1 == 'O' && next2 == 'A') { // up arrow
                                selected_idx = Math.max(0, selected_idx - 1);
                                quit_reader_loop = true;
                            }
                            if (next1 == 'O' && next2 == 'B') { // down arrow
                                selected_idx = Math.min(options.size() - 1, selected_idx + 1);
                                quit_reader_loop = true;
                            }
                        }
                    }
                }
            } catch ( IOException e ) {
                System.out.println("Failed to read user input: " + e);
            }
        }
    }

    public static void print_options(List<String> options, int max_length, int selected_idx) {
        String template  = "|   %-" + max_length + "s |\n";
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
    }
}
