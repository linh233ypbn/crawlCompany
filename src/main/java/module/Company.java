package module;

public class Company{
    public String id;
    public String name;
    public String representative;
    public String phoneNumber;
    public String address;
    public String behavior;
    public String link;
    public String date;
    public String email;
    public String taxCode;
    public String idDistrict;
    public String dateInput;

    public Company(String name, String representative, String phoneNumber, String address, String behavior, String link, String date, String email, String taxCode, String dateInput) {
        this.name = name;
        this.representative = representative;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.behavior = behavior;
        this.link = link;
        this.date = date;
        this.email = email;
        this.taxCode = taxCode;
        this.idDistrict = "-1";
        this.dateInput = dateInput;
    }

    public Company(){
        this.idDistrict = "-1";
    }
}