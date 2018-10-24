import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine {

    private VMTypes vmType;
    private Map<Product, Integer> productStock;
    private boolean wantToBuy = true;
    private Currency currency;

    public VendingMachine(String filePath) {
        this.initialize(filePath);
    }

    public void displayMenu(){
        System.out.println("This is a " + vmType + " VENDING MACHINE!");
        System.out.println("Cod \t Produs  Pret(" + currency + ") Gramaj");
        System.out.println("0 - Iesire");
        for(Product product:productStock.keySet()){
            System.out.println(product.getCod() + "\t\t" + product.getName() + "\t\t" + product.getPrice() + "\t\t" + product.getSize());
        }
    }

    public void buyProduct(){
        System.out.println("Alege un produs:");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        if(0 == option){
            System.out.println("Ati iesit din menu. Va mai asteptam!");
            wantToBuy = false;
        }else {
            for (Product p : productStock.keySet()) {
                if (p.getCod() == option) {
                    Integer quantity = productStock.get(p);
                    if (quantity > 0) {
                        productStock.put(p, quantity - 1);
                        System.out.println("Produs cumparat cu succes!");
                    }
                }
            }
        }
    }

    public void start(){
        while(wantToBuy) {
            this.displayMenu();
            buyProduct();
        }
    }

    public void initialize(String filePath){
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
        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Product product = new Product(parts[0], Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
            product.setCod(i-1);
            productStock.put(product, Integer.valueOf(parts[3]));

        }
    }



}
