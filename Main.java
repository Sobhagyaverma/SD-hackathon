import java.util.*;

class User {
    int id;
    String name;

    User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Expense {
    int id;
    String description;
    double amount;
    int paidBy;
    List<Integer> participants;

    Expense(int id, String description, double amount,
            int paidBy, List<Integer> participants) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.participants = participants;
    }
}

class ExpenseSplitter {

    Map<Integer, User> users = new HashMap<>();
    List<Expense> expenses = new ArrayList<>();
    Map<Integer, Map<Integer, Double>> balances = new HashMap<>();

    void createUser(int id, String name) {
        users.put(id, new User(id, name));
    }

    void addExpense(int id, String desc, double amount,
                    int paidBy, List<Integer> participants) {

        expenses.add(new Expense(id, desc, amount, paidBy, participants));

        double share = amount / participants.size();

        for (int user : participants) {
            if (user == paidBy) continue;

            if (!balances.containsKey(user)) {
                balances.put(user, new HashMap<>());
            }

            Map<Integer, Double> innerMap = balances.get(user);

            if (!innerMap.containsKey(paidBy)) {
                innerMap.put(paidBy, share);
            } else {
                double oldAmount = innerMap.get(paidBy);
                innerMap.put(paidBy, oldAmount + share);
            }
        }
    }

    void viewExpenses() {
        System.out.println("\nAll Expenses:");
        for (Expense e : expenses) {
            System.out.println(
                e.description + " | ₹" + e.amount +
                " | Paid by: " + users.get(e.paidBy).name
            );
        }
    }

    void showBalances() {
        System.out.println("\nBalances:");

        for (int from : balances.keySet()) {
            for (int to : balances.get(from).keySet()) {

                double amount = balances.get(from).get(to);

                if (amount > 0) {
                    System.out.println(
                        users.get(from).name +
                        " owes " +
                        users.get(to).name +
                        " ₹" +
                        String.format("%.2f", amount)
                    );
                }
            }
        }
    }
}

public class Main {

  public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ExpenseSplitter splitter = new ExpenseSplitter();

        while (true) {

            System.out.println("\n1. Add User");
            System.out.println("2. Add Expense");
            System.out.println("3. View Expenses");
            System.out.println("4. Show Balances");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); 

            if (choice == 1) {

                System.out.print("Enter User ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Enter Name: ");
                String name = sc.nextLine();

                splitter.createUser(id, name);
                System.out.println("User Added.");

            }

            else if (choice == 2) {

                System.out.print("Enter Expense ID: ");
                int id = sc.nextInt();
                sc.nextLine();

                System.out.print("Enter Description: ");
                String desc = sc.nextLine();

                System.out.print("Enter Amount: ");
                double amount = sc.nextDouble();

                System.out.print("Enter Paid By (User ID): ");
                int paidBy = sc.nextInt();

                System.out.print("Enter number of participants: ");
                int count = sc.nextInt();

                List<Integer> participants = new ArrayList<>();
                System.out.println("Enter participant IDs:");

                for (int i = 0; i < count; i++) {
                    participants.add(sc.nextInt());
                }

                splitter.addExpense(id, desc, amount, paidBy, participants);
                System.out.println("Expense Added.");
            }

            else if (choice == 3) {
                splitter.viewExpenses();
            }

            else if (choice == 4) {
                splitter.showBalances();
            }

            else if (choice == 5) {
                System.out.println("Exiting...");
                break;
            }

            else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }
}