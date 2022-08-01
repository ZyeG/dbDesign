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

    public void userRegister(){
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter Email format: name@domain.com");
            email = scanner.nextLine();
            // check email format
            while (!emailValid()) {
                System.out.print("invalid format, please re-enter, example: name@domain.com:");
                email = scanner.nextLine();
            }
            
            System.out.println("Password:");
            password = scanner.nextLine();

            System.out.println("Name:");
            name = scanner.nextLine();
           
            System.out.println("birth format: YYYY-MM-DD");
            birth = scanner.nextLine();
            // check age
            while (!ageValid()){
                System.out.print("invalid format, please re-enter, example: 2000-10-15");
                birth = scanner.nextLine();
            }
            
            System.out.println("are you host?(Y/N):");
            String ishost_str = scanner.nextLine();
            ishost = false;
            if (ishost_str.equals("Y")){
                ishost = true;
            }
        } catch (Exception e) {
            e.printStackTrace(); 
            System.out.println("parser register err");
        }

    }

    public void userLogin() {
        try (Scanner scanner = new Scanner(System.in)) {
            // System.out.println("Enter Email format: name@domain.com");
            // email = scanner.nextLine();
            
            System.out.println("Password:");
            password = scanner.nextLine();

        } catch (Exception e) {
            e.printStackTrace(); 
            System.out.println("parser login err");
        }
    }

    public void editProfile() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("occupation: (enter if wish not answer)");
            if (scanner.hasNextLine()){
                occupation = scanner.nextLine();
            }
            System.out.println("SIN: (enter if wish not answer)");
            if (scanner.hasNextLine()){
                SIN = Integer.parseInt(scanner.nextLine());
            }

        } catch (Exception e) {
            e.printStackTrace(); 
            System.out.println("parser patch err");
        }


    }
}