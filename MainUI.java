import java.util.List;
import java.util.Optional;

public class MainUI {
    private static final List<String> OPTIONS = List.of("Find the n-th prime", "Exit");

    // returns `Some(solver)` if the user chooses a solver
    // returns `None` if the user chooses to exit the program
    public Optional<Solver> choose_solver(List<Solver> solvers) {
        // todo
        switch (SelectorBox.get_option(OPTIONS)) {
            case 0 -> {return Optional.of(Solver.PrimeFinder);}
            default -> {
                return Optional.empty(); 
            } 
        }
    }
}
