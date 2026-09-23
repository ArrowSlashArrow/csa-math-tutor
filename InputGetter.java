import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

class Segment {
    String label;
    Boolean is_var;

    public Segment(String s, Boolean b) {
        this.label = s;
        this.is_var = b;
    }

    public String toString() {
        return this.label;
    }
}

class SegmentNumber {
    String raw_value;
    Double parsed_value;
    // where is it in the original string
    Integer idx;

    public SegmentNumber(String s, Double d, Integer i) {
        this.raw_value = s;
        this.parsed_value = d;
        this.idx = i;
    }

    public void update() {
        try {
            this.parsed_value = Double.parseDouble(this.raw_value);
        } catch (Exception e) {
            this.parsed_value = Double.NaN;
        }
    }
}

public class InputGetter {
    static final String CLEAR_AFTER_CURSOR = "\u001b[0K";

    // segment, is variable
    ArrayList<Segment> segments = new ArrayList<>();
    ArrayList<String> input_segments = new ArrayList<>();
    int at_idx = 0;
    int seg_count = 0;
    int cursor_position = 0;
    long lines = 0;

    // (variable, segment value)
    // index is necessary for input purposes
    // at_idx corresponds to the current entry in this list that is being edited
    ArrayList<Tuple<String, SegmentNumber>> inputs = new ArrayList<>();
    Function<ArrayList<Tuple<String, SegmentNumber>>, Double> evaluator;

    // example: "Area = pi * [radius]^2"
    // - [input]
    // - segments = " pi * ", "^2"
    public InputGetter(String input, Function<ArrayList<Tuple<String, SegmentNumber>>, Double> evaluator) {
        Optional<Tuple<String, String>> result = Utils.split_once(input, "=");

        if (result.isEmpty()) { // throw an error
            int x = 1 / 0;
        }

        this.lines = input.chars()
                .filter(ch -> ch == '\n')
                .count() + 1;

        // we don't care about the left side since
        // we are gonna display the input in its place
        String eq = result.get().b;

        Boolean in_value_getter = false;
        int ch_idx = 0;
        String current_segment = "";
        for (String ch : eq.split("(?!^)")) {
            if (ch.equals("[") && !in_value_getter) {
                if (current_segment.length() != 0) {
                    this.segments.add(new Segment(current_segment, false));
                    this.seg_count += 1;
                    current_segment = "";
                }
                in_value_getter = true;
            } else if (ch.equals("]") && in_value_getter) {
                this.inputs.add(new Tuple<>(current_segment, new SegmentNumber("", Double.NaN, inputs.size())));
                this.segments.add(new Segment(current_segment, true));
                this.input_segments.add(current_segment);

                current_segment = "";
                in_value_getter = false;
            } else {
                current_segment += ch;
            }
            ch_idx += 1;
        }

        this.segments.add(new Segment(current_segment, false));

        this.evaluator = evaluator;
    }

    // up - before
    // down - next
    public double get_input() {
        System.out.println("Type numbers with keyboard. Use up/down arrow to move between inputs");
        this.render_state();
        System.out.flush();
        while (true) {
            this.render_state();

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
                            System.out.println("");
                            return this.evaluator.apply(this.inputs);
                        } else if (ch == 27) { // escape key code
                            // next two chars tell us what's the key
                            int next1 = reader.read();
                            int next2 = reader.read();

                            // use ansi codes
                            // the below operations increment/decrement with bounds checks
                            if (next1 == 'O' && next2 == 'A') { // up arrow
                                this.at_idx = Math.max(0, this.at_idx - 1);
                                quit_reader_loop = true;
                            }
                            if (next1 == 'O' && next2 == 'B') { // down arrow
                                this.at_idx = Math.min(this.seg_count - 1, this.at_idx + 1);
                                quit_reader_loop = true;
                            }
                        } else if (ch == 8) { // backspace
                            if (this.inputs.get(this.at_idx).b.raw_value.length() > 0) {
                                String orig = this.inputs.get(this.at_idx).b.raw_value;
                                orig = orig.substring(0, orig.length() - 1);
                                this.inputs.get(this.at_idx).b.raw_value = orig;
                            }
                            this.render_state();
                        } else {
                            // push char to currently selected idx
                            this.inputs.get(this.at_idx).b.raw_value += ("" + (char) ch);
                            this.render_state();
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed to read user input: " + e);
            }
        }
    }

    public double eval_self() {
        for (Tuple<String, SegmentNumber> entry : this.inputs) {
            entry.b.update();
        }
        return this.evaluator.apply(this.inputs);
    }

    public void render_state() {
        double result = this.eval_self();
        String s = String.format("%.3f = ", result);

        int field_count = 0;
        for (Segment segment : segments) {
            if (segment.is_var) {
                String raw = Utils.find(this.inputs, this.input_segments.get(field_count)).raw_value;
                if (raw.length() == 0) {
                    raw = "____";
                }
                s += "(" + raw + ")";

                if (field_count == this.at_idx) {
                    this.cursor_position = s.length() - 1;
                }
                field_count += 1;
            } else {
                // this is a static string
                s += segment.label;
            }
        }
        System.out.println("\u001b[" + Integer.toString((int) this.lines) + "F\r");
        System.out.print("\r" + s + CLEAR_AFTER_CURSOR + "\u001b[" + this.cursor_position + "G");
    }
}
