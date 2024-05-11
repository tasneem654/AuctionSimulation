import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class AuctionGUI extends JFrame {
    private Auction auction;
    private Timer auctionTimer;
    private JTextArea bidLog = new JTextArea(10, 30);
    private JTextField itemNameField = new JTextField(20);
    private JTextArea itemDescriptionField = new JTextArea(2, 20);
    private JTextField startingBidField = new JTextField(20);
    private JTextField auctionDurationField = new JTextField(20);
    private JLabel currentBid = new JLabel("Current highest bid: $0.0");
    private JLabel winnerLabel = new JLabel("Winner: None");
    private JLabel itemImageLabel = new JLabel();
    private JButton startButton = new JButton("Start Auction");
    private JButton uploadButton = new JButton("Upload Image");

    public AuctionGUI() {
        super("Auction System");
        setupUI();
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5)); // Grid layout for inputs
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        bidLog.setEditable(false);
        itemDescriptionField.setLineWrap(true);
        itemDescriptionField.setWrapStyleWord(true);

        uploadButton.addActionListener(this::uploadImage);
        startButton.addActionListener(this::setupAuction);

        inputPanel.add(new JLabel("Item Name:"));
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(itemDescriptionField);
        inputPanel.add(new JLabel("Starting Bid ($):"));
        inputPanel.add(startingBidField);
        inputPanel.add(new JLabel("Duration (seconds):"));
        inputPanel.add(auctionDurationField);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.add(uploadButton);
        buttonPanel.add(startButton);

        // Create a panel to hold the input panel and button panel
        JPanel inputButtonPanel = new JPanel(new BorderLayout());
        inputButtonPanel.add(inputPanel, BorderLayout.CENTER);
        inputButtonPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel auctionPanel = new JPanel(new BorderLayout());
        auctionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        auctionPanel.add(itemImageLabel, BorderLayout.WEST); // Align image to the left
        auctionPanel.add(currentBid, BorderLayout.NORTH);
        auctionPanel.add(new JScrollPane(bidLog), BorderLayout.CENTER);
        auctionPanel.add(winnerLabel, BorderLayout.SOUTH);

        // Create a panel to hold the inputButtonPanel and auctionPanel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputButtonPanel, BorderLayout.NORTH);
        mainPanel.add(auctionPanel, BorderLayout.CENTER);

        add(mainPanel);

        setSize(800, 600); // Adjusted size for better visibility
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    private void uploadImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
            Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Adjusted image size
            itemImageLabel.setIcon(new ImageIcon(image));
            pack();
        }
    }


    private void setupAuction(ActionEvent e) {
        startButton.setEnabled(false);
        String itemName = itemNameField.getText().trim();
        String itemDescription = itemDescriptionField.getText().trim();
        String startingBidText = startingBidField.getText().trim();
        String auctionDurationText = auctionDurationField.getText().trim();

        // Check for empty inputs
        if (itemName.isEmpty() || itemDescription.isEmpty() || startingBidText.isEmpty() || auctionDurationText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            startButton.setEnabled(true); // Enable start button again
            return;
        }

        try {
            double startingBid = Double.parseDouble(startingBidText);
            long duration = Long.parseLong(auctionDurationText) * 1000; // Convert seconds to milliseconds

            Item item = new Item(itemName, itemDescription, (ImageIcon) itemImageLabel.getIcon());
            auction = new Auction(item, startingBid, this);
            startAuction(duration);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input format for starting bid or auction duration.", "Error", JOptionPane.ERROR_MESSAGE);
            startButton.setEnabled(true); // Enable start button again
        }
    }

    private void startAuction(long duration) {
        auction.start();
        for (int i = 1; i <= 5; i++) {
            new Thread(new Buyer("Buyer " + i, auction, this)).start(); 
        }

        if (auctionTimer != null) {
            auctionTimer.stop();  // Stop the existing timer if there is one
        }

        // Start a new timer
        auctionTimer = new Timer((int) duration, ev -> {
            auction.end();
        });
        auctionTimer.setRepeats(false);  // Ensure it doesn't repeat
        auctionTimer.start();
    }

    public void setWinnerLabel(String text) {
        winnerLabel.setText(text);
    }

    public void appendBidLog(String log) {
        bidLog.append(log);
    }

    public void updateBidLog(String bidderName, double bidAmount) {
        appendBidLog(bidderName + " placed a bid of $" + String.format("%.2f", bidAmount) + "\n");
        currentBid.setText("Current highest bid: $" + String.format("%.2f", auction.getCurrentHighestBid()));
    }
}
