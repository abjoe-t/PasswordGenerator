import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * A complete, self-contained Random Password Generator in Java using Swing.
 *
 * This single class handles all logic and the graphical user interface:
 * - Creating the settings window (length, character types)
 * - Generating a secure random password
 * - Displaying the password
 * - Providing a "Copy to Clipboard" button
 */
public class PasswordGenerator extends JFrame implements ActionListener {

    // Character sets
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String CHAR_NUMBERS = "0123456789";
    private static final String CHAR_SYMBOLS = "!@#$%^&*()_+-=[]{}|;':,.<>?";

    // GUI Components
    private JSpinner lengthSpinner;
    private JCheckBox upperCheck, lowerCheck, numbersCheck, symbolsCheck;
    private JButton generateButton;
    private JTextField passwordField;
    private JButton copyButton;
    
    // Secure random number generator
    private SecureRandom random;

    /**
     * Constructor: Initializes the GUI and sets up the window.
     */
    public PasswordGenerator() {
        random = new SecureRandom();

        // Set up the main window (JFrame)
        setTitle("Random Password Generator");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Gaps between components

        // --- Options Panel (Center) ---
        JPanel optionsPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        
        // 1. Length Spinner
        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lengthPanel.add(new JLabel("Password Length:"));
        // Model for the spinner: default 12, min 4, max 128, step 1
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(12, 4, 128, 1);
        lengthSpinner = new JSpinner(spinnerModel);
        lengthSpinner.setFont(new Font("Arial", Font.PLAIN, 16));
        lengthPanel.add(lengthSpinner);
        optionsPanel.add(lengthPanel);

        // 2. Checkboxes
        lowerCheck = new JCheckBox("Include Lowercase (a-z)", true);
        upperCheck = new JCheckBox("Include Uppercase (A-Z)", true);
        numbersCheck = new JCheckBox("Include Numbers (0-9)", true);
        symbolsCheck = new JCheckBox("Include Symbols (!@#...)", true);
        optionsPanel.add(lowerCheck);
        optionsPanel.add(upperCheck);
        optionsPanel.add(numbersCheck);
        optionsPanel.add(symbolsCheck);
        
        add(optionsPanel, BorderLayout.CENTER);

        // --- Generate Button (South) ---
        generateButton = new JButton("Generate Password");
        generateButton.setFont(new Font("Arial", Font.BOLD, 18));
        generateButton.addActionListener(this);
        add(generateButton, BorderLayout.SOUTH);

        // --- Result Panel (North) ---
        JPanel resultPanel = new JPanel(new BorderLayout());
        passwordField = new JTextField("Click 'Generate' to create a password.");
        passwordField.setEditable(false);
        passwordField.setFont(new Font("Monospaced", Font.PLAIN, 18));
        
        copyButton = new JButton("Copy");
        copyButton.addActionListener(this);
        
        resultPanel.add(passwordField, BorderLayout.CENTER);
        resultPanel.add(copyButton, BorderLayout.EAST);
        add(resultPanel, BorderLayout.NORTH);

    }

    /**
     * This method is called whenever a button is clicked.
     * @param e The ActionEvent object containing details about the click.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            generatePassword();
        } else if (e.getSource() == copyButton) {
            copyToClipboard();
        }
    }

    /**
     * Generates a password based on the selected options.
     */
    private void generatePassword() {
        // 1. Get the desired length
        int length = (Integer) lengthSpinner.getValue();

        // 2. Build the set of allowed characters
        StringBuilder charSet = new StringBuilder();
        if (lowerCheck.isSelected()) charSet.append(CHAR_LOWER);
        if (upperCheck.isSelected()) charSet.append(CHAR_UPPER);
        if (numbersCheck.isSelected()) charSet.append(CHAR_NUMBERS);
        if (symbolsCheck.isSelected()) charSet.append(CHAR_SYMBOLS);

        // 3. Check if any character set is selected
        if (charSet.length() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one character type.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            return;
        }

        // 4. Build the password
        StringBuilder password = new StringBuilder(length);
        String availableChars = charSet.toString();
        
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(availableChars.length());
            password.append(availableChars.charAt(randomIndex));
        }

        // 5. Display the password
        passwordField.setText(password.toString());
    }

    /**
     * Copies the text from the password field to the system clipboard.
     */
    private void copyToClipboard() {
        String password = passwordField.getText();
        if (password.isEmpty() || password.equals("Click 'Generate' to create a password.")) {
            return; // Don't copy the placeholder text
        }
        
        // Use StringSelection and the system clipboard
        StringSelection stringSelection = new StringSelection(password);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        
        // Show a quick confirmation
        JOptionPane.showMessageDialog(this, 
            "Password copied to clipboard!", 
            "Copied", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * The main method. This is the entry point of the program.
     */
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            PasswordGenerator generator = new PasswordGenerator();
            generator.setVisible(true);
        });
    }
}
