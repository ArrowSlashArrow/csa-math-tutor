import java.util.List;
import java.util.Optional;

// proof of concept
public class Main {
    // index => (label, function)
    private static final List<String> OPTION_LABELS = List.of("Find the n-th prime", "Solve physics equation", "Exit");
    private static final List<Optional<Solver>> SOLVERS = List.of(Optional.of(Solver.PrimeFinder),
            Optional.of(Solver.EquationSolver), Optional.empty());

    public static void main(String[] args) {
        assert OPTION_LABELS.size() == SOLVERS.size(); // guarantees that arrays are parallel
        System.out.println(
                "If java is throwing errors, it's because it can't handle the greatness of this program. Ignore them; JVM is delusional.");

        while (true) {
            Optional<Solver> s = SOLVERS.get(SelectorBox.get_option(OPTION_LABELS));
            if (s.isEmpty()) {
                break; // `None` option => exit
            } else {
                s.get().get_solver_fn().run();
            }
        }
    }
}