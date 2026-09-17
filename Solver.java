import java.util.Scanner;

public enum Solver {
    // variants
    PrimeFinder;

    public Solve get_solver_fn() {
        switch (this) {
            case PrimeFinder -> {
                return new PrimeFinder();
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

class PrimeFinder implements Solve {
    private static Scanner s = new Scanner(System.in);
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
