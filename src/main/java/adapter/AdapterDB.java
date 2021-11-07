package adapter;

import module.Company;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static java.sql.DriverManager.getConnection;

public class AdapterDB {
    public static final int ID              = 0;
    public static final int NAME            = 1;
    public static final int REPRESENTATIVE  = 2;
    public static final int PHONE_NUMBER    = 3;
    public static final int ADDRESS         = 4;
    public static final int BEHAVIOR        = 5;
    public static final int LINK            = 6;
    public static final int DATE            = 7;
    public static final int EMAIL           = 8;
    public static final int TAX_CODE        = 9;

    private static String DB_URL = "jdbc:mysql://localhost:3306/db_companies";
    private static String USER_NAME = "root";
    private static String PASSWORD = "Ijykqs8w@";
    private static Connection conn;
    private static Statement statement;
    private static ResultSet resultSet;
    private static int resultUpdate;

    public AdapterDB() {
        try {
            conn = getConnection(DB_URL, USER_NAME, PASSWORD);
            statement = conn.createStatement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void executeQuery(String query) {
        try {
            resultSet = statement.executeQuery(query);
        } catch (Exception ex) {
            System.err.println("FAILED: " + query);
            ex.printStackTrace();
        }
    }

    public static void executeUpdate(String query) {
        try {
            resultUpdate = statement.executeUpdate(query);
        } catch (Exception ex) {
            System.err.println("FAILED: " + query);
            ex.printStackTrace();
        }
    }

    public static String getInfoQuery(String query){
        String result = "fail";
        try {
            resultSet = statement.executeQuery(query);
            resultSet.next();
            result = resultSet.getInt(1) + "";
        } catch (Exception ex) {
            System.err.println("FAILED: " + query);
            ex.printStackTrace();
        }
        return result;
    }

    public static void addCompanies(ArrayList<Company> companies){
        System.out.println("Add companies");
        for(Company company : companies){
            addCompany(company);
        }
    }
    public static void addCompany(Company company){
        company.dateInput = java.time.LocalDate.now().toString();
        String query = "insert into companies value (null, "
                + "'" + company.name + "', "
                + "'" + company.representative + "', "
                + "'" + company.phoneNumber + "', "
                + "'" + company.address + "', "
                + "'" + company.behavior + "', "
                + "'" + company.link + "', "
                + "'" + company.date + "', "
                + "'" + company.email + "', "
                + "'" + company.taxCode + "', "
                + company.idDistrict + ","
                + "'" + company.dateInput + "')";
        executeUpdate(query);
    }

    public static boolean isExists(String taxCode){
        try {
            String checkQuery = "select * from companies where tax_code like '%" + taxCode + "%'";
            ResultSet resultSet = null;
            resultSet = statement.executeQuery(checkQuery);
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    //TO-DO 
    public static ArrayList<Company> getCompanies(String query) {
        ArrayList<Company> companies = new ArrayList<>();
        executeQuery(query);
        int cnt = 1;
        try {
            while (resultSet.next()) {
                Company company = new Company();
                company.id              = resultSet.getString(ID + 1);
                company.name            = resultSet.getString(NAME + 1);
                company.representative  = resultSet.getString(REPRESENTATIVE + 1);
                company.phoneNumber     = resultSet.getString(PHONE_NUMBER + 1);
                company.address         = resultSet.getString(ADDRESS + 1);
                company.behavior        = resultSet.getString(BEHAVIOR + 1);
                company.link            = resultSet.getString(LINK + 1);
                company.date            = resultSet.getString(DATE + 1);
                company.email           = resultSet.getString(EMAIL + 1);
                company.taxCode         = resultSet.getString(TAX_CODE + 1);
                companies.add(company);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return companies;
    }

    public static String getProvinceIDByName(String name){
        return getInfoQuery("select province_id from provinces where province_name like '" + name + "'");
    }
}
