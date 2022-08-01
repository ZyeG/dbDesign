// import java.util.Scanner;

// public class MainMenu {

//     public static void main(String[] args) {
//         Handler handler = new Handler();
//         Integer uid = -1;

//         String line = "";
//         Scanner scanner = new Scanner(System.in);

//         int i = 0;
//         printMainMenu();

//         try {
//             do {
//                 line = scanner.nextLine();

//                 if (line.length() == 1) {
//                     switch (line.charAt(0)) {
//                         case '1':
//                             uid = handler.userRegister();
//                             if ( uid != -1) {
//                                 System.out.println("Register success");
//                                 break;
//                             }
//                             System.out.println("Register fail");
//                             break;
                           
//                         case '2':
//                             uid = handler.userLogin();
//                             if (uid !=-1){
//                                 System.out.println("login success");
//                                 break;
//                             }
//                             System.out.println("login fail");
//                             break;

//                         case '3':
//                             if (uid==-1) {
//                                 System.out.println("please login/register first");
//                                 break;
//                             }
//                             else if (handler.editProfile(uid)==1){
//                                 System.out.println("edit profile success");
//                                 break;
//                             }
//                             System.out.println("edit profile fail");
//                             break;
                            
//                         case '4':
//                             System.out.print("case4");
//                             break;
//                         case '5':
//                             System.out.println("Exit");
//                             break;
//                         default:
//                             System.out.println("Unknown action\n");
//                             break;
//                     }
//                 } else {
//                     System.out.println("Error: Invalid action\n");
//                 }
//             } while (line.charAt(0) != '5' || line.length() != 1);
//         } catch (StringIndexOutOfBoundsException ex) {
//             System.out.println("Empty input received. Exiting program...");
//         }
//         // uid = handler.userRegister();
//         // if (uid != -1) {
//         //     System.out.println("Register successfully");
           
//         // }
//         // System.out.println("fail");
//     }

//     public static void printMainMenu()
//     {
//         System.out.print("\nWelcome to the Hotel Reservation Application\n" +
//                 "--------------------------------------------\n" +
//                 "1. user register\n" +
//                 "2. user login\n" +
//                 "3. user edit profile\n" +
//                 "4. Admin\n" +
//                 "5. Exit\n" +
//                 "--------------------------------------------\n" +
//                 "Please select a number for the menu option:\n");
//     }
    
    
// }


import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {
        Handler handler = new Handler();
        Integer uid = -1;

        String line = "";
        Scanner scanner = new Scanner(System.in);

        printMainMenu();

        try {
            do {
                line = "";
                line = scanner.nextLine();
                // 
                System.out.println("here");

                if (line.length() == 1) {
                    switch (line.charAt(0)) {
                        case '1':
                            uid = handler.userRegister();
                            if (uid != -1) {
                                System.out.println("Register success\n");
                                printMainMenu();
                                break;
                                
                            }
                            System.out.println("Register fail\n");
                            printMainMenu();
                            break;

                        case '2':
                            uid = handler.userLogin();
                            if (uid != -1) {
                                System.out.println("login success\n");
                                break;
                            }
                            System.out.println("login fail\n");
                            break;

                        case '3':
                            if (uid == -1) {
                                System.out.println("please login/register first\n");
                                break;
                            } else if (handler.editProfile(uid) == 1) {
                                System.out.println("edit profile success");
                                break;
                            }
                            System.out.println("edit profile fail\n");
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
        // uid = handler.userRegister();
        // if (uid != -1) {
        //     System.out.println("Register successfully");
           
        // }
        // System.out.println("fail");
    }

    public static void printMainMenu()
    {
        System.out.print("\nWelcome to the Hotel Reservation Application\n" +
                "--------------------------------------------\n" +
                "1. user register\n" +
                "2. user login\n" +
                "3. user edit profile\n" +
                "4. Admin\n" +
                "5. Exit\n" +
                "--------------------------------------------\n" +
                "Please select a number for the menu option:\n");
    }
    
    
}