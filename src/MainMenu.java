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
                    case11();
                    break;

                case 12:
                    case12();
                    break;

                case 13:
                    case13();
                    break;

                case 14:
                    case14();
                    break;
                
                case 15:
                    case15();
                    break;

                case 16:
                    case16();
                    break;
                case 17:
                    System.out.println("Exit");
                    break;
                default:
                    System.out.println("Unknown action\n");
                    break;
            } 
        } while(index!=17);
    } 

           


    private static void printMainMenu()
    {
        System.out.print("\nWelcome to the myAirbnb Application\n" +
                "--------------------------------------------\n" +
                "1. user register\n" +
                "2. user login\n" +
                "3. user edit profile\n" +
                "4. create a booking \n" +
                "5. comment/rate on finished bookings \n" +
                "6. cancel booking \n" +
                "7. search listings with multiple filters \n" +
                "8. report-booking\n" +
                "9. user account delete\n" +
                "10. report-listing-word cloud\n"+
                "11. create listing\n" +
                "12. update listing price\n"+
                "13. update listing availability\n"+
                "14. host toolkit(suggest price and amenities given city and type)\n"+
                "15. report-listing\n"+
                "16. delete listing\n"+
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
            handler.cancelBooking(scan,uid,isHost);
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
            System.out.print("delete account success");
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

    // create listing
    private static void case11(){
        try {
            handler.createListing(scan, uid, isHost);
            System.out.println("create listing success");
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // update listing price
    private static void case12() {
        try {
            handler.updateListingPrice(scan, uid, isHost);
            System.out.println("update price success");
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // update listing avail
    private static void case13() {
        try {
            handler.updateListingAvail(scan, uid, isHost);
            System.out.println("update availability success");
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // toolkit
    private static void case14() {
        try {
            handler.toolkit(scan, isHost);
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // report-booking
    private static void case15() {
        try {
            handler.reportListing();
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    // delete listing
    private static void case16() {
        try {
            handler.deleteListing(scan,isHost, uid);
            System.out.println("delete listing success");
        } catch(Exception e) {
            System.out.println("error" +"  "+e.getMessage());
        }
    }

    
}
