import java.io.IOException;
import java.io.Reader;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.List;

public class SelectorBox {
    static final String RESET = "\u001b[0m";
    static final String RESET_COL = "\u001b[39m";
    static final String BOLD = "\u001b[1m";
    static final String GREEN = "\u001b[32m";
    static final String CLEAR_AFTER_CURSOR = "\u001b[0K";

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
                    // use quit_reader_loop instead of break which could exit outer loop
                    // idk i didnt test it but it works therefore don't touch it.
                    boolean quit_reader_loop = false;

                    while (!quit_reader_loop) {
                        int ch = reader.read();

                        if (ch == '\r') { // enter
                            return selected_idx;
                        } else if (ch == 27) { // escape key code
                            // next two chars tell us what's the key
                            int next1 = reader.read();
                            int next2 = reader.read();

                            // use ansi codes
                            // the below operations increment/decrement with bounds checks
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
            } catch (IOException e) {
                System.out.println("Failed to read user input: " + e);
            }
        }
    }

    public static void print_options(List<String> options, int max_length, int selected_idx) {
        // styling?? :astonished:
        String template = "│   %-" + max_length + "s │"
                + CLEAR_AFTER_CURSOR + "\n";
        String template2 = "│ " + GREEN + BOLD + ">" + RESET_COL + " %-" + max_length + "s" + RESET + " │"
                + CLEAR_AFTER_CURSOR + "\n";
        String top = "┌───" + "─".repeat(max_length) + "─┐"
                + CLEAR_AFTER_CURSOR + "\n";
        String bottom = "└───" + "─".repeat(max_length) + "─┘"
                + CLEAR_AFTER_CURSOR + "\n";
        // build string
        String out_str = top;
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
        out_str += bottom;

        System.out.print(out_str);
        System.out.flush();
    }
}
