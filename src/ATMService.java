import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ATMService {

    public User login(String userId, String pin) {
        String sql = "SELECT * FROM users WHERE user_id = ? AND pin = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userId);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                return new User(userId, pin, balance);
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public boolean withdraw(User user, double amount) {
        if (amount <= 0) return false;
        if (user.getBalance() < amount) return false;

        double newBalance = user.getBalance() - amount;
        if (updateBalance(user.getUserId(), newBalance)) {
            user.setBalance(newBalance);
            recordTransaction(user.getUserId(), "Withdraw", amount, "Withdrawn from account");
            return true;
        }
        return false;
    }

    public boolean deposit(User user, double amount) {
        if (amount <= 0) return false;

        double newBalance = user.getBalance() + amount;
        if (updateBalance(user.getUserId(), newBalance)) {
            user.setBalance(newBalance);
            recordTransaction(user.getUserId(), "Deposit", amount, "Deposited to account");
            return true;
        }
        return false;
    }

    private boolean updateBalance(String userId, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, userId);
            int rows = stmt.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Balance update error: " + e.getMessage());
            return false;
        }
    }

    // Record a transaction
    private void recordTransaction(String userId, String type, double amount, String details) {
        String sql = "INSERT INTO transactions (user_id, type, amount, details) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, type);
            stmt.setDouble(3, amount);
            stmt.setString(4, details);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Transaction recording error: " + e.getMessage());
        }
    }

    // Transfer funds between users
    public boolean transfer(User sender, String receiverId, double amount) {
        if (amount <= 0 || sender.getUserId().equals(receiverId)) return false;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Check sender balance
            if (sender.getBalance() < amount) {
                conn.rollback();
                conn.setAutoCommit(true);
                return false;
            }

            // Check if receiver exists
            String checkReceiverSql = "SELECT balance FROM users WHERE user_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkReceiverSql)) {
                checkStmt.setString(1, receiverId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false; // receiver not found
                }
                double receiverBalance = rs.getDouble("balance");

                // Update sender balance
                String updateSenderSql = "UPDATE users SET balance = ? WHERE user_id = ?";
                try (PreparedStatement updateSenderStmt = conn.prepareStatement(updateSenderSql)) {
                    updateSenderStmt.setDouble(1, sender.getBalance() - amount);
                    updateSenderStmt.setString(2, sender.getUserId());
                    updateSenderStmt.executeUpdate();
                }

                // Update receiver balance
                String updateReceiverSql = "UPDATE users SET balance = ? WHERE user_id = ?";
                try (PreparedStatement updateReceiverStmt = conn.prepareStatement(updateReceiverSql)) {
                    updateReceiverStmt.setDouble(1, receiverBalance + amount);
                    updateReceiverStmt.setString(2, receiverId);
                    updateReceiverStmt.executeUpdate();
                }

                // Record transactions
                recordTransaction(sender.getUserId(), "Transfer Sent", amount, "To: " + receiverId);
                recordTransaction(receiverId, "Transfer Received", amount, "From: " + sender.getUserId());

                conn.commit();
                conn.setAutoCommit(true);

                // Update sender object balance
                sender.setBalance(sender.getBalance() - amount);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Transfer error: " + e.getMessage());
            try {
                Connection conn = DBConnection.getConnection();
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            return false;
        }
    }

    // Retrieve transaction history for a user
    public List<String> getTransactionHistory(String userId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT type, amount, date, details FROM transactions WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Timestamp date = rs.getTimestamp("date");
                String details = rs.getString("details");

                String record = String.format("%s: ₹%.2f on %s (%s)", type, amount, date.toString(), details);
                history.add(record);
            }
        } catch (SQLException e) {
            System.out.println("Fetch history error: " + e.getMessage());
        }
        return history;
    }
}
