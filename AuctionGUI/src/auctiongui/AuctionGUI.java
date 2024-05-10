/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package auctiongui;

/**
 *
 * @author Rana
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Random;

public class AuctionGUI extends JFrame {
    private Auction auction;
    private Timer auctionTimer;  // Reference to the timer controlling the auction end
    private JTextArea bidLog = new JTextArea(10, 30);
    private JTextField itemNameField = new JTextField(20);
    private JTextArea itemDescriptionField = new JTextArea(2, 20);
    private JTextField startingBidField = new JTextField(10);
    private JTextField auctionDurationField = new JTextField(10);
    private JLabel currentBid = new JLabel("Current highest bid: $0.0");
    private JLabel winnerLabel = new JLabel("Winner: None");
    private JLabel itemImageLabel = new JLabel();
    private JButton startButton = new JButton("Start Auction");
    private JButton endButton = new JButton("End Auction");
    private JButton uploadButton = new JButton("Upload Image");

    public AuctionGUI() {
        super("Auction System");
        setupUI();
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        bidLog.setEditable(false);
        itemDescriptionField.setLineWrap(true);
        itemDescriptionField.setWrapStyleWord(true);

        uploadButton.addActionListener(this::uploadImage);
        startButton.addActionListener(this::setupAuction);
        endButton.addActionListener(this::endAuction);
        endButton.setEnabled(false);

        add(new JLabel("Item Name:"));
        add(itemNameField);
        add(new JLabel("Description:"));
        add(new JScrollPane(itemDescriptionField));
        add(new JLabel("Starting Bid ($):"));
        add(startingBidField);
        add(new JLabel("Duration (seconds):"));
        add(auctionDurationField);
        add(uploadButton);
        add(itemImageLabel);
        add(startButton);
        add(endButton);
        add(currentBid);
        add(new JScrollPane(bidLog));
        add(winnerLabel);

        setSize(600, 500);
        setVisible(true);
    }

    private void uploadImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedFile.getAbsolutePath());
            Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            itemImageLabel.setIcon(new ImageIcon(image));
            pack();
        }
    }

    private void setupAuction(ActionEvent e) {
        startButton.setEnabled(false);
        String itemName = itemNameField.getText().trim();
        String itemDescription = itemDescriptionField.getText().trim();
        double startingBid = Double.parseDouble(startingBidField.getText().trim());
        long duration = Long.parseLong(auctionDurationField.getText().trim()) * 1000; // Convert seconds to milliseconds

        Item item = new Item(itemName, itemDescription, (ImageIcon) itemImageLabel.getIcon());
        auction = new Auction(item, startingBid);
        startAuction(duration);
        startButton.setEnabled(false);
        endButton.setEnabled(true);
    }

    private void endAuction(ActionEvent e) {
        auction.end();
        winnerLabel.setText("Winner: " + auction.getWinner());
        endButton.setEnabled(false);
    }

    private void startAuction(long duration) {
    auction.start();
    for (int i = 1; i <= 5; i++) {
        new Thread(new Bidder("Bidder " + i, auction)).start();
    }

    if (auctionTimer != null) {
        auctionTimer.stop();  // Stop the existing timer if there is one
    }

    // Start a new timer
    auctionTimer = new Timer((int) duration, ev -> {
        auction.end();
        endButton.setEnabled(false);
    });
    auctionTimer.setRepeats(false);  // Ensure it doesn't repeat
    auctionTimer.start();
}

  

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AuctionGUI::new);
    }

    class Bidder implements Runnable {
        private String name;
        private Auction auction;
        private Random random = new Random();

    public Bidder(String name, Auction auction) {
    this.name = name;
    this.auction = auction;  // Correct the syntax error here
}


        @Override
        public void run() {
            while (!auction.isEnded()) {
                double bidAmount = auction.getCurrentHighestBid() + random.nextDouble() * 10; // Random bid amount
                boolean success = auction.placeBid(name, bidAmount);
                if (success) {
                    SwingUtilities.invokeLater(() -> {
                        bidLog.append(name + " placed a bid of $" + String.format("%.2f", bidAmount) + "\n");
                        currentBid.setText("Current highest bid: $" + String.format("%.2f", auction.getCurrentHighestBid()));
                    });
                }
                try {
                    Thread.sleep(random.nextInt(2000) + 500); // Random sleep time between bids
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Auction {
        private Item item;
        private double currentHighestBid;
        private String winner;
        private volatile boolean ended = false;

        public Auction(Item item, double startingBid) {
            this.item = item;
            this.currentHighestBid = startingBid;
        }

        public synchronized boolean placeBid(String bidderName, double bidAmount) {
            if (!ended && bidAmount > currentHighestBid) {
                currentHighestBid = bidAmount;
                winner = bidderName;
                return true;
            }
            return false;
        }

        public double getCurrentHighestBid() {
            return currentHighestBid;
        }

        public String getWinner() {
            return winner;
        }

        public synchronized void start() {
            ended = false;
            bidLog.append("Auction started for " + item.name + ": " + item.description + "\n");
        }

        public synchronized void end() {
          if (!ended) {  // Check if the auction has already ended
        ended = true;
        System.out.println("Auction has ended.");
        SwingUtilities.invokeLater(() -> {
            bidLog.append("Auction has ended.\n");
            if (winner != null) {
                winnerLabel.setText("Winner: " + winner);
            } else {
                winnerLabel.setText("No winner.");
            }
        });
    }
}

        public boolean isEnded() {
            return ended;
        }
    }

    class Item {
        private String name;
        private String description;
        private ImageIcon image;

        public Item(String name, String description, ImageIcon image) {
            this.name = name;
            this.description = description;
            this.image = image;
        }
    }
}
