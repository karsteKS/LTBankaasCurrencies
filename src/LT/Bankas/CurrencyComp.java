package LT.Bankas;

import java.util.Date;

public class CurrencyComp {

    private String name;
    private String code;
    private String rate;
    private String date;

    public CurrencyComp(String name, String code, String rate, String date) {
        this.name = name;
        this.code = code;
        this.rate = rate;
        this.date = date;
    }

    public CurrencyComp() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "CurrencyComp{" + "name=" + name + ", code=" + code + ", rate=" + rate + ", date=" + date + '}';
    }

}
