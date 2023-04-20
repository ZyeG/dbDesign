import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class DAO {

    public Connection conn;
    // public PreparedStatement ps;

	public DAO() {
        String url = "jdbc:mysql://127.0.0.1/mydb";
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
			this.conn = DriverManager.getConnection(url, "root", "cscc43s2022");
		} 
		catch (Exception e) {
			System.out.print(e);
		}
	}

    /* user */
    public void postRegister(String email, String name, String password, Boolean ishost, String birth) throws SQLException {
        String query =  "INSERT INTO user (email, name, password, isHost, birth) VALUES(?, ?, ?, ?, ?) ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, name);
            ps.setString(3, password);
            ps.setBoolean(4, ishost);
            ps.setString(5, birth);
            ps.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    public ResultSet getUserFromEmail (String email) throws SQLException {
        String query =  "SELECT * FROM user WHERE email = ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, email);
            return ps.executeQuery();
        } catch (SQLException e){
            throw new SQLException();
        }
    }

    public ResultSet getUserFromUid (int uid) throws SQLException {
        String query =  "SELECT * FROM user WHERE uid = ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, uid);
            return ps.executeQuery();
        } catch (SQLException e){
            throw new SQLException();
        }
    }

    // patch with occupation & SIN (both optional)
    public void patchUser (int uid, String occupation, int SIN) throws SQLException {
        String query;
        try {
            if (occupation!= null) {
                query = "UPDATE user SET occupation = ? WHERE uid = ? ";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setString(1, occupation);
                ps.setInt(2, uid);
                ps.executeUpdate();
            }
            if (SIN!= -1) {
                query = "UPDATE user SET SIN = ? WHERE uid = ? ";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, SIN);
                ps.setInt(2, uid);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            
            throw new SQLException();
        }
        
    }

    public void deleteUser(int uid, boolean isHost) throws Exception{

        // host
        if (isHost) {
            // remove all bookings
            String query1 =  "DELETE FROM booking WHERE h_uid = ? ";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query1);
                ps.setInt(1, uid);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException();
            }

            // remove all listing
            String query2 = "DELETE FROM listing WHERE h_uid=?";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query2);
                ps.setInt(1, uid);
                ps.executeUpdate();
            } catch (SQLException e){
                throw new SQLException();
            }

        }
        // renter
        else {
            // remove all bookings
            String query1 =  "DELETE FROM booking WHERE r_uid = ? ";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query1);
                ps.setInt(1, uid);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new SQLException();
            }

        }


        // remove user
        String query3 =  "DELETE FROM user WHERE uid = ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query3);
            ps.setInt(1, uid);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException();
        }
    }
    



    /* listing */

    public ResultSet searchListing(int rentFrom, int rentTo) throws SQLException {
        // String query1 =  "ALTER TABLE listing ADD COLUMN rentFrom Integer DEFAULT ?, ADD COLUMN rentTo Integer DEFAULT ? ";
        // try {
        //     PreparedStatement ps = this.conn.prepareStatement(query1);
        //     ps.setInt(1, rentFrom);
        //     ps.setInt(2, rentTo);
        //     ps.execute();
        // } catch (SQLException e){
        //     
        //     throw new SQLException();
        // }

        // String query = "SELECT * from listing where notBooked(listing.bookedWindows, ?, ?) AND withinAvailability(listing.availableFrom, listing.availableTo, ?, ?)";
        // try {
        //     PreparedStatement ps = this.conn.prepareStatement(query);
        //     ps.setInt(1, rentFrom);
        //     ps.setInt(2, rentTo);
        //     ps.setInt(3, rentFrom);
        //     ps.setInt(4, rentTo);
        //     return ps.executeQuery();
        // } catch (SQLException e){
            
        //     throw new SQLException();
        // }
        String query = "SELECT * from listing where availableFrom<=? AND availableTo>=?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, rentFrom);
            ps.setInt(2, rentTo);
            return ps.executeQuery();
        } catch (SQLException e){
            throw new SQLException();
        }
    }

    // public void searchListingHelper_restore() throws SQLException {
    //     String query =  "ALTER TABLE listing DROP COLUMN rentFrom, DROP COLUMN rentTo ";
    //     try {
    //         PreparedStatement ps = this.conn.prepareStatement(query);
    //         ps.execute();
    //     } catch (SQLException e){
    //         
    //         throw new SQLException();
    //     }
    // }

    public ResultSet getListingFromlid(int lid) throws SQLException {
        String query =  "SELECT * FROM listing WHERE lid = ?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            return ps.executeQuery();
        } catch (SQLException e){
            
            throw new SQLException();
        }
    }

    public ResultSet getListingFromHuid(int h_uid) throws Exception {
        String query =  "SELECT * FROM listing WHERE h_uid = ?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, h_uid);
            return ps.executeQuery();
        } catch (SQLException e){
            
            throw new SQLException();
        }
    }

    /* booking */
    public void bookListing(int lid,int uid,int h_uid,int rentFrom, int rentTo, int price) throws SQLException {
        String query =  "INSERT INTO booking (lid, r_uid, h_uid, rentFrom, rentTo, price) VALUES(?, ?, ?, ?, ?, ?)  ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            ps.setInt(2, uid);
            ps.setInt(3, h_uid);
            ps.setInt(4, rentFrom);
            ps.setInt(5, rentTo);
            ps.setInt(6, price);
            ps.execute();
        } catch (SQLException e) {
            throw e;
        }
    }
    
    public void updateBookedWindows(int lid, int rentFrom, int rentTo) throws SQLException {
        String query =  "UPDATE listing SET bookedWindows = JSON_ARRAY_APPEND((select * from (SELECT bookedWindows FROM listing WHERE lid = ?)tblTmp), '$', (SELECT JSON_ARRAY (?,?))) WHERE lid = ?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            ps.setInt(2, rentFrom);
            ps.setInt(3, rentTo);
            ps.setInt(4, lid);
            ps.execute();
        } catch (SQLException e) {
            
            throw e;
        }
    }

    public ResultSet getBookingFromUid(int uid, boolean isHost) throws SQLException {
        if (isHost){
            String query =  "SELECT * FROM booking WHERE h_uid = ? AND canceledBy = -1";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, uid);
                return ps.executeQuery();
            } catch (SQLException e){
                throw new SQLException();
            }
        }
        else {
            String query =  "SELECT * FROM booking WHERE r_uid = ? AND canceledBy = -1";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, uid);
                return ps.executeQuery();
            } catch (SQLException e){
                
                throw new SQLException();
            }

        }
        
    }

    public ResultSet getBookingFromBid (int bid) throws Exception {
        String query =  "SELECT * FROM booking WHERE bid = ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, bid);
            return ps.executeQuery();
        } catch (SQLException e){
            
            throw new SQLException();
        }
    }

    public ResultSet getBookingFromlid (int lid) throws Exception {
        String query =  "SELECT * FROM booking WHERE lid = ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            return ps.executeQuery();
        } catch (SQLException e){
            
            throw new SQLException();
        }
    }

    public void patchBookingByHost(int bid, String comment, int rate) throws SQLException {
        String query;
        try {
            query = "UPDATE booking SET commentByHost = ? , rateByHost = ? WHERE bid = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, comment);
            ps.setInt(2, rate);
            ps.setInt(3, bid);
            ps.executeUpdate();
        } catch (Exception e) {
            
            throw new SQLException();
        }

    }

    public void patchBookingByRenter(int bid, String comment, int rate) throws SQLException {
        String query;
        try {
            query = "UPDATE booking SET commentByRenter = ? , rateByRenter = ? WHERE lid = ? AND r_uid = ? AND h_uid=? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, comment);
            ps.setInt(2, rate);
            ps.setInt(3, bid);
            ps.executeUpdate();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public void cancelBooking(int canceledBy_uid, int bid) throws SQLException {
        String query;
        try {
            query = "UPDATE booking SET canceledBy = ? WHERE bid = ? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, canceledBy_uid);
            ps.setInt(2, bid);
            ps.executeUpdate();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public void patchRenterPayinfo(int uid, int cardNumber, int cardExpirationDate, int CVV) throws SQLException {
        String query;
        try {
            query = "UPDATE user SET cardNumber = ?, cardExpirationDate =?, CVV = ? WHERE uid = ? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, cardNumber);
            ps.setInt(2, cardExpirationDate);
            ps.setInt(3, CVV);
            ps.setInt(4, uid);
            ps.executeUpdate();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet searchListingMultiFilter(Scanner scan, List<String> filters_parsed) throws Exception{
        // distance,city, postalCode, address, amenities, price, time, separate by ,\nexample: city,priceRange,timeWindow
        
        double latitude = -1.00;
        double longitude = -1.00;
        double distance = -1.00;
        String city = null;
        String postalCode = null;
        String address = null;
        int price_upper = -1;
        int price_lower = -1;
        // int timeFrom = -1;
        // int timeTo = -1;

        // String query = "SELECT * from listing WHERE (SELECT ST_Distance_Sphere(point(longitude, latitude), point(?,?))<?) AND "+
        // "city like ? AND " +
        // "address like ? AND " +
        // "postalCode like ? AND " +
        // "price<? AND price>? AND " +
        // "notBooked(bookedWindows, ? , ?) AND withinAvailability(availableFrom, availableTo, ?, ?))";
        String query = "SELECT * from listing WHERE (SELECT ST_Distance_Sphere(point(longitude, latitude), point(?,?))<?) AND "+
        "city like ? AND " +
        "address like ? AND " +
        "postalCode like ? AND " +
        "price<=? AND price>=?";


        PreparedStatement ps = this.conn.prepareStatement(query);
        ps.setDouble(1, -79.18);
        ps.setDouble(2, 43.78);
        ps.setDouble(3, 9999999999999999.99);
        // city
        ps.setString(4, "%");
        // address
        ps.setString(5, "%");
        // postalCode
        ps.setString(6,"%");
        // price
        ps.setInt(7, 1000);
        ps.setInt(8, 0);
        //time
        // ps.setInt(9, 20220101);
        // ps.setInt(10, 20220831);
        // ps.setInt(11, 20220101);
        // ps.setInt(12, 20220831);

        // set individual query with filter given
        if (filters_parsed.contains("distance")){
            System.out.println("latitude");
            latitude = Double.parseDouble(scan.nextLine()); 
            System.out.println("longitude");
            longitude = Double.parseDouble(scan.nextLine());
            System.out.println("distance(in meters)");
            distance = Double.parseDouble(scan.nextLine());

            ps.setDouble(1, longitude);
            ps.setDouble(2, latitude);
            ps.setDouble(3, distance);

            
        }
        if (filters_parsed.contains("city")) {
            System.out.println("city:");
            city = scan.nextLine();

            ps.setString(4, city);
        }

        if (filters_parsed.contains("postalCode")) {
            System.out.println("postalCode");
            postalCode = scan.nextLine();

            ps.setString(6,postalCode);
        }
        if (filters_parsed.contains("address")) {
            System.out.println("address");
            address = scan.nextLine();

            ps.setString(5, address);
            
        }
        // if (filters_parsed.contains("amenities")) {
        //     System.out.println("amenities");
        //     amenities = scan.nextLine();
        //     String[] amenities_parsed = amenities.replace("\n", "").split(",");
        //     int amenities_count = amenities_parsed.length;
        // }
        if (filters_parsed.contains("price")) {
            System.out.println("price_upper");
            price_upper = Integer.parseInt(scan.nextLine());
            System.out.println("price_lower");
            price_lower = Integer.parseInt(scan.nextLine());

            ps.setInt(7, price_upper);
            ps.setInt(8, price_lower);

        }
        // if (filters_parsed.contains("time")){
        //     System.out.println("timeFrom");
        //     timeFrom = Integer.parseInt(scan.nextLine());
        //     System.out.println("timeTo");
        //     timeTo = Integer.parseInt(scan.nextLine());

        //     ps.setInt(9, timeFrom);
        //     ps.setInt(10, timeTo);
        //     ps.setInt(11, timeFrom);
        //     ps.setInt(12, timeTo);
        // }

        // // test
        // System.out.println("printing the filter query");
        // System.out.println(ps);
    
        // exec query
        try {
            return ps.executeQuery();
        } catch(Exception e) {
            
            throw e;
        }

    }
    

    // public ResultSet searchListingMultiFilter_temp (Scanner scan, List<String> filters_parsed, PreparedStatement previous_ps) throws Exception {

    //     return null;
    // }
    public ResultSet reportBooking1(int startTime, int endTime) throws Exception {
        String query = "select city,count(*) from booking natural join listing where rentFrom>? and rentTo<? group by city";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, startTime);
            ps.setInt(2, endTime);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }

    }

    public ResultSet reportBooking2(int startTime, int endTime) throws Exception {
        String query = "select postalCode,city,count(*) from booking natural join listing where rentFrom>? and rentTo<? group by city, postalCode";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, startTime);
            ps.setInt(2, endTime);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }

    }

    public ResultSet reportBooking3() throws Exception {
        String query = "select r_uid, count(*) from booking group by r_uid order by count(*)";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }

    }

    public ResultSet reportBooking4(int startTime, int endTime) throws Exception {
        String query = "select r_uid,city,count(*) from booking natural join listing where rentTo<? and rentFrom>? group by city,r_uid having count(*)>=2 order by count(*)";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, endTime);
            ps.setInt(2, startTime);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }

    }

    public ResultSet getDistinctLidFromBooking() throws Exception {
        String query = "select distinct(lid) from booking";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet getCommentFromLid(int lid) throws Exception {
        String query = "select commentByRenter from booking where lid = ?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public void postListing(int h_uid, String type, double latitude, double longitude, String country, String city, 
            String postalCode, String address, int price, int availableFrom, int availableTo, String amenities) throws SQLException {
        String query =  "INSERT INTO listing (h_uid, type, latitude, longitude, country, city, postalCode, address, price, availableFrom, availableTo, amenities) " + 
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, h_uid);
            ps.setString(2, type);
            ps.setDouble(3, latitude);
            ps.setDouble(4, longitude);
            ps.setString(5, country);
            ps.setString(6, city);
            ps.setString(7, postalCode);
            ps.setString(8, address);
            ps.setInt(9, price);
            ps.setInt(10, availableFrom);
            ps.setInt(11, availableTo);
            if(amenities == "") {
                ps.setNull(12, Types.VARCHAR);   
            } else {
                ps.setString(12, amenities);
            }
            ps.execute();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteListing(int lid) throws SQLException {
        String query = "DELETE FROM listing WHERE lid = ?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            ps.executeUpdate();
        } catch (SQLException e){
            throw new SQLException();
        }
    }

    public void updateListingPrice(int lid, int price) throws SQLException {
        String query;
        try {
            query = "UPDATE listing SET price = ? WHERE lid = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, price);
            ps.setInt(2, lid);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SQLException();
        }
    }

    public void updateListingAvail(int lid, int availableFrom_new,int availableTo_new) throws SQLException{
        String query;
        try {
            query = "UPDATE listing SET availableFrom = ?, availableTo = ? WHERE lid = ?";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, availableFrom_new);
            ps.setInt(2, availableTo_new);
            ps.setInt(3, lid);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SQLException();
        }

    }

    public ResultSet getListingFromCityType(String type, String city) throws SQLException {
        String query =  "SELECT price,amenities FROM listing WHERE city = ? AND type = ?";
        try {  
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, city);
            ps.setString(2,type);
            return ps.executeQuery();
        } catch (SQLException e){
            
            throw new SQLException();
        }
    }

    public ResultSet reportListing1() throws Exception {
        String query = "select count(*),country from listing group by country";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet reportListing2() throws Exception {
        String query = "select count(*),country,city from listing group by country,city";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet reportListing3() throws Exception {
        String query = "select count(*),country,city,postalCode from listing group by country,city,postalCode";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet reportListing4() throws Exception {
        String query = "select count(*),country,h_uid from listing group by country,h_uid order by count(*) DESC";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet reportListing5() throws Exception {
        String query = "select count(*),city,h_uid from listing group by city,h_uid order by count(*) DESC";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            
            throw new SQLException();
        }
    }

    public ResultSet reportListing6() throws Exception {
        String query = "select h_uid, count_huid, count_all, city_all, country_all from "+
        "(select count(*) as count_huid,h_uid,city as city_huid, country as country_huid from listing group by city, country,h_uid) as temp2, " +
        "(select count(*) as count_all, city as city_all, country as country_all from listing group by city, country) as temp1 "+
        "where count_huid >= 0.1* count_all and city_huid = city_all and country_huid=country_all";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    // find bookings within intent rent time, used for creat a booking
    public ResultSet getBookingWithin(int rentFrom, int rentTo) throws Exception {
        String query = "select * from booking where (rentFrom<? AND rentFrom>?) OR (rentTo>? AND rentTo<?) OR (rentFrom<? AND rentTo>?)";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, rentTo);
            ps.setInt(2, rentFrom);
            ps.setInt(3, rentFrom);
            ps.setInt(4, rentTo);
            ps.setInt(5, rentFrom);
            ps.setInt(6, rentTo);
            return ps.executeQuery();
        } catch (Exception e) {
            throw new SQLException();
        }

    }

    // outside new avail and not camceled, used for update avail
    public ResultSet getBookingOutside(int availableFrom_new, int availableTo_new) throws Exception {
        String query = "select * from booking where (rentFrom<? OR rentTo>?) AND canceledBy =-1";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, availableFrom_new);
            ps.setInt(2, availableTo_new);
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    // not finished, not canceled, used for delete user
    public ResultSet getBookingUnfinished(int todayDate_int, int uid, boolean isHost) throws Exception{
        if (isHost) {
            String query = "select * from booking where rentFrom<? AND canceledBy =-1 AND h_uid =?";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, todayDate_int);
                ps.setInt(2, uid);
                return ps.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException();
            }
        }
        else {
            String query = "select * from booking where rentTo>? AND canceledBy =-1 AND r_uid =?";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, todayDate_int);
                ps.setInt(2, uid);
                return ps.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException();
            }
        }

    }

    public ResultSet getBookingUnfinishedLid(int todayDate_int, int lid) throws Exception {
        String query = "select * from booking where rentTo>? AND canceledBy =-1 AND lid =?";
            try {
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, todayDate_int);
                ps.setInt(2, lid);
                return ps.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException();
            }
    }
}
