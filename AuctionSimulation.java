
import java.util.Random;

/**
 *
 * @author tasne
 */
public class AuctionSimulation {

    private static final int MAX_BIDS = 10;
    private static final long AUCTION_DURATION_MS = 30000; // 30 seconds

    public static void main(String[] args) {
        Item item = new Item("Item 1", "Description");
        Auction auction = new Auction(item, 100.0); // Starting bid amount

        // Create multiple bidder threads
        for (int i = 1; i <= 5; i++) {
            Thread bidderThread = new Thread(new Bidder("Bidder " + i, auction));
            bidderThread.start();
        }

        // Start the auction
        auction.start();

        // Wait for auction duration
        try {
            Thread.sleep(AUCTION_DURATION_MS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // End the auction
        auction.end();

        // Wait for all bidder threads to finish
        try {
            Thread.sleep(1000); // Wait for 1 second for bidders to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Auction ended. Final highest bid: $" + auction.getCurrentHighestBid());
        System.out.println("Winner: " + auction.getWinner());
    }

    static class Bidder implements Runnable {
        private String name;
        private Auction auction;
        private Random random = new Random();

        public Bidder(String name, Auction auction) {
            this.name = name;
            this.auction = auction;
        }

        @Override
        public void run() {
            while (!auction.isEnded()) {
                double bidAmount = auction.getCurrentHighestBid() + random.nextDouble() * 10; // Random bid amount
                boolean success = auction.placeBid(name, bidAmount);
                if (success) {
                    System.out.println(name + " placed a bid of $" + bidAmount);
                } else {
                    System.out.println(name + " failed to place a bid.");
                }

                try {
                    Thread.sleep(random.nextInt(5000)); // Random sleep time between bids
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Auction {
        private Item item;
        private double currentHighestBid;
        private String winner;
        private boolean ended = false;
        private int bidCount = 0;

        public Auction(Item item, double startingBid) {
            this.item = item;
            this.currentHighestBid = startingBid;
        }

        public synchronized boolean placeBid(String bidderName, double bidAmount) {
            if (!ended && bidAmount > currentHighestBid) {
                // Update current highest bid
                currentHighestBid = bidAmount;
                winner = bidderName;
                bidCount++;
                return true;
            } else {
                return false;
            }
        }

        public double getCurrentHighestBid() {
            return currentHighestBid;
        }

        public String getWinner() {
            return winner;
        }

        public synchronized void start() {
            System.out.println("Auction started.");
        }

        public synchronized void end() {
            ended = true;
        }

        public synchronized boolean isEnded() {
            return ended || bidCount >= MAX_BIDS;
        }
    }

    static class Item {
        private String name;
        private String description;

        public Item(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}