import javax.swing.ImageIcon;

public class Item {
    private String name;
    private String description;
    private ImageIcon image;

    public Item(String name, String description, ImageIcon image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getImage() {
        return image;
    }
}
