import java.util.List;
import java.util.Optional;

// proof of concept
public class Main {
    private static final List<String> OPTIONS = List.of("Find the n-th prime", "Exit");

    public static void main(String[] args) {
        List<Solver> solvers = List.of(Solver.PrimeFinder);
        while (true) {
            Optional<Solver> s = choose_solver(solvers);
            if (s.isEmpty()) {
                // quit program
                return;
            }

            // otherwise, run solver
            Solver solver = s.get();
            solver.get_solver_fn().run();
        }

    }

    public static Optional<Solver> choose_solver(List<Solver> solvers) {
        // todo
        switch (SelectorBox.get_option(OPTIONS)) {
            case 0 -> {
                return Optional.of(Solver.PrimeFinder);
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
