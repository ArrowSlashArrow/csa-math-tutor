import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// proof of concept
public class Main {
    private static Scanner s = new Scanner(System.in);
    private static final String IN_STR = """
            Welcome to mathematics brother.
            Select an option:
            1. Find the nth prime
            2. exit

            > """;
    public static void main(String[] args) {
        // System.out.print(IN_STR);
        // System.out.flush(); // we're using print with no \n at the end
        // String input = s.nextLine();
        // switch (input) {
        //     case "1" -> {
        //         int n = find_prime();
        //         if (n != -1) {
        //             System.out.println("The nth prime number is " + n + ".");
        //         }
        //     }
                
        //     case "2" -> {
        //         return;
        //     }
        //     default -> System.out.println("Invalid input.");
        // }

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

    public static int find_prime() {
        System.out.print("""
            Enter `n` to find the n-th prime:
            > """);
        System.out.flush(); // we're using print with no \n at the end
        
        int n;
        try {
            n = Integer.parseInt(s.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input");
            return -1;
        } 

        if (n < 1) {
            System.out.println("invalid input. n must be > 0");
            return - 1;
        }

        // 2, 3, 5, 7, 11, 13, ...
        if (n == 1) return 2;
        if (n == 2) return 3;

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
        for (int f = 3; f * f <= n; f++) {
            if (n % f == 0) {
                return false;
            }
        }
        return true;    
    }
}
