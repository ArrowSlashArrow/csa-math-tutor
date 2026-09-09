
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MainUI {
    private static Scanner s = new Scanner(System.in);
    private static final String IN_STR = "Welcome to mathematics brother.";
    private static final List<String> OPTIONS = List.of("Find the n-th prime", "exit");

    // returns `Some(solver)` if the user chooses a solver
    // returns `None` if the user chooses to exit the program
    public Optional<Solver> choose_solver(List<Solver> solvers) {
        SelectorBox s = new SelectorBox();
        Optional<String> result = s.get_option(OPTIONS);
        
        // todo
        
         {
            case "1" -> {return Optional.of(Solver.PrimeFinder);}
            case "2" -> {return Optional.empty();}
            default -> {
                // with the ui concept below being used, this should never happen.
                // this is, of course, a temporary function.
                System.out.println("Invalid input.");
                return Optional.empty();
            }
        }
        
        /* ui concept
        +------ Pick a solver -----+
        | > Solver1                |
        |   Solver2                |
        |   Solver3                |
        |   Solver4                |
        |   exit                   |
        +--------------------------+
        */
    }
}
