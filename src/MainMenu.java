import java.util.Scanner;

public class MainMenu {
    static final Scanner scan = new Scanner(System.in);
    static final Handler handler = new Handler();

     // global var
     static Integer uid = -1;  // current user id
     static boolean isHost = true; // if current user is renter
    //  static Integer lid = -1; // if user selects a listing, this tracks the lid
    //  static Integer lid_rid = -1; // if user selects a listing, this tracks the renter of the listing

    public static void main(String[] args) {
        String line = "";
        try {
            do {
                printMainMenu();
                line = scan.nextLine();
                    if (line.length() == 1) {
                        switch (line.charAt(0)) {
                            case '1':
                                case1();
                                break;

                            case '2':
                                case2();
                                break;

                            case '3':
                                case3();
                                break;

                            case '4':
                                case4();
                                break;

                            case '5':
                                case5();
                                break;

                            case '6':
                                case6();
                                break;
                            
                            case '7':
                                case7();
                                break;
                            
                            case '8':
                                System.out.println("Exit");
                                break;
                            
                            case 'a':
                                casea();
                                break;
                            
                            case 'b':
                                caseb();
                                break;
                            
                            default:
                                System.out.println("Unknown action\n");
                                break;
                        }
                    } else {
                        System.out.println("Error: Invalid action\n");
                    }
            } while (line.charAt(0) != '8' || line.length() != 1);
        } catch (StringIndexOutOfBoundsException ex) {
            System.out.println("Empty input received. Exiting program...");
        }
    }

    private static void printMainMenu()
    {
        System.out.print("\nWelcome to the Hotel Reservation Application\n" +
                "--------------------------------------------\n" +
                "1. user register\n" +
                "2. user login\n" +
                "3. user edit profile\n" +
                "4. search listings and making bookings \n" +
                "5. comment/rate on finished bookings \n" +
                "6. cancel booking \n" +
                "7. search listings with multiple filters \n" +
                "a. create a listing \n" +
                "b. remove a listing \n" +
                "c. update a listing \n" +
                "d. search listings within radius" + 
                "--------------------------------------------\n" +
                "Please select a character for the menu option:\n");
    }
    
    private static void case1(){
        try {
            uid = handler.userRegister(scan);
            isHost = handler.isHost(uid);
            System.out.println("Register success");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void case2(){
        try {
            uid = handler.userLogin(scan);
            isHost = handler.isHost(uid);
            System.out.println("login success");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void case3(){
        if (uid == -1) {
            System.out.println("please login/register first");
        } 
        else {
            try {
                handler.editProfile(scan, uid);
                System.out.println("edit profile success");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private static void case4() {
        // search (by time window) and display
        try {
            handler.searchListing(scan, uid, isHost);
            System.out.println("book success");
            handler.patchUserPayinfo(scan, uid);
            System.out.println("payinfo stored success");
            
        } catch(Exception e) {
            System.out.println(e);
        }
    }

    private static void case5() {
        try {
            handler.rateBooking(scan, uid, isHost);
            System.out.println("comment/rate added success");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void case6(){
        try {
            handler.cancelBooking(scan,uid);
            System.out.println("cancel booking success");
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private static void case7() {
        try {
            handler.searchListingMultiFilter(scan);
        } catch (Exception e){
            System.out.println(e);
        }

    }

    private static void casea() {
        try{
            handler.createListing(scan, isHost, uid);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void caseb() {
        try{
            handler.deleteListing(scan, isHost, uid);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void cased() {

    }
}
