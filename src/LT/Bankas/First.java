package LT.Bankas;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class First {

    public static Scanner sc;

    
    public Date startDate;
    public Date endDate;
    public String currencyl;
    public int currentTime;
    public List<CurrencyComp> plList;
    

    public static void main(String[] args) throws MalformedURLException, IOException {
        sc = new Scanner(System.in);

        start();
    }

    private static void start() throws IOException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            System.out.println("Welcome to Exchange Currency Change application");
            System.out.println("------");
            System.out.println("Please choose option");
            System.out.println("For currency change in period type [ 1 ]");
            System.out.println("For all currency list type [ 2 ]");
            switch (sc.nextInt()) {
                case 1:
                    CurrencyChange(formatter);
                    break;
                case 2:
                    AllCurrencies(formatter);
                    break;
                default:
                    start();
                    break;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

    }

    private static List<CurrencyComp> AllCurrencies(DateFormat formatter) {

        Date date = new Date();
        String linkOut = "today.csv"; 
        System.out.println(formatter.format(date));

        String link = "https://www.lb.lt/lt/currency/daylyexport/?csv=1&class=Eu&type=day&date_day=" + formatter.format(date);
        System.out.println("start period link: " + link);

        File out = new File(linkOut);
        Download(link, out);

        readCSV(linkOut);
        System.out.println("Currency list: ");
        List<CurrencyComp> plList = readCSV(linkOut);
        for (int i = 0; i < plList.size(); i++) {
            System.out.println(plList.get(i).getCode());

        }
        return plList;

    }

    private static List<CurrencyComp> CurrencyChange(DateFormat formatter) throws ParseException, IOException {
        final String[] currCodes = new String[] {"AUD","BGN","BRL","CAD","CHF","CNY","CZK","DKK","GBP","HKD","HRK","HUF","IDR","ILS","INR","ISK",
                                                                "JPY","KRW","MXN","MYR","NOK","NZD","PHP","PLN","RON","RUB","SEK","SGD","THB","TRY","USD","ZAR"};
        String linkOut = "currencyChange.csv";
        
        System.out.println("Please enter currency code");
        String currencyCode = sc.next();
        boolean result = Arrays.stream(currCodes).anyMatch(currencyCode::equals);
	if (result) {
		System.out.println("Currency code: " + currencyCode);
	}else{
            System.out.println("Incorrect currency code");
            CurrencyChange(formatter);
        }

        System.out.println("Please enter start start pattern \"yyyy-MM-dd\"");
        String startPeriod = sc.next();
        dateCheck(startPeriod, formatter);
        
        
        System.out.println("Please enter end date with pattern \"yyyy-MM-dd\"");
        String endPeriod = sc.next();
        dateCheck(endPeriod, formatter);

        String link = "https://www.lb.lt/lt/currency/exportlist/?csv=1&currency=" + currencyCode + "&ff=1&class=Eu&type=day&date_from_day=" + startPeriod + "&date_to_day=" + endPeriod;
        System.out.println(link);

        File out = new File(linkOut);
        Download(link, out);

        readCSV(linkOut);

        List<CurrencyComp> plList = readCSV(linkOut);
        String number1 = plList.get(0).getRate();
        String number2 = plList.get(plList.size() - 1).getRate();
        double value1 = Double.parseDouble(number1.replace(",", "."));
        double value2 = Double.parseDouble(number2.replace(",", "."));
        String valuResult = String.format("%.5f", (value2 - value1));
        System.out.println("--------------");
        System.out.println(valuResult);
        System.out.println("--------------");
        return plList;

    }

    private static List<CurrencyComp> readCSV(String linkOut) {
        List<CurrencyComp> plList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(linkOut))) {
            String line;
            line = reader.readLine();
            Scanner scanner;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                CurrencyComp pl = new CurrencyComp();
                scanner = new Scanner(line);
                scanner.useDelimiter(";");
                while (scanner.hasNext()) {
                    String data = scanner.next();
                    switch (index) {
                        case 0:
                            pl.setName(data.replaceAll("^\"|\"$", ""));
                            break;
                        case 1:
                            pl.setCode(data.replaceAll("^\"|\"$", ""));
                            break;
                        case 2:
                            pl.setRate(data.replaceAll("^\"|\"$", ""));
                            break;
                        case 3:
                            pl.setDate(data.replaceAll("^\"|\"$", ""));
                            break;
                        default:
                            System.out.println("invalid data:" + data);
                            break;
                    }
                    index++;
                }
                index = 0;

                plList.add(pl);

            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return plList;
    }

    private static void Download(String link, File out) {
        try {
            URL url = new URL(link);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.addRequestProperty("User-Agent", "Mozilla/4.76");
            http.setReadTimeout(15000);

            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            FileOutputStream fos = new FileOutputStream(out);
            BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] buffer = new byte[1024];
            int downloaded = 0;
            int read = 0;

            while ((read = in.read(buffer, 0, 1024)) >= 0) {
                bout.write(buffer, 0, read);
                downloaded += read;
                System.out.println("Downloaded " + downloaded + " bytes");
            }
            bout.close();
            in.close();
            System.out.println("Download complete");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private static void dateCheck(String startPeriod, DateFormat formatter) throws ParseException, IOException {
        Date date = null;
try {
     date = formatter.parse(startPeriod);
    if (!startPeriod.equals(formatter.format(date))) {
        date = null;
    }
} catch (ParseException ex) {
    ex.printStackTrace();
}
if (date == null) {
    System.out.println("entered date is incorrect format, check the format and try from begining");
    CurrencyChange(formatter);
    
} else {
    System.out.println("entered date is OK");
}
    }
}
