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
    private Map<Coin, Integer> coinStockVM;
    private Map<Coin, Integer> coinStockBuyer;
    private boolean noMoreCoin = false;
    private Product selectedProduct;

    public VendingMachine(String filePath) {
        this.initialize(filePath);
    }

    public void displayMenu(){
        System.out.println("This is a " + vmType + " VENDING MACHINE!");
        System.out.println("Cod \t Produs  Pret(" + currency + ") Gramaj");
//        Optiunea pentru inchiderea programului
        System.out.println("0 - Iesire");
        for(Product product:productStock.keySet()){
            System.out.println(product.getCod() + "\t\t" + product.getName() + "\t\t" + product.getPrice() + "\t\t" + product.getSize());
        }
    }
//  Metoda pentru afisarea meniului cu monezi; are si optiunea 0 cand cumparatorul inceteaza sa mai introduca monezi
    public void displayPayment(){
        System.out.println("Introduceti monede corespunzator pretului produsului selectat:");
        for(Coin coin: coinStockBuyer.keySet()){
        System.out.println(coin.getValue() + "\t" + coinStockBuyer.get(coin));
        }
        System.out.println("0 - gata");
        System.out.println("Pana acum ati introdus " + sumBuyer() + " " + currency);
    }


//  Am introdus variabila wantToBuy pentru ca cel care cumpara sa poata iesi din meniul de produse
    public void buyProduct() {
        System.out.println("Alege un produs:");
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        if (0 == option) {
            System.out.println("Ati iesit din menu. Va mai asteptam!");
            wantToBuy = false;
        } else {
            for (Product p : productStock.keySet()) {
                if (p.getCod() == option) {
                    Integer quantity = productStock.get(p);
                    if (quantity > 0) {
                        productStock.put(p, quantity - 1);
                        System.out.println("Produs selectat cu succes!");
                        selectedProduct = p;
                    }
                }
            }
        }
    }

//    Metoda pentru efectuare a platii. Am introdus variabila noMoreCoin pentru iesirea din meniul monezi ( care
//    acceseaza si iesirea din meniul produse.
    public void makePayment(){
        displayPayment();
        while(!noMoreCoin) {
            Scanner scanner = new Scanner(System.in);
            int optionCoin = scanner.nextInt();
            if (0 == optionCoin) {
                noMoreCoin = true;
                System.out.println("Asteptati va rog produsul!");
                wantToBuy = false;
            }else {
                for (Coin c : coinStockBuyer.keySet()) {
                    if (c.getValue() == optionCoin) {
                        Integer amountBuyer = coinStockBuyer.get(c);
                        coinStockBuyer.put(c, amountBuyer + 1);
                    }
                }
                displayPayment();
                for (Coin c : coinStockVM.keySet()) {
                    if (c.getValue() == optionCoin) {
                        Integer amountVM = coinStockVM.get(c);
                        coinStockVM.put(c, amountVM + 1);
                    }
                }
            }
        }
    }

//    Metoda pentru calculul sumei de bani introdusa de un cumparator
    public int sumBuyer(){
        int sum = 0;
        for (Coin c : coinStockBuyer.keySet()) {
          sum = sum + coinStockBuyer.get(c) * c.getValue();
        }
        return sum;
    }

//    Metoda de calcul pentru suma de bani din VM
    public int sumVM(){
        int sum = 0;
        for (Coin c : coinStockVM.keySet()) {
            sum = sum + coinStockVM.get(c) * c.getValue();
        }
        return sum;
    }

//    Mesaje de afisare in functie de suma de bani introdusa vs. valoarea produsului.
    public void deliverProduct() throws InterruptedException {
        if (sumBuyer() >= selectedProduct.getPrice()) {
            loadingBar();
            System.out.println("Ati achizitionat produsul " + selectedProduct.getName());
            if (sumBuyer() > selectedProduct.getPrice()) {

                int rest = sumBuyer() - selectedProduct.getPrice();
                System.out.println("Restul dvs.: " + rest + " " + currency);
                payRest(rest);
            }
        } else {
            int difference = selectedProduct.getPrice() - sumBuyer();
            System.out.println("Mai trebuie sa achitati: " + difference + " " + currency);

        }
    }
//  Metoda care nu stiu de ce nu functioneaza
    public void loadingBar() throws InterruptedException {
        int i = 0;
        while(i < 21) {
            for (int j=0;j<i;j++) {
                System.out.print(".");
            }
            System.out.print( i*5 + "%" + "\r");
            Thread.sleep(750);
            i++;
        }
        System.out.println();
    }

//    Scaderea restului din stocul VM
    public void payRest(int a){
       int amount10;
       int amount5;

       if(sumVM() >= a){
            int zece = a/10;
            if( coinStockVM.get(10) >= zece ){
               coinStockVM.put(new Coin(10), coinStockVM.get(10) - zece);
               amount10 = zece;
            }else{
               amount10 = coinStockVM.get(10);
               coinStockVM.put(new Coin(10), 0);
            }
            int b = a - amount10 * 10;
            int cinci = b / 5;
            if(coinStockVM.get(5) >= cinci){
               coinStockVM.put(new Coin(5), coinStockVM.get(5) - cinci);
               amount5 = cinci;
            }else{
               amount5 = coinStockVM.get(5);
               coinStockVM.put(new Coin(5), 0);
            }
            int c = b - amount5 * 5;
            coinStockVM.put(new Coin(1), coinStockVM.get(1) - a);
       }else{
            int dif = a - sumVM();
            for (Coin c : coinStockVM.keySet()) {
               coinStockVM.put(c,0);
               System.out.println("Diferenta de " + dif + " " + currency + " este TEAPA!");
            }
       }

    }

//  Am pus conditia (wantToBuy) si am adaugat metodele makePayment() si deliverProduct().
    public void start() throws InterruptedException {
        while(wantToBuy) {
            this.displayMenu();
            buyProduct();
            makePayment();
            deliverProduct();
        }
    }
//  Am adaugat initializarea stocului cumparatorului si a VM
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
        coinStockVM = new LinkedHashMap<>();
        coinStockBuyer = new LinkedHashMap<>();

        for (int i = 5; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Product product = new Product(parts[0], Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
            product.setCod(i-4);
            productStock.put(product, Integer.valueOf(parts[3]));
        }
        for (int i = 2; i < 5; i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Coin coin = new Coin(Integer.valueOf(parts[0]));
            coinStockVM.put(coin, Integer.valueOf(parts[1]));
        }
        for (int i = 2; i < 5; i++) {
            String line = lines.get(i);
            String[] parts = line.split(" ");
            Coin coin = new Coin(Integer.valueOf(parts[0]));
            coinStockBuyer.put(coin, 0);
        }
    }
}

