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
			e.printStackTrace();
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
            e.printStackTrace();
            throw new SQLException();
        }
        
    }

    // TODO delete, cancel all booking not started yet, if renter delete all listings
    



    /* listing */

    public ResultSet searchListing(int rentFrom, int rentTo) throws SQLException {
        String query1 =  "ALTER TABLE listing ADD COLUMN rentFrom Integer DEFAULT ?, ADD COLUMN rentTo Integer DEFAULT ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query1);
            ps.setInt(1, rentFrom);
            ps.setInt(2, rentTo);
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }

        String query2 = "SELECT * from listing where notBooked(listing.bookedWindows, listing.rentFrom, listing.rentTo) AND withinAvailability(listing.availableFrom, listing.availableTo, listing.rentFrom, listing.rentTo)";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query2);
            return ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void searchListingHelper_restore() throws SQLException {
        String query =  "ALTER TABLE listing DROP COLUMN rentFrom, DROP COLUMN rentTo ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.execute();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public ResultSet getListingFromlid(int lid) throws SQLException {
        String query =  "SELECT * FROM listing WHERE lid = ?";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            return ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public ResultSet getListingFromUid(int h_uid) throws SQLException {
        String query = "SELECT * FROM listing WHERE h_uid = ?";

        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, h_uid);
            return ps.executeQuery();
        } catch (SQLException e){
            throw new SQLException();
        }
    }

    public void postListing(int h_uid, String type, double latitude, double longitude, String country, String city, 
            String postalCode, String address, int price, int availableFrom, int availableTo, String amenities) throws SQLException {
        String query =  "INSERT INTO user (h_uid, type, latitude, longitude, country, city, postalCode, address, price, availableFrom, availableTo, amenities) " + 
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

    public void patchListing() {
        // TODO: update listing
    }

    public void deleteListing(int lid) throws SQLException {
        String query = "DELETE FROM listing WHERE lid = ?";

        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            ps.executeQuery();
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
            e.printStackTrace();
            throw e;
        }
    }

    public ResultSet getBookingFromUid(int h_uid) throws SQLException {
        String query =  "SELECT * FROM booking WHERE h_uid = ? AND canceledBy = -1";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, h_uid);
            return ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public ResultSet getBookingFromBid (int bid) throws Exception {
        String query =  "SELECT * FROM booking WHERE bid = ? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, bid);
            return ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void searchListingMultiFilter(Scanner scan, List<String> filters_parsed) {
        // distance,city, postalCode, address, amenities, price, time, separate by ,\nexample: city,priceRange,timeWindow
        
        double latitude = -1.00;
        double longitude = -1.00;
        double distance = -1.00;
        String city = null;
        String postalCode = null;
        String address = null;
        String amenities = null;
        int price_upper = -1;
        int price_lower = -1;
        int timeFrom = -1;
        int timeTo = -1;


        if (filters_parsed.contains("distance")){
            System.out.println("latitude");
            latitude = Double.parseDouble(scan.nextLine()); 
            System.out.println("longitude");
            longitude = Double.parseDouble(scan.nextLine());
            System.out.println("distance(in meters)");
            distance = Double.parseDouble(scan.nextLine());
        }
        if (filters_parsed.contains("city")) {
            System.out.println("city");
            city = scan.nextLine();
        }
        if (filters_parsed.contains("postalCode")) {
            System.out.println("postalCode");
            postalCode = scan.nextLine();
        }
        if (filters_parsed.contains("address")) {
            System.out.println("address");
            address = scan.nextLine();
        }
        if (filters_parsed.contains("amenities")) {
            System.out.println("amenities");
            amenities = scan.nextLine();
            String[] amenities_parsed = amenities.replace("\n", "").split(",");
            int ameniteis_count = amenities_parsed.length;
        }
        if (filters_parsed.contains("price")) {
            System.out.println("price_upper");
            price_upper = Integer.parseInt(scan.nextLine());
            System.out.println("price_lower");
            price_lower = Integer.parseInt(scan.nextLine());
        }
        if (filters_parsed.contains("time")){
            System.out.println("timeFrom");
            timeFrom = Integer.parseInt(scan.nextLine());
            System.out.println("timeTo");
            timeTo = Integer.parseInt(scan.nextLine());
        }

        String query;
        try {
            query = "UPDATE user SET cardNumber = ?, cardExpirationDate =?, CVV = ? WHERE uid = ? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            // ps.setInt(1, cardNumber);
            // ps.setInt(2, cardExpirationDate);
            // ps.setInt(3, CVV);
            // ps.setInt(4, uid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            //throw new SQLException();
        }






    //     lid INT UNSIGNED NOT NULL AUTO_INCREMENT,
    // h_uid INT UNSIGNED NOT NULL,
    // type varchar(10) NOT NULL,
    // latitude DECIMAL(5,2) NOT NULL,
    // longitude DECIMAL(5,2) NOT NULL,
    // country varchar(10) NOT NULL,
    // city varchar(10) NOT NULL,
    // postalCode varchar(7) NOT NULL,
    // address varchar(25) NOT NULL,
    // amenities varchar(50),
    // price integer NOT NULL,
    // availableFrom integer NOT NULL,
    // availableTo integer NOT NULL,
    // bookedWindows JSON,
    }
    

}
