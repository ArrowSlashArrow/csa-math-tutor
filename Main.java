import java.util.List;
import java.util.Optional;

// proof of concept
public class Main {
    public static void main(String[] args) {
        List<Solver> solvers = List.of(Solver.PrimeFinder);
        MainUI main = new MainUI();
        while (true) { 
            Optional<Solver> s = main.choose_solver(solvers);
            if (s.isEmpty()) {
                // quit program
                return;
            }

            // otherwise, run solver
            Solver solver = s.get();
            solver.get_solver_fn().run();
        }
    
    }
}
