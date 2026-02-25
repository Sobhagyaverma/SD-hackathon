import java.util.*;

public class ExpenseTracker {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        UserService userService = new UserService();
        BalanceService balanceService = new BalanceService(userService);
        SplitStrategy strategy = new EqualSplitStrategy(balanceService);
        ExpenseService expenseService = new ExpenseService(strategy);

        while (true) {

            System.out.println("\n1. Add User");
            System.out.println("2. Add Expense");
            System.out.println("3. View Expenses");
            System.out.println("4. Show Balances");
            System.out.println("5. Exit");

            int choice = readInt(sc, "Enter choice: ");

            if (choice == 1) {

                System.out.print("Enter User ID: ");
                String id = sc.nextLine().trim();

                System.out.print("Enter Name: ");
                String name = sc.nextLine().trim();

                if (id.isEmpty() || name.isEmpty()) {
                    System.out.println("Invalid input.");
                    continue;
                }

                userService.addUser(new User(id, name));
                System.out.println("User Added.");

            } else if (choice == 2) {

                System.out.print("Enter Title: ");
                String title = sc.nextLine().trim();

                double amount = readDouble(sc, "Enter Amount: ");

                if (amount <= 0) {
                    System.out.println("Amount must be positive.");
                    continue;
                }

                System.out.print("Enter Payer ID: ");
                String payerId = sc.nextLine().trim();

                User payer = userService.getUser(payerId);

                if (payer == null) {
                    System.out.println("Payer not found.");
                    continue;
                }

                int count = readInt(sc, "Enter number of participants: ");

                if (count <= 0) {
                    System.out.println("Invalid participant count.");
                    continue;
                }

                List<User> participants = new ArrayList<>();

                for (int i = 0; i < count; i++) {

                    System.out.print("Enter participant ID: ");
                    String id = sc.nextLine().trim();

                    User user = userService.getUser(id);

                    if (user == null) {
                        System.out.println("User not found: " + id);
                        participants.clear();
                        break;
                    }

                    participants.add(user);
                }

                if (participants.isEmpty()) {
                    System.out.println("Expense not added.");
                    continue;
                }

                Expense expense = new Expense(title, amount, payer, participants);
                expenseService.addExpense(expense);
                System.out.println("Expense Added.");

            } else if (choice == 3) {

                expenseService.printExpenses();

            } else if (choice == 4) {

                balanceService.printBalances();

            } else if (choice == 5) {

                System.out.println("Exiting...");
                break;

            } else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    private static int readInt(Scanner sc, String message) {

        while (true) {
            System.out.print(message);
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } else {
                System.out.println("Please enter a valid integer.");
                sc.nextLine();
            }
        }
    }

    private static double readDouble(Scanner sc, String message) {

        while (true) {
            System.out.print(message);
            if (sc.hasNextDouble()) {
                double value = sc.nextDouble();
                sc.nextLine();
                return value;
            } else {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
            }
        }
    }
}

class User {
    private String id;
    private String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}

class Expense {
    private String title;
    private double amount;
    private User paidBy;
    private List<User> participants;

    public Expense(String title, double amount, User paidBy, List<User> participants) {
        this.title = title;
        this.amount = amount;
        this.paidBy = paidBy;
        this.participants = participants;
    }

    public String getTitle() { return title; }
    public double getAmount() { return amount; }
    public User getPaidBy() { return paidBy; }
    public List<User> getParticipants() { return participants; }
}

interface SplitStrategy {
    void split(Expense expense);
}

class EqualSplitStrategy implements SplitStrategy {

    private BalanceService balanceService;

    public EqualSplitStrategy(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public void split(Expense expense) {

        double share = expense.getAmount() / expense.getParticipants().size();

        for (User user : expense.getParticipants()) {
            if (!user.getId().equals(expense.getPaidBy().getId())) {
                balanceService.updateBalance(user, expense.getPaidBy(), share);
            }
        }
    }
}

class ExpenseService {

    private List<Expense> expenses = new ArrayList<>();
    private SplitStrategy splitStrategy;

    public ExpenseService(SplitStrategy splitStrategy) {
        this.splitStrategy = splitStrategy;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        splitStrategy.split(expense);
    }

    public void printExpenses() {

        if (expenses.isEmpty()) {
            System.out.println("No expenses yet.");
            return;
        }

        for (Expense e : expenses) {
            System.out.println(e.getTitle() + " | ₹" + e.getAmount() + " | Paid by " + e.getPaidBy().getName());
        }
    }
}

class BalanceService {

    private Map<String, Map<String, Double>> balances = new HashMap<>();
    private UserService userService;

    public BalanceService(UserService userService) {
        this.userService = userService;
    }

    public void updateBalance(User debtor, User creditor, double amount) {

        balances.putIfAbsent(debtor.getId(), new HashMap<>());

        Map<String, Double> inner = balances.get(debtor.getId());
        inner.put(creditor.getId(), inner.getOrDefault(creditor.getId(), 0.0) + amount);
    }

    public void printBalances() {

        if (balances.isEmpty()) {
            System.out.println("No balances yet.");
            return;
        }

        for (String debtorId : balances.keySet()) {

            Map<String, Double> inner = balances.get(debtorId);

            for (String creditorId : inner.keySet()) {

                double amount = inner.get(creditorId);

                if (amount > 0) {
                    System.out.println(
                            userService.getUser(debtorId).getName()
                                    + " owes "
                                    + userService.getUser(creditorId).getName()
                                    + " ₹" + amount
                    );
                }
            }
        }
    }
}

class UserService {

    private Map<String, User> users = new HashMap<>();

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public User getUser(String id) {
        return users.get(id);
    }
}