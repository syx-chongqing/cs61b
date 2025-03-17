public class Collatz {

    /**implementation of nextNumber! */
    public static int nextNumber(int n) {
        if (n  == 1) {
            return 1;
        } else if (n % 2 == 0) {
            return n / 2;
        } else {
            return n * 3 + 1;
        }
    }

    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1) {
            n = nextNumber(n);
            System.out.print(n + " ");
        }
        System.out.println();
    }
}
