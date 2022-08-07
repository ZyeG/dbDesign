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
        int index;
        do {
            printMainMenu();
            index = scan.nextInt();
            scan.nextLine();
            switch (index) {
                case 1:
                    case1();
                    break;

                case 2:
                    case2();
                    break;

                case 3:
                    case3();
                    break;

                case 4:
                    case4();
                    break;

                case 5:
                    case5();
                    break;

                case 6:
                    case6();
                    break;
                
                case 7:
                    case7();
                    break;
                
                case 8:
                    case8();
                    break;
                
                case 9:
                    case9();
                    break;
                
                case 10:
                    case10();
                    break;
                case 11:
                    System.out.println("Exit");
                    break;

                default:
                    System.out.println("Unknown action\n");
                    break;
            } 
        } while(index!=11);
    } 

           


    private static void printMainMenu()
    {
        System.out.print("\nWelcome to the myAirbnb Application\n" +
                "--------------------------------------------\n" +
                "1. user register\n" +
                "2. user login\n" +
                "3. user edit profile\n" +
                "4. search listings and making bookings \n" +
                "5. comment/rate on finished bookings \n" +
                "6. cancel booking \n" +
                "7. search listings with multiple filters \n" +
                "8. report-booking\n" +
                "9. user account delete\n" +
                "10. report-listing-word cloud\n"+
                "--------------------------------------------\n" +
                "Please select a number for the menu option:\n");
    }
    // register
    private static void case1(){
        try {
            uid = handler.userRegister(scan);
            isHost = handler.isHost(uid);
            System.out.println("Register success");
        } catch (Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // login
    private static void case2(){
        try {
            uid = handler.userLogin(scan);
            isHost = handler.isHost(uid);
            System.out.println("login success");
        } catch (Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // profile
    private static void case3(){
        if (uid == -1) {
            System.out.println("please login/register first");
        } 
        else {
            try {
                handler.editProfile(scan, uid);
                System.out.println("edit profile success");
            } catch (Exception e) {
                System.out.println("error" +"  "+e.getMessage());
            }
        }
    }

    // create booking
    private static void case4() {
        // search (by time window) and display
        try {
            handler.searchListing(scan, uid, isHost);
            System.out.println("book success");
            handler.patchUserPayinfo(scan, uid);
            System.out.println("payinfo stored success");
            
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // rate/comment
    private static void case5() {
        try {
            handler.rateBooking(scan, uid, isHost);
            System.out.println("comment/rate added success");
        }
        catch (Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // cancel booking
    private static void case6(){
        try {
            handler.cancelBooking(scan,uid);
            System.out.println("cancel booking success");
        } catch (Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }

    }

    // filter & rank listing
    private static void case7() {
        try {
            handler.searchListingMultiFilter(scan);
        } catch (Exception e){
            System.out.println("error" +"  "+e.getMessage());
        }

    }

    // report-booking
    private static void case8(){
        try {
            handler.reportBooking(scan);
        } catch (Exception e){
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // delete user
    private static void case9(){
        try {
            handler.deleteUser(uid,isHost);
        } catch (Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // word cloud
    private static void case10(){
        try {
            handler.wordCloud();
        } catch (Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }
}
