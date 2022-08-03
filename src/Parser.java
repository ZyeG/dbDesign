import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    // user obj must-have attributes
    public String email;
    public String name;
    public boolean ishost;
    public String birth;
    public String password;

    // user obj optional attributes
    public String occupation=null;
    public int SIN=-1;


    public boolean emailValid() {
        String regex_email = "^(.+)@(.+)$";
        Pattern pattern_email = Pattern.compile(regex_email);
        return pattern_email.matcher(email).matches();

    }

    public boolean ageValid() {
        LocalDate dob = LocalDate.parse(birth);
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears() >= 18;
    }

    public boolean birthValid() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
        try {
            sdf.parse(birth);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public void userRegister(Scanner scan){
        System.out.println("Enter Email format: name@domain.com");
        email = scan.nextLine();
        // check email format
        while (!emailValid()) {
            System.out.println("invalid format, please re-enter, example: name@domain.com:");
            email = scan.nextLine();
        }

        System.out.println("Password:");
        password = scan.nextLine();

        System.out.println("Name:");
        name = scan.nextLine();

        System.out.println("birth format: YYYY-MM-DD");
        birth = scan.nextLine();
        // check age
        while ((!birthValid()) || (!ageValid())) {
            System.out.println("invalid format, please re-enter, example: 2000-10-15:");
            birth = scan.nextLine();
        }

        System.out.println("are you host?(Y/N):");
        String ishost_str = scan.nextLine();
        ishost = false;
        if (ishost_str.equals("Y")) {
            ishost = true;
        }

    }

    public void userLogin(Scanner scan) {
        System.out.println("Enter Email format: name@domain.com");
        email = scan.nextLine();
        
        System.out.println("Password:");
        password = scan.nextLine();
    }

    public void editProfile(Scanner scan) {
        System.out.println("occupation: (enter if wish not answer)");
        if (scan.hasNextLine()) {
            occupation = scan.nextLine();
        }
        System.out.println("SIN: (enter if wish not answer)");
        if (scan.hasNextLine()) {
            SIN = Integer.parseInt(scan.nextLine());
        }
    }
}