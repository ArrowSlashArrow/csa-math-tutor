import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public enum Solver {
    // variants
    PrimeFinder,
    EquationSolver,
    CircleArea,
    Hypotenuse,
    ExponentialEq;

    public SolverBase get_solver_fn() {
        switch (this) {
            case PrimeFinder -> {
                return new PrimeFinder();
            }
            case EquationSolver -> {
                return new EquationSolver();
            }
            case CircleArea -> {
                return new CircleArea();
            }
            case ExponentialEq -> {
                return new ExponentialEq();
            }
            case Hypotenuse -> {
                return new Hypotenuse();
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
        System.out.print("Enter `n` to find the n-th prime:\n> ");
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

// apologies in advance if it takes you 30 minutes to decipher this class
// use `solver2_concept.py` as a reference
class EquationSolver extends SolverBase {
    String HELP = """

            Enter variables like this:
            var1 = value <units>, var2 = value <units>

            No spaces needed anywhere except for between the value and units.
            To mark an unknown, type "var = ?"
            Any variable can be solved for provided all but the unknown are given.

            Sample inputs:
            1. aavg = 5.0 m/s^2, dv = 30 m/s, dt=?
                - solving for dt, expect output: +6.000 s
            2. dx = -239 m, a = -3.7 m/s^2, vf = ?, vi = 0.0 m/s
                - solving for dt, expect output: +42.055 m/s
            3. vi = 8.0 m/s, a = -9.81 m/s^2, dx = 0.0 m, dt = ?
                - solving for dt, expect output: -0.000, +1.631s

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
            Optional<Tuple<String, String>> result = Utils.split_once(var_segment, "=");
            if (result.isPresent())
                vars.add(result.get());
            else
                return;
        }

        // processed vars
        HashMap<String, Optional<Tuple<String, String>>> pvars = new HashMap<>();
        for (Tuple<String, String> var : vars) {
            String key = var.a;

            Optional<Tuple<String, String>> value = Optional.empty();
            if (!var.b.equals("?")) {
                Optional<Tuple<String, String>> result = Utils.split_once(var.b, " ");
                if (!result.isPresent())
                    return;
                value = Optional.of(result.get());
            }
            pvars.put(key, value);
        }

        HashMap<String, Optional<Double>> pvars_nounits = new HashMap<>();
        for (Tuple<String, String> var : vars) {
            String key = var.a;

            Optional<Double> value = Optional.empty();
            if (!var.b.equals("?")) {
                Optional<Tuple<String, String>> result = Utils.split_once(var.b, " ");
                if (!result.isPresent())
                    return;

                // result is (unparsed number, units)
                try {
                    value = Optional.of(Double.parseDouble(result.get().a));
                } catch (Exception e) {
                    System.out.println("Could not parse number: " + result.get().a);
                    return;
                }

            }
            pvars_nounits.put(key, value);
        }

        // get unknown and validate input
        String unknown = null;
        for (HashMap.Entry<String, Optional<Tuple<String, String>>> entry : pvars.entrySet()) {
            String var = entry.getKey();
            Optional<Tuple<String, String>> val = entry.getValue();

            if (val.isEmpty()) {
                if (unknown != null) {
                    System.out.println("Too many unknowns. Solving for one unknown at most.");
                    return;
                }
                unknown = var;
            } else {
                Tuple<String, String> unwrapped = val.get();
                // validate units
                String units = unwrapped.b;
                if (!valid_units.containsKey(var)) {
                    System.out
                            .println("Unknown variable " + var + ". Supported variables: " + valid_units.keySet());
                    return;
                }
                if (!valid_units.get(var).equals(units)) {
                    System.out.println(
                            "Invalid units for " + var + ". Acceptable units are " + valid_units.get(var));
                    return;
                }
            }
        }

        if (unknown == null) {
            System.out.println("No unknowns given (nothing to solve for)");
            return;
        }

        List<String> sorted_vars = new ArrayList<>(pvars.keySet());
        Collections.sort(sorted_vars);
        String eq_key = String.join(",", sorted_vars);

        int eq_idx = 0;
        if (eq_key.equals("vavg,vf,vi"))
            eq_idx = 0;
        else if (eq_key.equals("dt,dx,vavg"))
            eq_idx = 1;
        else if (eq_key.equals("aavg,dt,dv"))
            eq_idx = 2;
        else if (eq_key.equals("a,dt,dx,vi"))
            eq_idx = 3;
        else if (eq_key.equals("a,dx,vf,vi"))
            eq_idx = 4;
        else {
            System.out.println("No known equation found from variables. Type 'help' to see supported equations.");
            return;
        }

        // get eq here
        String[] solverEqs = getSolverEquation(eq_idx);
        String eq = solverEqs[get_unknown_idx(unknown, eq_idx)];
        System.out.println("solving equation: \n" + unknown + " = " + eq);

        // substitute givens (for displaying)
        List<HashMap.Entry<String, Optional<Tuple<String, String>>>> sorted_entries = new ArrayList<>(pvars.entrySet());
        sorted_entries.sort((a, b) -> Integer.compare(b.getKey().length(), a.getKey().length()));

        for (HashMap.Entry<String, Optional<Tuple<String, String>>> entry : sorted_entries) {
            String var = entry.getKey();
            Optional<Tuple<String, String>> val = entry.getValue();
            if (val.isPresent()) {
                Tuple<String, String> unwrapped = val.get();
                eq = eq.replace(var, unwrapped.a + " " + unwrapped.b);
            }
        }
        System.out.println(unknown + " = " + eq);

        // then solve
        // results here could be more than one because dt equation is a parabola
        // (may have 2 solutions)
        double[] results = solve(eq_idx, unknown, pvars_nounits);

        // then filter negatives if the var is dt (time can't go backwards, obviously)
        List<Double> filtered_results = new ArrayList<>();
        for (double r : results) {
            if (r >= 0 || !unknown.equals("dt")) {
                filtered_results.add(r);
            }
        }

        // Format output
        String output = unknown + " = ";
        for (int i = 0; i < filtered_results.size(); i++) {
            if (i > 0)
                output += ", ";
            output += format_num(filtered_results.get(i));
        }
        output += " " + valid_units.get(unknown);
        System.out.println(output);
    }

    static int get_unknown_idx(String unknown, int eq_idx) {
        String[] unknowns = new String[] {
                "vavg,vf,vi",
                "vavg,dx,dt",
                "aavg,dv,dt",
                "dx,vi,dt,a",
                "vf,vi,a,dx"
        };
        String[] variables = unknowns[eq_idx].split(",");
        for (int i = 0; i < variables.length; i++) {
            if (variables[i].equals(unknown))
                return i;
        }
        return 0;
    }

    static String[] getSolverEquation(int eq_idx) {
        // autoformatter is tripping (imo these shouldnt be double indented)
        String[][] equations = {
                {
                        "(vf + vi) / 2",
                        "2 * vavg - vi",
                        "2 * vavg - vf"
                },
                {
                        "(dx) / (dt)",
                        "dt * vavg",
                        "(dx) / (vavg)"
                },
                {
                        "dv / dt",
                        "aavg * dt",
                        "dv / aavg"
                },
                {
                        "(vi * dt) + (0.5 * a * dt^2)",
                        "(dx - (0.5 * a * dt^2)) / dt",
                        "(-vi + sqrt(vi^2 + (2 * a * dx))) / a",
                        "(dx - (vi * dt)) / (0.5 * dt^2)"
                },
                {
                        "sqrt(vi^2 + (2 * a * dx))",
                        "sqrt(vf^2 - (2 * a * dx))",
                        "(vf^2 - vi^2) / (2 * dx)",
                        "(vf^2 - vi^2) / (2 * a)"
                }
        };
        return equations[eq_idx];
    }

    static double[] solve(int eq_idx, String unknown, HashMap<String, Optional<Double>> pvars_nounits) {
        double vi = get_var(pvars_nounits, "vi");
        double vf = get_var(pvars_nounits, "vf");
        double vavg = get_var(pvars_nounits, "vavg");
        double dx = get_var(pvars_nounits, "dx");
        double dt = get_var(pvars_nounits, "dt");
        double a = get_var(pvars_nounits, "a");
        double dv = get_var(pvars_nounits, "dv");
        double aavg = get_var(pvars_nounits, "aavg");

        // lambda are a pain in the ass to store (especially in hashmaps)
        // use switch to simplify things
        switch (eq_idx) {
            case 0: // vavg equations
                if (unknown.equals("vavg"))
                    return new double[] { (vf + vi) / 2 };
                if (unknown.equals("vf"))
                    return new double[] { 2 * vavg - vi };
                if (unknown.equals("vi"))
                    return new double[] { 2 * vavg - vf };

            case 1: // vavg = dx / dt equations
                if (unknown.equals("vavg"))
                    return new double[] { dx / dt };
                if (unknown.equals("dx"))
                    return new double[] { dt * vavg };
                if (unknown.equals("dt"))
                    return new double[] { dx / vavg };

            case 2: // aavg equations
                if (unknown.equals("aavg"))
                    return new double[] { dv / dt };
                if (unknown.equals("dv"))
                    return new double[] { aavg * dt };
                if (unknown.equals("dt"))
                    return new double[] { dv / aavg };

            case 3: // dx equation
                if (unknown.equals("dx"))
                    return new double[] { (vi * dt) + (0.5 * a * dt * dt) };
                if (unknown.equals("vi"))
                    return new double[] { (dx - (0.5 * a * dt * dt)) / dt };
                if (unknown.equals("dt")) {
                    double discriminant = vi * vi + (2 * a * dx);
                    return new double[] { (-vi + Math.sqrt(discriminant)) / a, (-vi - Math.sqrt(discriminant)) / a };
                }
                if (unknown.equals("a"))
                    return new double[] { (dx - (vi * dt)) / (0.5 * dt * dt) };

            case 4: // vf^2 equation
                if (unknown.equals("vf"))
                    return new double[] { Math.sqrt(vi * vi + (2 * a * dx)) };
                if (unknown.equals("vi"))
                    return new double[] { Math.sqrt(vf * vf - (2 * a * dx)) };
                if (unknown.equals("a"))
                    return new double[] { (vf * vf - vi * vi) / (2 * dx) };
                if (unknown.equals("dx"))
                    return new double[] { (vf * vf - vi * vi) / (2 * a) };
        }
        return new double[] {};
    }

    static double get_var(HashMap<String, Optional<Double>> map, String key) {
        return map.getOrDefault(key, Optional.empty()).orElseGet(() -> Double.NaN);
    }

    static String format_num(double n) {
        return String.format("%+.3f", n);
    }
}

class CircleArea extends SolverBase {
    public void run() {
        System.out.println("\nArea = radius^2 * pi\n");
        InputGetter i = new InputGetter("Area = [radius] ^ 2 * pi", inputs -> {
            return Math.pow(Utils.find(inputs, "radius").parsed_value, 2.0) * Math.PI;
        });
        System.out.println(String.format("Area of this circle: %.3f", i.get_input()));
    }
}

class ExponentialEq extends SolverBase {
    public void run() {
        System.out.println("\nA_t = A_0 * e^kt rearranged to solve for k:\nk = \n(A_t / A_0) / t\n");
        InputGetter i = new InputGetter("k = ln([at]/[a0])/[t]", inputs -> {
            return Math.log(Utils.find(inputs, "at").parsed_value / Utils.find(inputs, "a0").parsed_value)
                    / Utils.find(inputs, "t").parsed_value;
        });

        double k = i.get_input();
        System.out.println("A_t = A_0 * e^kt:");
        System.out.println(String.format("k = %.3f\n", k));

        System.out.println("\nSecond equation: A_t = A_0 * e^kt\n");
        InputGetter i2 = new InputGetter("[at] = [at] * e^(k*[t])", inputs -> {
            return Utils.find(inputs, "at").parsed_value * Math.exp(k * Utils.find(inputs, "t").parsed_value);
        });
        i2.get_input();
    }
}

class Hypotenuse extends SolverBase {
    public void run() {
        System.out.println("\nHypotenuse of a right triangle:\nc = sqrt(a^2 + b^2)\n");
        InputGetter i = new InputGetter("c = sqrt([a]^2 + [b]^2)", inputs -> {
            return Math.hypot(Utils.find(inputs, "a").parsed_value, Utils.find(inputs, "b").parsed_value);
        });
        System.out.println(String.format("Hypotenuse of the triangle: %.3f", i.get_input()));
    }
}

// 2d vector addition
// matrix multipliction
//