package module;

public class Province {
    private static int id;
    private static String name;

    public static void setId(int id) {
        Province.id = id;
    }

    public static void setName(String name) {
        Province.name = name;
    }

    public static int getId() {
        return id;
    }

    public static String getName() {
        return name;
    }

    public Province() {
    }
}
