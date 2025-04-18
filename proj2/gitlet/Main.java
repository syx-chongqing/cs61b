package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                Repository.init();
                break;
            case "add":
                if (args.length != 2) {
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    System.exit(0);
                }
                Repository.rm(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    System.exit(0);
                }
                Repository.log();
                break;
            case "checkout":
                if (args.length == 2 || args.length == 4 || args.length == 3) {
                    Repository.checkout(args);
                } else {
                    System.exit(0);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
