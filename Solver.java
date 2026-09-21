import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

public enum Solver {
    // variants
    PrimeFinder,
    EquationSolver;

    public SolverBase get_solver_fn() {
        switch (this) {
            case PrimeFinder -> {
                return new PrimeFinder();
            }
            case EquationSolver -> {
                return new EquationSolver();
            }
            default -> {
                // given that we implement all of the variants of this enum,
                // this line should never run.

                System.out.println("""
                        Create an issue on https://github.com/ArrowSlashArrow/csa-math-tutor titled
                        \"You didn't implement all of the variants of `Solver`, idiot.\" """);
                return new PrimeFinder();
            }
        }
    }
}

abstract class SolverBase {
    // private doesn't work here; classes won't be able to use the inherited
    // variable
    protected static Scanner s = new Scanner(System.in);

    public void run() {
        System.out.println("no .run() impl for " + getClass());
    }
}

class PrimeFinder extends SolverBase {
    @Override
    public void run() {
        System.out.print("""
                Enter `n` to find the n-th prime:
                > """);
        System.out.flush(); // we're using print with no \n at the end

        int n;
        try {
            n = Integer.parseInt(s.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input");
            return;
        }

        if (n < 1) {
            System.out.println("invalid input. n must be > 0");
            return;
        }

        Integer result = get_nth_prime(n);
        System.out.println("The " + Utils.format_position(n) + " prime number is " + result);
    }

    public static Integer get_nth_prime(int n) {
        // 2, 3, 5, 7, 11, 13, ...
        if (n == 1)
            return 2;
        if (n == 2)
            return 3;

        int found = 2;
        int i = 3;
        while (found < n) {
            i += 2;
            if (is_prime(i)) {
                found++;
            }
        }

        return i;
    }

    public static boolean is_prime(int n) {
        // starting factor 3
        // * all numbers are divisible by 1, so don't check it
        // * all numbers here are guaranteed to be odd, so 2 is pointless

        // not gonna bother to make it faster
        for (int f = 3; f * f <= n; f += 2) {
            if (n % f == 0) {
                return false;
            }
        }
        return true;
    }
}

class EquationSolver extends SolverBase {
    String HELP = """
    Enter variables like this:
    var1 = value <units>, var2 = value <units>
    No spaces needed anywhere except for between the value and units. 
    To mark an unknown, type "var = ?"
    Any variable can be solved for provided all but the unknown are given.

    Supported equations:
    1: vavg = (vf + vi) / 2
    2: vavg = dx / dt
    3: aavg = dv / dt
    4: dx = (vi * dt) + (0.5 * a * dt^2)
    5: vf^2 = vi ^ 2 + (2a * dx)

    Accepted units for each variable:
    vavg: m/s
    vf: m/s
    vi: m/s
    dv: m/s
    dx: m
    dt: s
    aavg: m/s^2
    a: m/s^2
    """;

    HashMap<String, String> valid_units = new HashMap<>();


    @Override
    public void run() {
        // init maps
        valid_units.put("vavg", "m/s");
        valid_units.put("vf", "m/s");
        valid_units.put("vi", "m/s");
        valid_units.put("dv", "m/s");
        valid_units.put("dx", "m");
        valid_units.put("dt", "s");
        valid_units.put("aavg", "m/s^2");
        valid_units.put("a", "m/s^2");
        System.out.print("Enter your variables (type 'help' for help): ");
        System.out.flush();
        String inp = s.nextLine();
        if (inp.equals("help")) {
            System.out.println(HELP);
            return;
        }

        // one line in python btw
        ArrayList<Tuple<String, String>> vars = new ArrayList<>();
        for (String var_segment : inp.strip().split(",")) {
            // var segment must have only two parts: name, value +units
            Optional<Tuple<String, String>> result = split_once(var_segment, "=");
            if (result.isPresent()) vars.add(result.get());
            else return;
        }

        // processed vars
        HashMap<String, Optional<Tuple<String, String>>> pvars = new HashMap<>();
        for (Tuple<String, String> var : vars) {
            String key = var.a;

            Optional<Tuple<String, String>> value = Optional.empty();
            if (!var.b.equals("?")) {
                Optional<Tuple<String, String>> result = split_once(var.b, " ");
                if (!result.isPresent()) return;
                value = Optional.of(result.get());
            }
            pvars.put(key, value);
        }
        
        HashMap<String, Optional<Double>> pvars_nounits = new HashMap<>();
        for (Tuple<String, String> var : vars) {
            String key = var.a;

            Optional<Double> value = Optional.empty();
            if (!var.b.equals("?")) {
                Optional<Tuple<String, String>> result = split_once(var.b, " ");
                if (!result.isPresent()) return;

                // result is (unparsed number, units)
                try {
                    value = Optional.of(Double.parseDouble(result.get()[0]));
                } catch (Exception e) {
                    System.out.println("Could not parse number: "+ result.get()[0]);
                    return;
                }
                
            }
            pvars_nounits.put(key, value);
        }

        System.out.println("vars: " + vars + "\npvars: " + pvars);
    }

    private static Optional<Tuple<String, String>> split_once(String s, String split) {
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
}
