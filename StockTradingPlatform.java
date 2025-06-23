import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StockTradingPlatform {
    private Map<String, Stock> stocks;
    private Map<String, User> users;
    private List<Transaction> transactions;
    private Scanner scanner;
    private User currentUser;
    
    public StockTradingPlatform() {
        stocks = new HashMap<>();
        users = new HashMap<>();
        transactions = new ArrayList<>();
        scanner = new Scanner(System.in);
        initializeStocks();
    }
    
    // Stock class
    static class Stock {
        private String symbol;
        private String name;
        private double price;
        private double previousPrice;
        private List<Double> priceHistory;
        
        public Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
            this.previousPrice = price;
            this.priceHistory = new ArrayList<>();
            this.priceHistory.add(price);
        }
        
        public void updatePrice(double newPrice) {
            this.previousPrice = this.price;
            this.price = newPrice;
            this.priceHistory.add(newPrice);
        }
        
        public double getPriceChange() {
            return price - previousPrice;
        }
        
        public double getPriceChangePercent() {
            return ((price - previousPrice) / previousPrice) * 100;
        }
        
        // Getters
        public String getSymbol() { return symbol; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public List<Double> getPriceHistory() { return priceHistory; }
    }
    
    // User class
    static class User {
        private String username;
        private double balance;
        private Map<String, Integer> portfolio;
        
        public User(String username, double initialBalance) {
            this.username = username;
            this.balance = initialBalance;
            this.portfolio = new HashMap<>();
        }
        
        public boolean buyStock(String symbol, int quantity, double price) {
            double totalCost = quantity * price;
            if (balance >= totalCost) {
                balance -= totalCost;
                portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + quantity);
                return true;
            }
            return false;
        }
        
        public boolean sellStock(String symbol, int quantity, double price) {
            int currentShares = portfolio.getOrDefault(symbol, 0);
            if (currentShares >= quantity) {
                balance += quantity * price;
                portfolio.put(symbol, currentShares - quantity);
                if (portfolio.get(symbol) == 0) {
                    portfolio.remove(symbol);
                }
                return true;
            }
            return false;
        }
        
        public double getPortfolioValue(Map<String, Stock> stocks) {
            double totalValue = balance;
            for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
                String symbol = entry.getKey();
                int quantity = entry.getValue();
                if (stocks.containsKey(symbol)) {
                    totalValue += quantity * stocks.get(symbol).getPrice();
                }
            }
            return totalValue;
        }
        
        // Getters
        public String getUsername() { return username; }
        public double getBalance() { return balance; }
        public Map<String, Integer> getPortfolio() { return portfolio; }
    }
    
    // Transaction class
    static class Transaction {
        private String username;
        private String type;
        private String symbol;
        private int quantity;
        private double price;
        private LocalDateTime timestamp;
        
        public Transaction(String username, String type, String symbol, int quantity, double price) {
            this.username = username;
            this.type = type;
            this.symbol = symbol;
            this.quantity = quantity;
            this.price = price;
            this.timestamp = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            return String.format("%s | %s | %s | %d shares @ $%.2f | %s",
                timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                type, symbol, quantity, price, username);
        }
    }
    
    private void initializeStocks() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 150.00));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 2500.00));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 300.00));
        stocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 800.00));
        stocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 3200.00));
        stocks.put("NVDA", new Stock("NVDA", "NVIDIA Corp.", 220.00));
        stocks.put("META", new Stock("META", "Meta Platforms Inc.", 320.00));
        stocks.put("NFLX", new Stock("NFLX", "Netflix Inc.", 400.00));
    }
    
    public void simulateMarketMovement() {
        Random random = new Random();
        for (Stock stock : stocks.values()) {
            // Simulate price movement (-5% to +5%)
            double changePercent = (random.nextDouble() - 0.5) * 0.1;
            double newPrice = stock.getPrice() * (1 + changePercent);
            stock.updatePrice(Math.max(newPrice, 1.0)); // Minimum price of $1
        }
    }
    
    public void displayMarketData() {
        System.out.println("\n=== MARKET DATA ===");
        System.out.println("Symbol\tName\t\t\tPrice\t\tChange\t\tChange%");
        System.out.println("========================================================================");
        
        for (Stock stock : stocks.values()) {
            double change = stock.getPriceChange();
            double changePercent = stock.getPriceChangePercent();
            String changeStr = String.format("%+.2f", change);
            String changePercentStr = String.format("%+.2f%%", changePercent);
            
            System.out.printf("%-6s\t%-20s\t$%-10.2f\t%-10s\t%s%n",
                stock.getSymbol(),
                stock.getName(),
                stock.getPrice(),
                changeStr,
                changePercentStr
            );
        }
    }
    
    public void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        if (users.containsKey(username)) {
            System.out.println("Username already exists!");
            return;
        }
        
        System.out.print("Enter initial balance: $");
        double balance = scanner.nextDouble();
        scanner.nextLine();
        
        users.put(username, new User(username, balance));
        System.out.println("User registered successfully!");
    }
    
    public void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        if (users.containsKey(username)) {
            currentUser = users.get(username);
            System.out.println("Login successful! Welcome, " + username);
        } else {
            System.out.println("User not found!");
        }
    }
    
    public void buyStock() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }
        
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().toUpperCase();
        
        if (!stocks.containsKey(symbol)) {
            System.out.println("Stock not found!");
            return;
        }
        
        Stock stock = stocks.get(symbol);
        System.out.printf("Current price of %s: $%.2f%n", symbol, stock.getPrice());
        System.out.print("Enter quantity to buy: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        
        double totalCost = quantity * stock.getPrice();
        System.out.printf("Total cost: $%.2f%n", totalCost);
        System.out.printf("Your balance: $%.2f%n", currentUser.getBalance());
        
        if (currentUser.buyStock(symbol, quantity, stock.getPrice())) {
            transactions.add(new Transaction(currentUser.getUsername(), "BUY", symbol, quantity, stock.getPrice()));
            System.out.println("Purchase successful!");
        } else {
            System.out.println("Insufficient funds!");
        }
    }
    
    public void sellStock() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }
        
        if (currentUser.getPortfolio().isEmpty()) {
            System.out.println("You don't own any stocks!");
            return;
        }
        
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();
        
        if (!currentUser.getPortfolio().containsKey(symbol)) {
            System.out.println("You don't own this stock!");
            return;
        }
        
        Stock stock = stocks.get(symbol);
        int ownedShares = currentUser.getPortfolio().get(symbol);
        
        System.out.printf("You own %d shares of %s%n", ownedShares, symbol);
        System.out.printf("Current price: $%.2f%n", stock.getPrice());
        System.out.print("Enter quantity to sell: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        
        if (quantity > ownedShares) {
            System.out.println("You don't have enough shares!");
            return;
        }
        
        double totalValue = quantity * stock.getPrice();
        if (currentUser.sellStock(symbol, quantity, stock.getPrice())) {
            transactions.add(new Transaction(currentUser.getUsername(), "SELL", symbol, quantity, stock.getPrice()));
            System.out.printf("Sale successful! You received $%.2f%n", totalValue);
        }
    }
    
    public void displayPortfolio() {
        if (currentUser == null) {
            System.out.println("Please login first!");
            return;
        }
        
        System.out.println("\n=== YOUR PORTFOLIO ===");
        System.out.printf("Username: %s%n", currentUser.getUsername());
        System.out.printf("Cash Balance: $%.2f%n", currentUser.getBalance());
        
        if (currentUser.getPortfolio().isEmpty()) {
            System.out.println("No stocks owned.");
        } else {
            System.out.println("\nStocks Owned:");
            System.out.println("Symbol\tShares\tCurrent Price\tValue");
            System.out.println("==========================================");
            
            double totalStockValue = 0;
            for (Map.Entry<String, Integer> entry : currentUser.getPortfolio().entrySet()) {
                String symbol = entry.getKey();
                int shares = entry.getValue();
                double price = stocks.get(symbol).getPrice();
                double value = shares * price;
                totalStockValue += value;
                
                System.out.printf("%-6s\t%d\t$%.2f\t\t$%.2f%n", symbol, shares, price, value);
            }
            
            System.out.printf("\nTotal Stock Value: $%.2f%n", totalStockValue);
        }
        
        System.out.printf("Total Portfolio Value: $%.2f%n", currentUser.getPortfolioValue(stocks));
    }
    
    public void displayTransactionHistory() {
        System.out.println("\n=== RECENT TRANSACTIONS ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        
        // Display last 10 transactions
        int start = Math.max(0, transactions.size() - 10);
        for (int i = start; i < transactions.size(); i++) {
            System.out.println(transactions.get(i));
        }
    }
    
    public void showMainMenu() {
        System.out.println("\n=== STOCK TRADING PLATFORM ===");
        System.out.println("1. Register User");
        System.out.println("2. Login");
        System.out.println("3. Display Market Data");
        System.out.println("4. Buy Stock");
        System.out.println("5. Sell Stock");
        System.out.println("6. View Portfolio");
        System.out.println("7. Transaction History");
        System.out.println("8. Simulate Market Movement");
        System.out.println("9. Exit");
        System.out.print("Choose an option: ");
    }
    
    public void run() {
        int choice;
        
        System.out.println("Welcome to the Stock Trading Platform!");
        
        do {
            showMainMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    displayMarketData();
                    break;
                case 4:
                    buyStock();
                    break;
                case 5:
                    sellStock();
                    break;
                case 6:
                    displayPortfolio();
                    break;
                case 7:
                    displayTransactionHistory();
                    break;
                case 8:
                    simulateMarketMovement();
                    System.out.println("Market prices updated!");
                    break;
                case 9:
                    System.out.println("Thank you for using Stock Trading Platform!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } while (choice != 9);
        
        scanner.close();
    }
    
    public static void main(String[] args) {
        StockTradingPlatform platform = new StockTradingPlatform();
        platform.run();
    }
}
