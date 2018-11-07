import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {

    private VMTypes vmType;
    private Map<Product, Integer> productStock;
    private boolean wantToBuy = true;
    private Currency currency;
    private Map<Coin, Integer> coinStock;
    private boolean noMoreCoin = false;
    private Product selectedProduct;

    public VendingMachine(String filePath) {
        this.initialize(filePath);
    }

    public void displayMenu() {
        System.out.println("This is a " + vmType + " VENDING MACHINE!");
        System.out.println("Cod \t Produs  Pret(" + currency + ") Gramaj");


        for (Product product : productStock.keySet()) {
            System.out.println(product.getCod() + "\t\t" + product.getName() + "\t\t" + product.getPrice() + "\t\t" + product.getSize());
        }
    }


        public void displayCoinStock () {
            System.out.println("Cod valoare");
            for (Coin coin : coinStock.keySet()) {
                System.out.println(coin.getCode() + "\t" + coin.getValue());
            }
        }

        public Integer insertCoins (Integer productPrice) {
            Integer sum = 0;

            Scanner scanner = new Scanner(System.in);
            int option = 1;
            boolean ok = false;
            while(sum < productPrice){
                System.out.println("Introdu monezi");
                ok = false;
                option = scanner.nextInt();
                for(Coin coin: coinStock.keySet()){
                    if(coin.getCode() == option){
                        coinStock.put(coin, 1 + coinStock.get(coin));
                        sum = sum + coin.getValue();
                        System.out.println("Suma introdusa: " + sum +" " + currency);
                        Integer toPay = productPrice - sum;
                        System.out.println("Ramas de introdus: " + (toPay>0?toPay:0) + " " + currency);
                        ok = true;
                    }
                }
                if(ok == false){
                    System.out.println("Optiunea introdusa nu este valida.");
                }
            }
            return sum;
        }



        public Product buyProduct () {
            System.out.println("Alege un produs:");
            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();
            boolean ok = false;
            if(option == 0){
                System.exit(0);
            }
                for (Product p : productStock.keySet()) {
                    if (p.getCod() == option) {
                        Integer quantity = productStock.get(p);
                        if (quantity > 0) {
                            ok = true;
                            return p;
                        }
                        else{
                            System.out.println("Nu sunt produse suficiente.");
                            break;
                    }
                }
            }
            if(ok == false){
                System.out.println("Optiunea introdusa nu este valida.");
                return this.buyProduct();
            }
            return null;
        }

    public void deliverProduct(Product product)  {
        productStock.put(product, productStock.get(product) - 1);
    }

    public void payRest(Integer rest){
        for(Coin coin: coinStock.keySet()){
            while(coin.getValue() <= rest){
                if(coinStock.get(coin) > 0){
                    System.out.println("Paying rest " + coin.getValue());
                    coinStock.put(coin, coinStock.get(coin) - 1);
                    rest = rest - coin.getValue();
                }else{
                    break;
                }
            }
        }
        if(rest == 0){
            System.out.println("Rest dat cu succes!");
        }
        else{
            System.out.println("Nu sunt destule monede pentru rest");
            System.out.println("Rest ramas: "+ rest);
        }
    }

    public void start() throws InterruptedException {
        while(true) {
            this.displayMenu();
            Product product = this.buyProduct();
            this.displayCoinStock();
            Integer sum = this.insertCoins(product.getPrice());
            this.deliverProduct(product);
            this.payRest(sum - product.getPrice());
        }
    }

        public void initialize (String filePath){
            Path path = Paths.get(filePath);
            List<String> lines = null;
            try {
                lines = Files.readAllLines(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            vmType = VMTypes.valueOf(lines.get(0));
            currency = Currency.valueOf(lines.get(1));
            productStock = new LinkedHashMap<>();
            coinStock = new LinkedHashMap<>();
            Integer nrProducts = Integer.valueOf(lines.get(2));
            for (int i = 3; i < 3 + nrProducts; i++) {
                String line = lines.get(i);
                String[] parts = line.split(" ");
                Product product = new Product(parts[0], Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
                product.setCod(i);
                productStock.put(product, Integer.valueOf(parts[3]));
            }

            Integer currencyLineIndex = 3 + nrProducts;
            Integer nrCoins = Integer.valueOf(lines.get(currencyLineIndex));
            for (int i = currencyLineIndex + 1; i < currencyLineIndex + 1 + nrCoins; i++) {
                String line = lines.get(i);
                String[] parts = line.split(" ");
                Coin coin = new Coin(i, Integer.valueOf(parts[0]));
                coinStock.put(coin, Integer.valueOf(parts[1]));
            }

        }

}


