public class SPOJ {
    public static void main(String[] args) {
        int sum = 0;
        int current = 10;
        for(int i = 1; i <= 52; i++){
            sum += current;
            current += 10;
        }
        System.out.println(sum);
    }
}
