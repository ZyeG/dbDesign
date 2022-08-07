import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import java.time.LocalDate;  
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
                throw new Exception("login failed, email not found");
            }
            else if (!this.parser.password.equals(rs.getString("password"))){
                throw new Exception("login failed, password not match");
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

    public void deleteUser(int uid, boolean isHost) throws Exception {
        // 
        if (uid ==-1) {
            throw new Exception("please login first");
        }

        // get today date
        String[] splitTodayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
        String todayDate_str = "";
        for (int i=0; i<3; i++){
            todayDate_str=todayDate_str.concat(splitTodayDate[i]);
        }
        int todayDate_int = Integer.parseInt(todayDate_str);
        
        // 
        try {
            ResultSet rs = dao.getBookingFromUid(uid);
            if (utility.canDeleteUser(rs,todayDate_int)){
                dao.deleteUser(uid,isHost);
            }
            else {
                throw new Exception("cannot delete account, has ongoing bookings");
            }
        } catch(Exception e) {
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
            // dao.searchListingHelper_restore();
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
                int price = rs.getInt("price");
                try {
                    // insert booking
                    dao.bookListing(lid,uid,h_uid,parser.rentFrom, parser.rentTo, price);
                    // update listing.bookedwindows
                    dao.updateBookedWindows(lid, parser.rentFrom, parser.rentTo);
                }
                catch (Exception e){
                    throw e;
                }
            }
        } catch (Exception e){
            throw e;
        }
    }

    
    public void patchUserPayinfo(Scanner scan, int uid) throws Exception{
        // patch user payinfo
        try {
            System.out.println("please enter cardnumber");
            int cardNumber = Integer.parseInt(scan.nextLine());
            System.out.println("please enter cardExpirationDate format yymm");
            int cardExpirationDate = Integer.parseInt(scan.nextLine());
            System.out.println("please enter CVV");
            int CVV = Integer.parseInt(scan.nextLine());
            dao.patchRenterPayinfo(uid,cardNumber,cardExpirationDate,CVV);
        } catch (Exception e){
            throw e;
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
                    System.out.println("please enter bid of the booking you want to rate:");
                    int bid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter your comments(<100 characters)");
                    String comment = scan.nextLine();
                    System.out.println("please enter your rate(1-5, 5 very satisfied)");
                    int rate = Integer.parseInt(scan.nextLine());
                    try {
                        ResultSet rs2 = dao.getBookingFromBid(bid);
                        if(rs2.next()){
                            if(rs2.getInt("rentTo")>todayDate_int){
                                throw new Exception("cannot comment/rate unfinished bookings");
                            }
                            else {
                                try {
                                    dao.patchBookingByHost(bid, comment, rate);
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
                    System.out.println("please enter bid of the booking you want to rate:");
                    int bid = Integer.parseInt(scan.nextLine());
                    System.out.println("please enter your comments(<100 characters)");
                    String comment = scan.nextLine();
                    System.out.println("please enter your rate(1-5, 5 very satisfied)");
                    int rate = Integer.parseInt(scan.nextLine());
                    try {
                        ResultSet rs2 = dao.getBookingFromBid(bid);
                        if(rs2.next()){
                            if(rs2.getInt("rentTo")>todayDate_int){
                                throw new Exception("cannot comment/rate unfinished bookings");
                            }
                            else {
                                try {
                                    dao.patchBookingByRenter(bid, comment, rate);
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

        // get today date
        String[] splitTodayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
        String todayDate_str = "";
        for (int i=0; i<3; i++){
            todayDate_str=todayDate_str.concat(splitTodayDate[i]);
        }
        int todayDate_int = Integer.parseInt(todayDate_str);

        // check can cancel (todaydate<rentForm) , cancel, and update bookedWindows
        int booking_count =0;
        try {
            ResultSet rs = dao.getBookingFromUid(canceledBy_uid);
            booking_count = utility.printResultSetBooking(rs);
            if (booking_count == 0) {
                throw new Exception("you have no bookings to cancel");
            }
            else {
                System.out.println("please enter bid of the booking you want to cancel:");
                int bid = Integer.parseInt(scan.nextLine());
                try {
                    ResultSet rs2 = dao.getBookingFromBid(bid);
                    if(rs2.next()){
                        if((rs2.getInt("rentTo")>todayDate_int) && (rs2.getInt("rentFrom")<todayDate_int) ){
                            throw new Exception("cannot cancel ungoing bookings");
                        }
                        else if(rs2.getInt("rentTo")<todayDate_int){
                            throw new Exception("canot cancel past/finished bookings");
                        }
                        else {
                            try {
                                // patch canceledBy to booking
                                dao.cancelBooking(canceledBy_uid, bid);
                                // update bookedWindows to listing
                                // dao.cancelBooking_updateBookedWindows(rs2.getInt("lid"),rs2.getInt("rentTo"),rs2.getInt("rentFrom"));
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



    public void searchListingMultiFilter(Scanner scan) throws Exception {
        System.out.println("set filters, select from distance,city, postalCode, address, amenities, price, time, separate by ,\nexample: city,priceRange,timeWindow");
        String filters = scan.nextLine();
        List<String> filters_parsed = Arrays.asList(filters.replace("\n", "").split(","));
        try {
            // get listing
            ResultSet rs = dao.searchListingMultiFilter(scan, filters_parsed);
            // print listing
            utility.printResultSetListing(rs);
        } catch (Exception e) {
            throw e;
        }
    }

    public void reportBooking(Scanner scan) throws Exception {
        System.out.println("start time:");
        int startTime = Integer.parseInt(scan.nextLine());
        System.out.println("end time:");
        int endTime = Integer.parseInt(scan.nextLine());
        try {
            ResultSet rs1 = dao.reportBooking1(startTime,endTime);
            utility.printReportBooking1(rs1);
            ResultSet rs2 = dao.reportBooking2(startTime,endTime);
            utility.printReportBooking2(rs2);
            ResultSet rs3 = dao.reportBooking3();
            utility.printReportBooking3(rs3);
            ResultSet rs4 = dao.reportBooking4(startTime,endTime);
            utility.printReportBooking4(rs4);

        } catch (Exception e){
            throw e;
        }

    }


    public void wordCloud() throws Exception {
        
        HashMap<Integer, String> lid_word = new HashMap<Integer, String>(); 
        try {
            // get distinct lid from booking
            ResultSet rs1 = dao.getDistinctLidFromBooking();
            while(rs1.next()){
                lid_word.put(rs1.getInt("lid"), "");
            }
            // select commentByRenter from booking where lid=?, loop through rs_lid, get String comments_lid (concat, find word_lid 
            for (int lid :lid_word.keySet()){
                ResultSet rs = dao.getCommentFromLid(lid);
                String comment = "";
                while(rs.next()){
                    comment = comment + ","+rs.getString("commentByRenter");
                }
                String[] comment_parsed = comment.split(",");
                lid_word.put(lid, utility.findWord(comment_parsed));
            }
            // print
            for (int lid: lid_word.keySet()) {
                System.out.printf("for lid %d, most common word of renters' comments is: '%s'\n", lid, lid_word.get(lid));
            }
        } catch (Exception e){throw e;}
    }
}

