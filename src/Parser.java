import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;
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

    // used by search listing
    public int rentFrom;
    public int rentTo;

    public boolean emailValid() {
        String regex_email = "^(.+)@(.+)$";
        Pattern pattern_email = Pattern.compile(regex_email);
        return pattern_email.matcher(email).matches();

    }

    public boolean ageValid() {
        LocalDate dob = LocalDate.parse(String.valueOf(birth));
        LocalDate curDate = LocalDate.now();
        return Period.between(dob, curDate).getYears() >= 18;
    }

    // String.valueOf(i);
    public boolean dateValid(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");
        try {
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public void userRegister(Scanner scan) throws Exception{
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
        while ((!dateValid(birth)) || (!ageValid())) {
            if (!ageValid()) {
                throw new Exception("must be adult to register");
            }
            else {
                System.out.println("invalid format, please re-enter, example: 20001015:");
                birth = scan.nextLine();
            }
        }

        System.out.println("are you host?(Y/N):");
        String ishost_str = scan.nextLine();
        ishost = false;
        if (ishost_str.equals("Y")) {
            ishost = true;
        }

    }

    public void userLogin(Scanner scan) {
        System.out.println("Enter Email:");
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

    public void searchListing(Scanner scan) {
        System.out.println("Enter rent starting date format:YYYYMMDD");
        if (scan.hasNextLine()) {
            rentFrom = Integer.parseInt(scan.nextLine());
        }

        System.out.println("Enter rent end date format:YYYYMMDD");
        if (scan.hasNextLine()) {
            rentTo = Integer.parseInt(scan.nextLine());
        }
    }
}
