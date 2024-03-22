public class Clothing extends Product {
    private String size;
    private String color;

    // Constructor
    public Clothing(String productId, String name, int availableItems, double price, String size, String color) {
        // Call the super constructor with "Clothing" as the category
        super(productId, name, availableItems, price, "Clothing");
        this.size = size;
        this.color = color;
    }

    // Displaying Clothing details
    @Override
    public String toString() {
        return super.toString() +
                "Size: " + size + "\n" +
                "Color: " + color + "\n";
    }

    // Getters and setters
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
