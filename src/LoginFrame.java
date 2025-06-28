import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField pinField;
    private JButton loginButton;
    private ATMService atmService;

    public LoginFrame() {
        atmService = new ATMService();

        setTitle("ATM Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        panel.add(userIdField);

        panel.add(new JLabel("PIN:"));
        pinField = new JPasswordField();
        panel.add(pinField);

        loginButton = new JButton("Login");
        panel.add(new JLabel());  // empty label for spacing
        panel.add(loginButton);

        add(panel);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText();
                String pin = new String(pinField.getPassword());

                User user = atmService.login(userId, pin);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login successful! Welcome, " + user.getUserId(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    // Open main ATM frame and close login
                    new MainATMFrame(user, atmService).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Invalid User ID or PIN.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
