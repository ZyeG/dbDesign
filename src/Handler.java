import java.io.EOFException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.cj.protocol.Resultset;

import java.time.LocalDate;  
import java.time.Period;
import java.time.format.DateTimeFormatter;  

/* check input validity, things cannot be checked by sql CHECK */
public class Handler {
    public DAO dao;
    public Parser parser;

    public Handler() {
        this.dao = new DAO();
        this.parser = new Parser();
    }

    /* check input & call dao */
    public Integer userRegister(Scanner scan) throws Exception {
        try {
            parser.userRegister(scan);
        } catch (Exception e) {
            throw e;
        }

        try {
            // post
            dao.postRegister(this.parser.email, this.parser.name, this.parser.password, this.parser.ishost, this.parser.birth);
            // get uid
            ResultSet rs = dao.getUserFromEmail(this.parser.email);
            if(rs.next()){
                return rs.getInt("uid");
            }
            return -1;
        } catch (SQLException e) { // CHECK err, or queryExec err
            throw e;
        }
    }

    public Integer userLogin(Scanner scan) throws Exception {
        parser.userLogin(scan);
        try {
            ResultSet rs = dao.getUserFromEmail(this.parser.email);
            if (!rs.next()){
                System.out.println("login failed, email not found");
                return -1;
            }
            else if (!this.parser.password.equals(rs.getString("password"))){
                System.out.println("login failed, password not match");
                return -1;
            }
            return rs.getInt("uid");
        } catch (SQLException e) {
            throw e;
        }
    }

    public boolean isHost(int uid) throws Exception {
        boolean isHost = true;

        try {
            ResultSet rs = dao.getUserFromUid(uid);
            if(rs.next()){
                return rs.getBoolean("isHost");
            }

        } catch (Exception e) {
            throw new Exception("getUserFromUid dao fail");
        }
        return isHost; 
    }
    public void editProfile(Scanner scan, int uid) throws Exception {
        // parser
        parser.editProfile(scan);

        // dao
        try {
            dao.patchUser(uid, parser.occupation, parser.SIN);
        } catch(SQLException e){
            throw e;
        }
    }

    public void searchListing (Scanner scan, Integer uid, boolean isHost) throws Exception {
        // parse
        parser.searchListing(scan);
        // 
        int listing_count;
        try {
            // get listings
            ResultSet rs = dao.searchListing(parser.rentFrom, parser.rentTo);
            // restore table listing 
            dao.searchListingHelper_restore();
            // print listings
            try{
                listing_count = utility.printResultSetListing(rs);
                // no listing found
                if (listing_count == 0) {
                    throw new Exception("no listing found");
                }
                // make a booking
                else {
                    String ifBook;
                    System.out.println("do you want book one of these lisitngs?(Y/N)");
                    ifBook = scan.nextLine();
                    while (!(ifBook.equals("Y") || ifBook.equals("N"))) {
                        System.out.println("invalid format, please re-enter:");
                        ifBook = scan.nextLine();
                    }
                    if (ifBook.equals("Y")) {
                        try {bookListing(scan, uid, isHost);
                        }catch (Exception e){
                            throw e;
                        }
                    }
                    return;
                }

            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void bookListing (Scanner scan, Integer uid, Boolean isHost) throws Exception {
        // check if can make a booking
        if (uid == -1) {
            throw new Exception("not logged in");
        }
        if (isHost) {
            throw new Exception("only renters can make a booking");
        }

        System.out.println("please enter the lid of the listing you want to book");
        int lid = Integer.parseInt(scan.nextLine());
        try {
             ResultSet rs = dao.getListingFromlid(lid);
             if(rs.next()){
                int h_uid = rs.getInt("h_uid");
                int prince = rs.getInt("price");
                try {
                    dao.bookListing(lid,uid,h_uid,parser.rentFrom, parser.rentTo,price);
                    
                }
                catch (Exception e){
                    throw e;
                }
            }
        } catch (Exception e){
            throw e;
        }
    }


    public void patchPayinfo (){
        // patch user payinfo
        try {
            
            int cardNumber = Integer.parseInt(scan.nextLine());
            System.out.println("please enter cardnumber");
            
            System.out.println("please enter the lid of the listing you want to book");
            dao.patchRenterPayinfo(uid);

            
    // cardNumber integer,
    // cardExpirationDate varchar(5),
    // CVV integer,

    
        }

    }
    


    public void rateBooking(Scanner scan, Integer uid, Boolean isHost) throws Exception {
        if (uid == -1) {
            throw new Exception("not logged in");
        }
        String[] splitTodayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
        String todayDate_str = "";
        for (int i=0; i<3; i++){
            todayDate_str=todayDate_str.concat(splitTodayDate[i]);
        }
        int todayDate_int = Integer.parseInt(todayDate_str);
        
        // commentByhost
        if (isHost) {
            int booking_count =0;
            try {
                ResultSet rs = dao.getBookingFromUid(uid);
                booking_count = utility.printResultSetBooking(rs);
                if (booking_count == 0) {
                    throw new Exception("you have no bookings to rate");
                }
                else {
                    System.out.println("please enter lid of the booking you want to rate:");
                    int lid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter r_uid of the booking you want to rate:");
                    int r_uid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter h_uid of the booking you want to rate:");
                    int h_uid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter your comments(<100 characters)");
                    String comment = scan.nextLine();
                    System.out.println("please enter your rate(1-5, 5 very satisfied)");
                    int rate = Integer.parseInt(scan.nextLine());
                    try {
                        ResultSet rs2 = dao.getBookingFromKey(lid, r_uid, h_uid);
                        if(rs2.next()){
                            if(rs2.getInt("rentTo")>todayDate_int){
                                throw new Exception("cannot comment/rate unfinished bookings");
                            }
                            else {
                                try {
                                    dao.patchBookingByHost(lid, r_uid, h_uid, comment, rate);
                                } catch(Exception e){
                                    throw e;
                                }
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        throw e;
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }

        // commentByrenter
        else {
            int booking_count =0;
            try {
                ResultSet rs = dao.getBookingFromUid(uid);
                booking_count = utility.printResultSetBooking(rs);
                if (booking_count == 0) {
                    throw new Exception("you have no bookings to rate");
                }
                else {
                    System.out.println("please enter lid of the booking you want to rate:");
                    int lid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter r_uid of the booking you want to rate:");
                    int r_uid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter h_uid of the booking you want to rate:");
                    int h_uid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter your comments(<100 characters)");
                    String comment = scan.nextLine();
                    System.out.println("please enter your rate(1-5, 5 very satisfied)");
                    int rate = Integer.parseInt(scan.nextLine());
                    try {
                        ResultSet rs2 = dao.getBookingFromKey(lid, r_uid, h_uid);
                        if(rs2.next()){
                            if(rs2.getInt("rentTo")>todayDate_int){
                                throw new Exception("cannot comment/rate unfinished bookings");
                            }
                            else {
                                try {
                                    dao.patchBookingByRenter(lid, r_uid, h_uid, comment, rate);
                                } catch(Exception e){
                                    throw e;
                                }
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        throw e;
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }
    

    public void cancelBooking(Scanner scan, int canceledBy_uid) throws Exception {
        if (canceledBy_uid == -1) {
            throw new Exception("not logged in");
        }

        // get today date, can cancel only before start
        String[] splitTodayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
        String todayDate_str = "";
        for (int i=0; i<3; i++){
            todayDate_str=todayDate_str.concat(splitTodayDate[i]);
        }
        int todayDate_int = Integer.parseInt(todayDate_str);

        // 
        int booking_count =0;
        try {
            ResultSet rs = dao.getBookingFromUid(canceledBy_uid);
            booking_count = utility.printResultSetBooking(rs);
            if (booking_count == 0) {
                throw new Exception("you have no bookings to cancel");
            }
            else {
                System.out.println("please enter lid of the booking you want to cancel:");
                int lid = Integer.parseInt(scan.nextLine());
                System.out.println("please enter r_uid of the booking you want to cancel:");
                int r_uid = Integer.parseInt(scan.nextLine());
                System.out.println("please enter h_uid of the booking you want to cancel:");
                int h_uid = Integer.parseInt(scan.nextLine());
                try {
                    ResultSet rs2 = dao.getBookingFromKey(lid, r_uid, h_uid);
                    if(rs2.next()){
                        if((rs2.getInt("rentTo")>todayDate_int) && (rs2.getInt("rentFrom")<todayDate_int) ){
                            throw new Exception("cannot cancel ungoing bookings");
                        }
                        else if(rs2.getInt("rentTo")<todayDate_int){
                            throw new Exception("canot cancel past/finished bookings");
                        }
                        else {
                            try {
                                dao.cancelBooking(canceledBy_uid, lid, r_uid, h_uid);
                            } catch(Exception e){
                                throw e;
                            }
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }



}

