#  ATM Interface

## Objective

The **ATM Interface** is a Java Swing-based desktop application simulating an ATM machine. It allows users to:
- Log in with their PIN or account credentials.
- View account balance.
- Deposit or withdraw money.
- Exit securely.

This project aims to mimic an ATM workflow and practice GUI programming with Swing.

---

## Tools & Technologies

- **Programming Language:** Java (JDK 8+)
- **GUI:** Java Swing
- **IDE:** NetBeans or IntelliJ IDEA

---

## Steps Performed

1. **Login Interface:**
   - Created a login screen using Swing where the user enters their PIN.

2. **Main Menu:**
   - Designed a menu for operations: balance inquiry, deposit, withdraw, and exit.
   - Added buttons and event listeners to handle user choices.

3. **Balance Handling:**
   - Implemented logic to update and display the current balance.

4. **Deposit/Withdraw:**
   - Developed input dialogs to enter amounts.
   - Updated balance after each transaction with validation (e.g., insufficient funds).

5. **Exit:**
   - Added confirmation dialogs to ensure secure exits.

6. **Exception Handling:**
   - Managed invalid inputs and UI errors gracefully.

---

## How to Run

1. **Compile the code:**
   ```bash
   javac ATMInterface.java

2. **Run the application:**
   ```bash
   java ATMInterface

3. **Use the ATM:**
-Log in using your PIN or account credentials.

-Perform actions such as checking balance, withdrawing, depositing, or exiting.

## Outcome
-A functional ATM simulation with a user-friendly Java Swing GUI.

-Practiced implementing GUI components, event handling, and basic business logic.

-Built understanding of state management in interactive desktop applications.
