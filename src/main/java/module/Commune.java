package module;


import java.util.ArrayList;
import java.util.List;

public class Commune{
    public int id;
    public int cntCompany;
    public String link;
    public String name;
    public List<Company> companyList;

    public Commune(){
        companyList = new ArrayList<>();
    }
}