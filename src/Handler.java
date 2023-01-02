import java.sql.ResultSet;
import java.sql.SQLException;
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
            if (dao.getBookingUnfinished(todayDate_int, uid, isHost).next()){
                throw new Exception("cannot delete account, has ongoing bookings");
            }
            else {
                dao.deleteUser(uid,isHost);
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
            ResultSet rs = dao.searchListing(parser.rentFrom, parser.rentTo);
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
                            e.printStackTrace();
                            throw e;
                        }
                    }
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    // check if already booked
                    if(dao.getBookingWithin(parser.rentFrom, parser.rentTo).next()){
                        throw new Exception("already booked");
                    }
                    // insert booking
                    dao.bookListing(lid,uid,h_uid,parser.rentFrom, parser.rentTo, price);
                    // update listing.bookedwindows
                    // dao.updateBookedWindows(lid, parser.rentFrom, parser.rentTo);
                }
                catch (Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
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
        int booking_count =0;
            try {
                ResultSet rs = dao.getBookingFromUid(uid,isHost);
                booking_count = utility.printResultSetBooking(rs);
                if (booking_count == 0) {
                    throw new Exception("you have no bookings to rate");
                }
                else {
                    System.out.println("please enter bid of the booking you want to rate:");
                    int bid = Integer.parseInt(scan.nextLine());
                    try {
                        ResultSet rs2 = dao.getBookingFromBid(bid);
                        if(rs2.next()){
                            if(rs2.getInt("canceledBy")!=-1){
                                throw new Exception("cannot rate canceled bookings");
                            }
                            if(rs2.getInt("rentTo")>todayDate_int){
                                throw new Exception("cannot comment/rate unfinished bookings");
                            }
                            // if(rs2.getInt("rentFrom")>todayDate_int){
                            //     throw new Exception("cannot comment/rate future bookings");
                            // }
                            
                            else {
                                System.out.println("please enter your comments(<100 characters)");
                                String comment = scan.nextLine();
                                System.out.println("please enter your rate(1-5, 5 very satisfied)");
                                int rate = Integer.parseInt(scan.nextLine());
                                try {
                                    dao.patchBookingByHost(bid, comment, rate);
                                } catch(Exception e){
                                    throw e;
                                }
                            }
                        }
                    } catch(Exception e){
                        
                        throw e;
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        
        // // commentByhost
        // if (isHost) {
            
        // }

        // // commentByrenter
        // else {
        //     int booking_count =0;
        //     try {
        //         ResultSet rs = dao.getBookingFromUid(uid,isHost);
        //         booking_count = utility.printResultSetBooking(rs);
        //         if (booking_count == 0) {
        //             throw new Exception("you have no bookings to rate");
        //         }
        //         else {
        //             System.out.println("please enter bid of the booking you want to rate:");
        //             int bid = Integer.parseInt(scan.nextLine());
        //             System.out.println("please enter your comments(<100 characters)");
        //             String comment = scan.nextLine();
        //             System.out.println("please enter your rate(1-5, 5 very satisfied)");
        //             int rate = Integer.parseInt(scan.nextLine());
        //             try {
        //                 ResultSet rs2 = dao.getBookingFromBid(bid);
        //                 if(rs2.next()){
        //                     if(rs2.getInt("rentTo")>todayDate_int){
        //                         throw new Exception("cannot comment/rate unfinished bookings");
        //                     }
        //                     else {
        //                         try {
        //                             dao.patchBookingByRenter(bid, comment, rate);
        //                         } catch(Exception e){
        //                             throw e;
        //                         }
        //                     }
        //                 }
        //             } catch(Exception e){
        //                 throw e;
        //             }
        //         }
        //     } catch (Exception e) {
        //         throw e;
        //     }
        // }
    
    }
    

    public void cancelBooking(Scanner scan, int canceledBy_uid, boolean isHost) throws Exception {
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

        // check can cancel (todaydate<rentForm) , cancel
        int booking_count =0;
        try {
            ResultSet rs = dao.getBookingFromUid(canceledBy_uid,isHost);
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

    public void createListing(Scanner scan, int h_uid, boolean isHost) throws Exception{
        // 
        if (h_uid==-1) {
            throw new Exception("please login first");
        }
        // check
        if (!isHost) {
            throw new Exception("only hosts can create a listing");
        }
        // get input
        System.out.println("[create a new listing] please enter the following fields:\n");
        System.out.println("type (fullHouse, apartment, room): ");
        String type = scan.nextLine();
        System.out.println("latitude: ");
        double latitude = Double.parseDouble(scan.nextLine());
        System.out.println("longitude: ");
        double longitude = Double.parseDouble(scan.nextLine());
        System.out.println("country: ");
        String country = scan.nextLine();
        System.out.println("city: ");
        String city = scan.nextLine();
        System.out.println("postalCode: ");
        String postalCode = scan.nextLine();
        System.out.println("address: ");
        String address = scan.nextLine();
        System.out.println("price: ");
        int price = Integer.parseInt(scan.nextLine());
        System.out.println("available from (YYYYMMDD): ");
        int availableFrom = Integer.parseInt(scan.nextLine());
        System.out.println("available to (YYYYMMDD): ");
        int availableTo = Integer.parseInt(scan.nextLine());
        System.out.println("amenities (optional, enter nothing to skip): ");
        String amenities = scan.nextLine();
        try{
            dao.postListing(h_uid, type, latitude, longitude, country, city, postalCode, address, price, availableFrom, availableTo, amenities);
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateListingPrice (Scanner scan, int h_uid, boolean isHost) throws Exception {
        // check login
        if (h_uid==-1) {
            throw new Exception("please login first");
        }
        // check isHost
        if (!isHost) {
            throw new Exception("only hosts can create a listing");
        }

        try {
            // print h_uid's listing
            ResultSet rs = dao.getListingFromHuid(h_uid);
            utility.printResultSetListing(rs);
            // get new price
            System.out.println("enter lid of the listing for update");
            int lid = Integer.parseInt(scan.nextLine());
            System.out.println("enter the new price:");
            int price = Integer.parseInt(scan.nextLine());
            // update dao
            dao.updateListingPrice(lid, price);
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateListingAvail(Scanner scan, int h_uid, boolean isHost) throws Exception {
        // check login
        if (h_uid==-1) {
            throw new Exception("please login first");
        }
        // check isHost
        if (!isHost) {
            throw new Exception("only hosts can update a listing");
        }
        try {
            // print h_uid's listing
            ResultSet rs = dao.getListingFromHuid(h_uid);
            utility.printResultSetListing(rs);

            // get new avail
            System.out.println("enter lid of the listing for update");
            int lid = Integer.parseInt(scan.nextLine());
            System.out.println("enter the new availableFrom date, format YYYYMMDD:");
            int availableFrom_new = Integer.parseInt(scan.nextLine());
            System.out.println("enter the new availableTo date, format YYYYMMDD:");
            int availableTo_new = Integer.parseInt(scan.nextLine());

            if (dao.getBookingOutside(availableFrom_new, availableTo_new).next()){
                throw new Exception("cannot update avail, has uncancelled current bookings outside the new avail");
            }
            else {
                dao.updateListingAvail(lid, availableFrom_new,availableTo_new);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public void deleteListing(Scanner scan, boolean isHost, int h_uid) throws Exception {
        // Check if user is logged in and is host
        if (h_uid < 0) throw new Exception("[error] not logged in");
        if (!isHost) throw new Exception("[error] only hosts have listings");

        // get today date
        String[] splitTodayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).split("-");
        String todayDate_str = "";
        for (int i=0; i<3; i++){
            todayDate_str=todayDate_str.concat(splitTodayDate[i]);
        }
        int todayDate_int = Integer.parseInt(todayDate_str);
        
        // print list of listings
        try {
            ResultSet rs = dao.getListingFromHuid(h_uid); 
            utility.printResultSetListing(rs);
            // 
            System.out.println("Enter the id of listing to be deleted:");
            int lid = Integer.parseInt(scan.nextLine());

            if (dao.getBookingUnfinishedLid(todayDate_int, lid).next()){
                throw new Exception("cannot delete listing because there are unfinished bookings of this listing");
            }
            else {
                dao.deleteListing(lid);
            }
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }


    public void toolkit(Scanner scan, boolean isHost) throws Exception {
        // 
        if (!isHost) {
            throw new Exception("toolkit is only for hosts");
        }
        System.out.println("enter the type of your rental(fullHouse/apartment/room):");
        String type = scan.nextLine();
        System.out.println("enter the city of your rental location:(ny/toronto/victoria)");
        String city = scan.nextLine();
        try {
            // get similar listings
            ResultSet rs = dao.getListingFromCityType(type, city);
            int similarListing_count = 0;
            int priceTotal = 0;
            String amenitiesTotal = "";
            while(rs.next()){
                similarListing_count= similarListing_count+1;
                priceTotal = priceTotal +rs.getInt("price");
                amenitiesTotal = amenitiesTotal + rs.getString("amenities") + ",";
            }
            if (similarListing_count ==0 ){
                throw new Exception("sorry cannot give suggestions since no similiar listing found");
            }
            else {
                // calculate and print
                int price_suggestion = priceTotal/similarListing_count;
                String amenities_suggestion = utility.findWord(amenitiesTotal.split(","));
                System.out.printf("suggested price for your rental is: %d\n", price_suggestion);
                System.out.printf("suggested amenities for your rental is: %s\n", amenities_suggestion);
            }
        } catch (Exception e){
            throw e;
        }
    }

    public void reportListing() throws Exception {
        try {
            // 5 reports
            ResultSet rs1 = dao.reportListing1();
            utility.printReportListing1(rs1);

            ResultSet rs2 = dao.reportListing2();
            utility.printReportListing2(rs2);

            ResultSet rs3 = dao.reportListing3();
            utility.printReportListing3(rs3);

            ResultSet rs4 = dao.reportListing4();
            utility.printReportListing4(rs4);

            ResultSet rs5 = dao.reportListing5();
            utility.printReportListing5(rs5);

            ResultSet rs6 = dao.reportListing6();
            utility.printReportListing6(rs6);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }




}


