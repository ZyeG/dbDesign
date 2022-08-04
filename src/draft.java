import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class draft {
    public static void main(String[] args) {

        String[] splitTodayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
        String todayDate_str = "";
        for (int i=0; i<3; i++){
            todayDate_str=todayDate_str.concat(splitTodayDate[i]);
            System.out.printf("todayDate_str is '%s'",todayDate_str);
        }
        System.out.println(todayDate_str);
        int todayDate_int = Integer.parseInt(todayDate_str);
        System.out.println(todayDate_int);
    }
}
