package module;

public class MongoCompany {
    public String name;
    public String date_input;
    public String date_of_license;
    public String email;
    public String phone;
    public String address;
    public String director;
    public String business_code;

    public MongoCompany(String name, String date_input, String date_of_license, String email, String phone, String address, String director, String business_code) {
        this.name = name;
        this.date_input = date_input;
        this.date_of_license = date_of_license;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.director = director;
        this.business_code = business_code;
    }
}
