import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ShoppingGUI {
    private JFrame frame;
    private JComboBox<String> categoryComboBox;
    private JTable productTable;
    private JTextArea productDetailsArea;
    private JButton addToCartButton, viewCartButton;
    private ShoppingCart shoppingCart;
    private WestminsterShoppingManager manager;
    private JLabel totalCostLabel;
    private JTextArea totalsDiscountsArea; // Declare as a class member variable

    public ShoppingGUI(WestminsterShoppingManager manager) {
        this.manager = manager;
        this.shoppingCart = new ShoppingCart();

        initializeUI();
        updateProductTable(manager.getProductList());
    }


    // Method to initialize the UI
    private void initializeUI() {
        // Create and configure the frame
        frame = new JFrame("Westminster Shopping Centre");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Top panel with category selection and shopping cart button
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        categoryComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Clothing"});
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.add(categoryComboBox);
        topPanel.add(comboBoxPanel, BorderLayout.CENTER);

        viewCartButton = new JButton("Shopping Cart");
        JPanel cartButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cartButtonPanel.add(viewCartButton);
        topPanel.add(cartButtonPanel, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table setup
        String[] columnNames = {"Product ID", "Product Name", "Category", "Price", "Info"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        TableColumnModel columnModel = productTable.getColumnModel();

        // Add preferred column widths
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(200);

        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Product details area setup
        productDetailsArea = new JTextArea(7, 20);
        productDetailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(productDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createTitledBorder("Selected Product - Details")
        ));


        contentPanel.add(detailsScrollPane, BorderLayout.SOUTH);

        // Add the content panel to the frame
        frame.add(contentPanel, BorderLayout.CENTER);

        // Add to Cart button at the bottom
        addToCartButton = new JButton("Add to Shopping Cart");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addToCartButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Default table cell renderer to change text color based on available stock
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Assume that the table model is a DefaultTableModel and the first column contains the product ID
                String productId = (String) table.getModel().getValueAt(row, 0);
                Product product = findProductById(productId);

                if (product != null) {
                    int availableItems = product.getAvailableItems();
                    if (availableItems < 3) {
                        c.setForeground(Color.RED);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        // Make the frame visible
        frame.setVisible(true);

        // Selection listener for the table
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedProductDetails();
            }
        });

        // Category combo box action listener
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            List<Product> filteredProducts = manager.getProductList().stream()
                    .filter(product -> {
                        if ("All".equals(selectedCategory)) {
                            return true;
                        } else if ("Electronics".equals(selectedCategory)) {
                            return product instanceof Electronics;
                        } else if ("Clothing".equals(selectedCategory)) {
                            return product instanceof Clothing;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
            updateProductTable(filteredProducts);
        });

        // Add to Cart button action listener
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow != -1) {
                    Product selectedProduct = findProductById((String) productTable.getValueAt(selectedRow, 0));
                    if (selectedProduct != null) {
                        shoppingCart.addProduct(selectedProduct);
                        JOptionPane.showMessageDialog(frame, "Product added to cart successfully!");
                    }
                }
            }
        });

        // View Cart button action listener
        viewCartButton.addActionListener(e -> displayShoppingCart());
    }

    private void displayShoppingCart() {
        // Create a new dialog for displaying the shopping cart
        JDialog cartDialog = new JDialog(frame, "Shopping Cart", true);
        cartDialog.setSize(600, 400);
        cartDialog.setLayout(new BorderLayout());

        // Define column names for the cart table
        String[] columnNames = {"Product Details", "Quantity", "Price"};


        // Create the table model for the cart
        DefaultTableModel cartModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;  // Only the "Quantity" column should be editable
            }
        };

        // Fill the table model with cart items
        for (Product product : shoppingCart.getProducts()) {
            String productDetails = productToDetailsString(product);
            int quantity = shoppingCart.getProductQuantity(product);
            double price = product.getPrice() * quantity;
            cartModel.addRow(new Object[]{productDetails, quantity, String.format("%.2f €", price)});
        }

        // Create the cart table with the defined model
        JTable cartTable = new JTable(cartModel);
        cartTable.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 1) {
                    int row = e.getFirstRow();
                    int newQuantity;
                    try {
                        newQuantity = Integer.parseInt(cartTable.getModel().getValueAt(row, 1).toString());
                        if (newQuantity <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid quantity.");
                        return;
                    }

                    Product product = shoppingCart.getProducts().get(row);
                    double newPrice = product.getPrice() * newQuantity;
                    cartTable.getModel().setValueAt(String.format("%.2f €", newPrice), row, 2);
                    shoppingCart.updateProductQuantity(product, newQuantity);
                    updateTotalCostLabel();
                }
            }
        });

        // Create a scroll pane for the cart table
        JScrollPane scrollPane = new JScrollPane(cartTable);
        cartDialog.add(scrollPane, BorderLayout.CENTER);

        // Setup totals and discounts area
        totalsDiscountsArea = new JTextArea(7, 20);
        totalsDiscountsArea.setEditable(false);
        JScrollPane totalsScrollPane = new JScrollPane(totalsDiscountsArea);
        totalsScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder("Totals and Discounts")));

        // Setup buttons for navigation and purchase
        JButton backButton = new JButton("Back to Shopping");
        backButton.addActionListener(e -> cartDialog.dispose());
        JButton purchaseButton = new JButton("Complete Purchase");
        purchaseButton.addActionListener(e -> completePurchase());
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.add(backButton);
        backButtonPanel.add(purchaseButton);

        // Initialize the total cost label
        totalCostLabel = new JLabel();
        updateTotalCostLabel();

        // Create a bottom panel for totals and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalsScrollPane, BorderLayout.CENTER);
        bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);
        cartDialog.add(bottomPanel, BorderLayout.SOUTH);

        // Set the dialog location and make it visible
        cartDialog.setLocationRelativeTo(frame);
        cartDialog.setVisible(true);
    }



    private void completePurchase() {
        // Check if the cart quantities are valid before proceeding
        if (!shoppingCart.validateCartQuantities()) {
            JOptionPane.showMessageDialog(frame,
                    "One or more items in your cart exceed the available quantity.",
                    "Quantity Error",
                    JOptionPane.ERROR_MESSAGE);
            return; // Stop the purchase process
        }

        // Check if the cart is empty
        if (shoppingCart.getProducts().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Your cart is empty!", "Purchase Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the current user
        User currentUser = manager.getCurrentUser();

        // Check if a user is logged in
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "No user is currently logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate the total cost of the purchase
        double totalCost = shoppingCart.calculateFinalTotal(currentUser);

        // Get the list of products in the cart
        List<Product> purchasedProducts = new ArrayList<>(shoppingCart.getProducts());

        // Iterate over each product in the shopping cart and update their available quantities
        for (Product cartProduct : purchasedProducts) {
            int purchasedQuantity = shoppingCart.getProductQuantity(cartProduct);
            Product mainListProduct = manager.findProductById(cartProduct.getProductId());
            if (mainListProduct != null) {
                int newAvailableQuantity = mainListProduct.getAvailableItems() - purchasedQuantity;
                mainListProduct.setAvailableItems(Math.max(0, newAvailableQuantity));
            }
        }

        // Create a new Purchase instance and add it to the user's purchase history
        LocalDate purchaseDate = LocalDate.now();
        Product newPurchase = new Product(purchasedProducts, totalCost, purchaseDate);
        currentUser.addPurchase(newPurchase);

        // Save the updated user data with the new purchase
        manager.saveUsers();

        // Clear the shopping cart after purchase
        shoppingCart.clearCart();

        // Update the product table to reflect the new quantities
        updateProductTable(manager.getProductList());

        // Display a success message
        JOptionPane.showMessageDialog(frame, "Purchase completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Save the updated product list
        manager.saveProducts(); // Save products after updating their quantities
    }


    // Update the total cost label
    private void updateTotalCostLabel() {
        // Get the current user
        User currentUser = manager.getCurrentUser();

        // Calculate the subtotal of the items in the shopping cart
        double subtotal = shoppingCart.calculateSubtotal();

        // Calculate the final total with discounts applied, using the current user
        double finalTotal = shoppingCart.calculateFinalTotal(currentUser);

        // Get the first purchase discount percentage
        double firstPurchaseDiscount = shoppingCart.getFirstPurchaseDiscount();

        // Get the category discount percentage for purchasing three items in the same category
        double categoryDiscount = shoppingCart.getCategoryDiscount();

        // Create a formatted string with the calculated values
        String totalsText = String.format("Subtotal: %.2f €\n" +
                        "First Purchase Discount (10%%): -%.2f €\n" +
                        "Three Items in the same Category Discount (20%%): -%.2f €\n" +
                        "Final Total: %.2f €",
                subtotal, firstPurchaseDiscount, categoryDiscount, finalTotal);

        // Set the formatted text to the totalsDiscountsArea JTextArea
        totalsDiscountsArea.setText(totalsText);
    }

    // Show details of the selected product
    private void showSelectedProductDetails() {
        // Get the index of the selected row in the productTable
        int selectedRow = productTable.getSelectedRow();

        // Check if a valid row is selected
        if (selectedRow != -1) {
            // Get the Product ID from the selected row
            String productId = (String) productTable.getValueAt(selectedRow, 0);

            // Find the product by its ID
            Product selectedProduct = findProductById(productId);

            // Check if the selectedProduct is not null
            if (selectedProduct != null) {
                // Format the product details as a string and set it to productDetailsArea
                productDetailsArea.setText(productToDetailsString(selectedProduct));
            }
        }
    }

    // Helper method to find a product by ID
    private Product findProductById(String productId) {
        // Iterate through the list of products in the manager
        for (Product product : manager.getProducts()) {
            // Check if the product's ID matches the provided productId
            if (product.getProductId().equals(productId)) {
                // Return the found product
                return product;
            }
        }

        // If no product is found, return null (or handle the error as needed)
        return null;
    }

    // Helper method to format product details as a string
    private String productToDetailsString(Product product) {
        // Initialize the details string with common product information
        String details =
                "Product Id: " + product.getProductId() + "\n" +
                        "Category: " + product.getCategory() + "\n" +
                        "Name: " + product.getName() + "\n";

        // Check the type of product (Electronics or Clothing)
        if (product instanceof Electronics) {
            // If it's Electronics, cast it to Electronics and add brand and warranty information
            Electronics electronics = (Electronics) product;
            details += "Brand: " + electronics.getBrand() + "\n" +
                    "Warranty: " + electronics.getWarrantyPeriod() + " weeks\n";
        } else if (product instanceof Clothing) {
            // If it's Clothing, cast it to Clothing and add size and color information
            Clothing clothing = (Clothing) product;
            details += "Size: " + clothing.getSize() + "\n" +
                    "Color: " + clothing.getColor() + "\n";
        }

        // Add the available items information to the details
        details += "Items Available: " + product.getAvailableItems();

        // Return the formatted details string
        return details;
    }

    // Update the product table with the provided list of products
    public void updateProductTable(List<Product> products) {
        // Sort products alphabetically by their name
        Collections.sort(products, Comparator.comparing(Product::getProductId));
        // Get the model of the productTable
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();

        // Clear the existing data in the table model
        model.setRowCount(0);

        // Iterate through the list of products and populate the table with product information
        for (Product product : products) {
            String info = "";

            // Check the type of product (Electronics or Clothing) and format additional information
            if (product instanceof Electronics) {
                Electronics electronics = (Electronics) product;
                info = "Brand: " + electronics.getBrand() + ", Warranty: " + electronics.getWarrantyPeriod() + " weeks";
            } else if (product instanceof Clothing) {
                Clothing clothing = (Clothing) product;
                info = "Size: " + clothing.getSize() + ", Color: " + clothing.getColor();
            }

            // Add a row to the table with product details
            model.addRow(new Object[]{product.getProductId(), product.getName(),
                    product.getCategory(), product.getPrice(), info});
        }
    }
}
