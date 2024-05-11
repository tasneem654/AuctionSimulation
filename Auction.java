
import javax.swing.*;

public class Auction {
    private Item item;
    private double currentHighestBid;
    private String winner;
    private volatile boolean ended = false;
    private AuctionGUI auctionGUI;

    public Auction(Item item, double startingBid, AuctionGUI auctionGUI) {
        this.item = item;
        this.currentHighestBid = startingBid;
        this.auctionGUI = auctionGUI;
    }

    public synchronized boolean placeBid(String bidderName, double bidAmount) {
        if (!ended && bidAmount > currentHighestBid) {
            currentHighestBid = bidAmount;
            winner = bidderName;
            SwingUtilities.invokeLater(() -> {
                auctionGUI.updateBidLog(bidderName, bidAmount);
            });
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
        auctionGUI.appendBidLog("Auction started for " + item.getName() + ": " + item.getDescription() + "\n");
    }

    public synchronized void end() {
        if (!ended) {  // Check if the auction has already ended
            ended = true;
            System.out.println("Auction has ended.");
            SwingUtilities.invokeLater(() -> {
                auctionGUI.appendBidLog("Auction has ended.\n");
                if (winner != null) {
                    auctionGUI.setWinnerLabel("Winner: " + winner);
                } else {
                    auctionGUI.setWinnerLabel("No winner.");
                }
            });
        }
    }

    public boolean isEnded() {
        return ended;
    }
}
