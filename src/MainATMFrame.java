import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainATMFrame extends JFrame {
    private User user;
    private ATMService atmService;

    private JLabel welcomeLabel;
    private JLabel balanceLabel;
    private JButton withdrawButton;
    private JButton depositButton;
    private JButton transferButton;
    private JButton historyButton;
    private JButton logoutButton;

    public MainATMFrame(User user, ATMService atmService) {
        this.user = user;
        this.atmService = atmService;

        setTitle("ATM - Welcome " + user.getUserId());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome, " + user.getUserId());
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(welcomeLabel);

        balanceLabel = new JLabel();
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateBalanceLabel();
        panel.add(balanceLabel);

        withdrawButton = new JButton("Withdraw");
        depositButton = new JButton("Deposit");
        transferButton = new JButton("Transfer");
        historyButton = new JButton("Transaction History");
        logoutButton = new JButton("Logout");

        panel.add(withdrawButton);
        panel.add(depositButton);
        panel.add(transferButton);
        panel.add(historyButton);
        panel.add(logoutButton);

        add(panel);

        // Button listeners

        withdrawButton.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
            if (amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (atmService.withdraw(user, amount)) {
                        JOptionPane.showMessageDialog(this, "Withdrawal successful!");
                        updateBalanceLabel();
                    } else {
                        JOptionPane.showMessageDialog(this, "Withdrawal failed! Check your balance or input.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount entered.");
                }
            }
        });

        depositButton.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
            if (amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (atmService.deposit(user, amount)) {
                        JOptionPane.showMessageDialog(this, "Deposit successful!");
                        updateBalanceLabel();
                    } else {
                        JOptionPane.showMessageDialog(this, "Deposit failed! Check your input.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount entered.");
                }
            }
        });

        transferButton.addActionListener(e -> {
            JTextField receiverField = new JTextField();
            JTextField amountField = new JTextField();
            Object[] message = {
                    "Receiver User ID:", receiverField,
                    "Amount:", amountField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Transfer Money", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String receiverId = receiverField.getText().trim();
                String amountStr = amountField.getText().trim();

                if (receiverId.isEmpty() || amountStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter all details.");
                    return;
                }

                try {
                    double amount = Double.parseDouble(amountStr);
                    boolean success = atmService.transfer(user, receiverId, amount);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Transfer successful!");
                        updateBalanceLabel();
                    } else {
                        JOptionPane.showMessageDialog(this, "Transfer failed! Check details and balance.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount entered.");
                }
            }
        });

        historyButton.addActionListener(e -> {
            List<String> history = atmService.getTransactionHistory(user.getUserId());
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No transaction history found.");
            } else {
                JTextArea textArea = new JTextArea(15, 30);
                history.forEach(record -> textArea.append(record + "\n"));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);

                JOptionPane.showMessageDialog(this, scrollPane, "Transaction History", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
    }

    private void updateBalanceLabel() {
        balanceLabel.setText(String.format("Balance: ₹%.2f", user.getBalance()));
    }
}
