import java.util.Scanner;

public class MainMenu {
    static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        Handler handler = new Handler();
        
        
        // global var
        Integer uid = -1;  // current user id
        boolean isHost = true; // if current user is renter
        Integer lid = -1; // if user selects a listing, this tracks the lid
        Integer lid_rid = -1; // if user selects a listing, this tracks the renter of the listing
        
        String line = "";
        
        

        try {
            do {
                printMainMenu();
                while (!scan.hasNextLine()){
                    System.out.println("a");
                }
                line = scan.nextLine();
                    if (line.length() == 1) {
                        switch (line.charAt(0)) {
                            case '1':
                                try {
                                    uid = handler.userRegister(scan);
                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                                if (uid != -1) {
                                    System.out.println("Register success");
                                    // isHost
                                    try {
                                        isHost = handler.isHost(uid);
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                }

                                else {
                                    System.out.println("Register fail");
                                }
                                // printMainMenu();
                                break;

                            case '2':
                                try {
                                    uid = handler.userLogin(scan);
                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                                if (uid != -1) {
                                    System.out.println("login success");
                                    try {
                                        isHost = handler.isHost(uid);
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                } else {
                                    System.out.println("login fail");
                                }
                                // printMainMenu();
                                break;

                            case '3':
                                if (uid == -1) {
                                    System.out.println("please login/register first");
                                } else if (handler.editProfile(scan, uid) == 1) {
                                    System.out.println("edit profile success");
                                }
                                System.out.println("edit profile fail");
                                printMainMenu();
                                break;

                            case '4':
                                System.out.print("case4");
                                break;
                            case '5':
                                System.out.println("Exit");
                                break;
                            default:
                                System.out.println("Unknown action\n");
                                break;
                        }
                    } else {
                        System.out.println("Error: Invalid action\n");
                    }
            } while (line.charAt(0) != '5' || line.length() != 1);
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println("Empty input received. Exiting program...");
        }
    }

    public static void printMainMenu()
    {
        System.out.print("\nWelcome to the Hotel Reservation Application\n" +
                "--------------------------------------------\n" +
                "1. user register\n" +
                "2. user login\n" +
                "3. user edit profile\n" +
                "4. search listings\n" +
                "5. create booking\n" +
                "--------------------------------------------\n" +
                "Please select a number for the menu option:\n");
    }
    
    
}