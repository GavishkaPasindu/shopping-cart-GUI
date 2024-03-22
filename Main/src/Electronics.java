public class Electronics extends Product {
    private String brand;
    private int warrantyPeriod;

    // Constructor
    public Electronics(String productId, String name, int availableItems, double price, String brand, int warrantyPeriod) {
        // Call the super constructor with "Electronics" as the category
        super(productId, name, availableItems, price, "Electronics");
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }

    // Displaying Electronics details
    @Override
    public String toString() {
        return super.toString() +
                "Brand: " + brand + "\n" +
                "Warranty Period: " + warrantyPeriod + " weeks\n";
    }

    // Getters and setters
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }
}
