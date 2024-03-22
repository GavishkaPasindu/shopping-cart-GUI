import javax.swing.*;
import java.io.*;
import java.util.*;

public class WestminsterShoppingManager implements ShoppingManager {
    private List<Product> productList;
    private List<User> userList;
    private final int MAX_PRODUCTS = 50;
    private static final String PRODUCT_FILE_NAME = "products.txt";
    private static final String USER_FILE_NAME = "users.txt";
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    // When retrieving a user
    public User getUserByUsername(String username) {
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                if (user.getPurchaseHistory() == null) {
                    user.setPurchaseHistory(new ArrayList<>()); // Initialize if null
                }
                return user;
            }
        }
        return null;
    }

    private ShoppingGUI guiInstance;
    public WestminsterShoppingManager() {
        productList = new ArrayList<>();
        userList = new ArrayList<>();
        loadProducts();
        loadUsers();
    }
    public void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("-------------------------------------------------");
        System.out.println("Product Management Menu:");
        System.out.println("1. Add Product");
        System.out.println("2. Delete Product");
        System.out.println("3. Display Products");
        System.out.println("4. Save");
        System.out.println("5. GUI ");
        System.out.println("6. Exit");
        System.out.println("-------------------------------------------------");
        System.out.print("Enter your selection: ");

        // Check if the next input is an integer
        String input = scanner.nextLine();
        // Remove all spaces from the input and parse it as an integer
        try {
            int option = Integer.parseInt(input.replaceAll("\\s+", ""));
            handleMenuSelection(option);
        } catch (NumberFormatException e) {
            // Handle case where the input is not a valid integer
            System.out.println("Invalid input. Please enter a valid number.");
            displayMenu(); // Redisplay the menu
        }
    }


    private void handleMenuSelection(int selection) {
        switch (selection) {
            case 1:
                addProduct();
                break;
            case 2:
                deleteProduct();
                break;
            case 3:
                displayProducts();
                break;
            case 4:
                saveProducts();
                saveUsers();
                break;
            case 5:
                invokeGUI();
                break;
            case 6:
                System.out.println("Exiting...");
                saveUsers();
                System.exit(0);
                break;

            default:
                System.out.println("Invalid selection.");
                break;
        }
        displayMenu();
    }
    private void invokeGUI() {
        WestminsterShoppingManager manager = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserGUI(manager); // Open the user login GUI
            }
        });
    }


    // Inside WestminsterShoppingManager class
public boolean validateUser(String username, String password) {
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }



    public boolean usernameExists(String username) {
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public void addProduct() {
        if (productList.size() >= MAX_PRODUCTS) {
            System.out.println("The system can't hold more than " + MAX_PRODUCTS + " products.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select product type to add:");
        System.out.println("1. Electronics");
        System.out.println("2. Clothing");


        int choice;
        while (true) {
            System.out.print("Enter your choice (1 for Electronics, 2 for Clothing): ");
            String input = scanner.nextLine().trim(); // Read the entire line and remove leading/trailing spaces
            if (input.matches("^\\d+$")) {
                // Check if the input contains only digits
                choice = Integer.parseInt(input);
                if (choice == 1 || choice == 2) {
                    break; // Valid choice, exit the loop
                } else {
                    System.out.println("Invalid choice. Please enter 1 for Electronics or 2 for Clothing.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }



        String productIdPrefix = (choice == 1) ? "E" : (choice == 2) ? "C" : "";
        String productId, name, brand, size, color;

        while (true) {
            System.out.print("Enter Product ID Ex: (" + productIdPrefix + "0001): ");
            productId = scanner.nextLine().toUpperCase(); // Convert to uppercase for validation
            if (isValidProductId(productId, productIdPrefix)) {
                break; // Exit the loop if product ID is valid
            }
            System.out.println("Invalid Product ID.");
        }



        System.out.print("Enter Product Name: ");
        while (true) {
            name = scanner.nextLine().trim(); // Trim leading and trailing spaces
            if (name.matches("[a-zA-Z ]+")) { // Regex to check if the name contains only letters and spaces
                break; // Valid input, exit the loop
            } else {
                System.out.println("Invalid product name. Please enter a name using only letters and spaces.");
                System.out.print("Enter Product Name: ");
            }
        }


        System.out.print("Enter Number of Available Items: ");
        int availableItems = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                String availableItemsInput = scanner.nextLine();
                availableItems = Integer.parseInt(availableItemsInput.replaceAll("\\s+", ""));
                if (availableItems < 0) {
                    System.out.println("Number of available items cannot be negative. Please enter a valid number.");
                } else {
                    validInput = true; // Valid input, break the loop
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }

        System.out.print("Enter Price: ");
        double price = 0;
        boolean validPriceInput = false;

        while (!validPriceInput) {
            try {
                String priceInput = scanner.nextLine();
                // Removing all spaces and parsing the input as a double
                price = Double.parseDouble(priceInput.replaceAll("\\s+", ""));
                if (price < 0) {
                    System.out.println("Price cannot be negative. Please enter a valid number.");
                } else {
                    validPriceInput = true; // Valid input, break the loop
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }


        Product product = null;

        switch (choice) {
            case 1: // Electronics
                System.out.print("Enter Brand: ");
                while (true) {
                    brand = scanner.nextLine().trim();
                    if (brand.matches("[a-zA-Z ]+")) { // Regex for letters and spaces
                        break;
                    } else {
                        System.out.println("Invalid brand. Please enter a valid brand name.");
                    }
                }

                System.out.print("Enter Warranty Period (in weeks): ");
                int warrantyPeriod;
                while (true) {
                    try {
                        String warrantyPeriodInput = scanner.nextLine().trim();
                        warrantyPeriod = Integer.parseInt(warrantyPeriodInput);
                        if (warrantyPeriod > 0) {
                            System.out.println("-------------------------------------------------");
                            break;
                        } else {
                            System.out.println("Invalid warranty period. Please enter a positive integer.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                product = new Electronics(productId, name, availableItems, price, brand, warrantyPeriod);
                break;
            case 2: // Clothing
                System.out.print("Enter Size (e.g., S, M, L, XL, XXL): ");

                while (true) {
                    size = scanner.nextLine().trim().toUpperCase(); // Convert input to uppercase
                    if (size.matches("S|M|L|XL|XXL")) { // Validation against uppercase values

                        break;
                    } else {
                        System.out.println("Invalid size. Please enter a valid size (e.g., S, M, L, XL, XXL).");
                    }
                }

                System.out.print("Enter Color: ");

                while (true) {
                    color = scanner.nextLine().trim();
                    if (color.matches("[a-zA-Z]+")) { // Simple check to ensure color is a word
                        System.out.println("-------------------------------------------------");
                        break;
                    } else {
                        System.out.println("Invalid color. Please enter a valid color name.");
                    }
                }

                product = new Clothing(productId, name, availableItems, price, size, color);
                break;
            default:
                System.out.println("Invalid product type selected.");
                return;
        }

        if (product != null) {
            productList.add(product);
            System.out.println("Product added successfully: " + product);

            // Now update the GUI with the new product details
            if (guiInstance != null) { // guiInstance is an instance of ShoppingGUI
                guiInstance.updateProductTable(productList);

            }
        }
    }

    private boolean isValidProductId(String productId, String prefix) {
        // Check if productId is non-null and not empty, and starts with the correct prefix
        if (productId == null || productId.trim().isEmpty() || !productId.startsWith(prefix)) {
            return false;
        }

        // Check if the rest of the productId (excluding the prefix) consists of exactly 4 digits
        String numericPart = productId.substring(1); // Extract part after the prefix
        if (!numericPart.matches("\\d{4}")) { // Regex to check for exactly 4 digits
            return false;
        }

        // Check if a product with this ID already exists
        for (Product p : productList) {
            if (p.getProductId().equals(productId)) {
                System.out.println("A product with this ID already exists.");
                return false;
            }
        }

        return true; // productId is valid
    }
    private boolean isValidProductIdFormat(String productId) {
        // You can customize this regex pattern to match your product ID format
        String regexPattern = "[A-Za-z0-9]+";
        return productId.matches(regexPattern);
    }


    public void loadUsers() {
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE_NAME))) {
        userList = (List<User>) ois.readObject();
        System.out.println("Users loaded successfully.");
    } catch (IOException | ClassNotFoundException e) {
        System.out.println("Error loading users: " + e.getMessage());
    }
}

    public void addUser(User newUser) {
        // Check if a user with the same username already exists
        for (User user : userList) {
            if (user.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                System.out.println("A user with the username '" + newUser.getUsername() + "' already exists.");
                return;
            }
        }

        // Add the new user to the list
        newUser.setPurchaseHistory(new ArrayList<>()); // Initialize purchase history
        userList.add(newUser);
        System.out.println("New user added successfully: " + newUser);
    }

    public Product findProductById(String productId) {
        for (Product product : productList) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public void deleteProduct() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the product ID of the product to delete: ");
        String productId = scanner.nextLine().toUpperCase();;

        // Validate the product ID format
        if (!isValidProductIdFormat(productId)) {
            System.out.println("Invalid Product ID format.");
            return;
        }

        // Find the product with the given ID
        Optional<Product> productToRemove = productList.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst();

        // Remove the product if it exists
        if (productToRemove.isPresent()) {
            productList.remove(productToRemove.get());
            System.out.println("Product with ID " + productId + " has been deleted.");
            System.out.println("Total number of products now: " + productList.size());
        } else {
            System.out.println("Product with ID " + productId + " not found.");
        }
    }


    public void displayProducts() {
        // Logic to display all products, sorted alphabetically by product ID.
        productList.stream()
                .sorted(Comparator.comparing(Product::getProductId))
                .forEach(System.out::println);
    }

    public void saveProducts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRODUCT_FILE_NAME))) {
            oos.writeObject(productList);
            System.out.println("Products saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving products: " + e.getMessage());
        }
    }

    public void loadProducts() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRODUCT_FILE_NAME))) {
            productList = (List<Product>) ois.readObject();
            System.out.println("Products loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_NAME))) {
            oos.writeObject(userList);
            System.out.println("Users saved successfully.");
        } catch (IOException e) {
            e.printStackTrace(); // This will print the stack trace to help identify the issue
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
    public List<Product> getProducts() {
        return productList;
    }





    public List<Product> getProductList() {
        return productList;
    }

}