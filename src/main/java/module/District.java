package module;

import java.util.Comparator;
import java.util.PriorityQueue;

public class District{
    public int id;
    public String name;
    public Comparator<Commune> cntCompanyComparator = new Comparator<Commune>() {
        @Override
        public int compare(Commune d1, Commune d2) {
            return d1.cntCompany - d2.cntCompany;
        }
    };
    public PriorityQueue<Commune> communies;

    public District(int i, String n) {
        Commune a = new Commune();
        id = i;
        name = n;
        communies = new PriorityQueue<Commune>(cntCompanyComparator);
    }
}
