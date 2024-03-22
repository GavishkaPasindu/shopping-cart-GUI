import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private LocalDate accountCreationDate;
    private LocalTime accountCreationTime;
    // Add a list to track user purchases
    private List<Product> purchaseHistory;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.accountCreationDate = LocalDate.now();
        this.accountCreationTime = LocalTime.now();
        this.purchaseHistory = new ArrayList<>();
    }
    // Getter for purchaseHistory
    public List<Product> getPurchaseHistory() {
        return purchaseHistory;
    }

    // Setter for purchaseHistory
    public void setPurchaseHistory(List<Product> purchaseHistory) {
        this.purchaseHistory = purchaseHistory;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Method to add a purchase to the user's history
    public void addPurchase(Product purchase) {
        purchaseHistory.add(purchase);
    }

    // Method to check if the user has purchased before
    public boolean hasPurchasedBefore() {
        return purchaseHistory != null && !purchaseHistory.isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", accountCreationDate=" + accountCreationDate +
                ", accountCreationTime=" + accountCreationTime +
                '}';
    }
}
