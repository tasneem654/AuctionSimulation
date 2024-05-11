import javax.swing.SwingUtilities;

public class AuctionGUIMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AuctionGUI::new);
    }
}