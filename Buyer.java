import javax.swing.SwingUtilities;
import java.util.Random;

class Buyer implements Runnable {
    private String name;
    private Auction auction;
    private AuctionGUI auctionGUI; // Add this member variable
    private Random random = new Random();

    public Buyer(String name, Auction auction, AuctionGUI auctionGUI) { 
        this.name = name;
        this.auction = auction;
        this.auctionGUI = auctionGUI; // Initialize auctionGUI
    }

    @Override
    public void run() {
        while (!auction.isEnded()) {
            double bidAmount = auction.getCurrentHighestBid() + random.nextDouble() * 10; // Random bid amount
            boolean success = auction.placeBid(name, bidAmount);
            if (success) {
                SwingUtilities.invokeLater(() -> {
                    auctionGUI.updateBidLog(name, bidAmount); // Use auctionGUI reference to call updateBidLog
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
