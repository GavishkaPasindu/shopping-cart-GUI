import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Product implements Serializable {
    private String productId;
    private String name;
    private int availableItems;
    private double price;
    private LocalDate dateAdded;
    private String category;
    private List<Product> products;
    private double totalCost;
    private LocalDate purchaseDate;

    // Constructor for creating a Purchase instance
    public Product(List<Product> products, double totalCost, LocalDate purchaseDate) {
        this.products = products;
        this.totalCost = totalCost;
        this.purchaseDate = purchaseDate;
    }

    public Product(String productId, String name, int availableItems, double price, String category) {
        this.productId = productId;
        this.name = name;
        this.availableItems = availableItems;
        this.price = price;
        this.dateAdded = LocalDate.now();
        this.category = category;
    }

    // Getters and setters

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getAvailableItems() { return availableItems; }
    public double getPrice() { return price; }
    public LocalDate getDateAdded() { return dateAdded; }
    public String getCategory() { return category; }

    // Method to set available items (if needed)
    public void setAvailableItems(int availableItems) {
        this.availableItems = availableItems;
    }

    // Method to set price (if needed)
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product ID: " + productId + "\n" +
                "Name: " + name + "\n" +
                "Category: " + category + "\n" +
                "Available Items: " + availableItems + "\n" +
                "Price: " + price + "\n" +
                "Date Added: " + dateAdded + "\n";
    }
}
