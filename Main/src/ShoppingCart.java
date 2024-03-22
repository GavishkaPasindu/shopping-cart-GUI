import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;
public class ShoppingCart {
    private Map<Product, Integer> productQuantities;
    private boolean isFirstPurchase;
    private double categoryDiscount = 0.0;
    private double firstPurchaseDiscount = 0.0;
    private double subtotal = 0.0;
    public List<Product> getProducts() {
        return new ArrayList<>(productQuantities.keySet());
    }

    public ShoppingCart() {
        this.productQuantities = new HashMap<>();
        this.isFirstPurchase = false; // Default value, change if necessary
    }



    public void addProduct(Product product) {
        productQuantities.merge(product, 1, Integer::sum);
    }


    public double calculateSubtotal() {
        subtotal = productQuantities.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        return subtotal;
    }

    public void clearCart() {
        productQuantities.clear();
        categoryDiscount = 0.0;
        firstPurchaseDiscount = 0.0;
        subtotal = 0.0;
    }
    // Method to validate quantities in the cart
    public boolean validateCartQuantities() {
        for (Map.Entry<Product, Integer> entry : productQuantities.entrySet()) {
            Product product = entry.getKey();
            int quantityInCart = entry.getValue();
            int availableQuantity = product.getAvailableItems();

            if (quantityInCart > availableQuantity) {
                System.out.println("Validation failed for " + product.getName());
                return false; // Quantity in cart exceeds available stock
            }
        }
        System.out.println("All items in cart are valid.");
        return true; // All quantities are valid
    }


    public double calculateFinalTotal(User currentUser) {
        subtotal = calculateSubtotal();
        double total = subtotal;

        // Calculate category discount
        if (CategoryDiscountCondition()) {
            categoryDiscount = subtotal * 0.2; // 20% category discount
            total -= categoryDiscount;
        } else {
            categoryDiscount = 0.0;
        }

        // Calculate first purchase discount
        if (currentUser != null && !currentUser.hasPurchasedBefore()) {
            firstPurchaseDiscount = subtotal * 0.1; // 10% first purchase discount
            total -= firstPurchaseDiscount;
        } else {
            firstPurchaseDiscount = 0.0;
        }

        return total;
    }

    public int getProductQuantity(Product product) {
        return productQuantities.getOrDefault(product, 0);
    }

    public void updateProductQuantity(Product product, int quantity) {
        if (quantity > 0) {
            // Check if the requested quantity exceeds available stock
            int availableQuantity = product.getAvailableItems();
            if (quantity > availableQuantity) {
                // Display GUI message
                JOptionPane.showMessageDialog(null,
                        "Requested quantity exceeds available stock.",
                        "Quantity Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                productQuantities.put(product, quantity);
            }
        } else {
            productQuantities.remove(product);
        }
    }

    private boolean CategoryDiscountCondition() {
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Product product : productQuantities.keySet()) {
            String category = product.getCategory();
            categoryCounts.merge(category, productQuantities.get(product), Integer::sum);
        }
        return categoryCounts.values().stream().anyMatch(count -> count >= 3);
    }

    public double getCategoryDiscount() {
        return categoryDiscount;
    }

    public double getFirstPurchaseDiscount() {
        return firstPurchaseDiscount;
    }

}
