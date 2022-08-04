import java.sql.*;

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

    /* booking */
    public void bookListing(int lid,int uid,int h_uid,int rentFrom, int rentTo) throws SQLException {
        String query =  "INSERT INTO booking (lid, r_uid, h_uid, rentFrom, rentTo) VALUES(?, ?, ?, ?, ?)  ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            ps.setInt(2, uid);
            ps.setInt(3, h_uid);
            ps.setInt(4, rentFrom);
            ps.setInt(5, rentTo);
            ps.execute();
        } catch (SQLException e) {
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

    public ResultSet getBookingFromKey (int lid, int r_uid, int h_uid) throws Exception {
        String query =  "SELECT * FROM booking WHERE lid = ? AND r_uid = ? AND h_uid=? ";
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, lid);
            ps.setInt(2, r_uid);
            ps.setInt(3, h_uid);
            return ps.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void patchBookingByHost(int lid, int r_uid, int h_uid, String comment, int rate) throws SQLException {
        String query;
        try {
            query = "UPDATE booking SET commentByHost = ? , rateByHost = ? WHERE lid = ? AND r_uid = ? AND h_uid=? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, comment);
            ps.setInt(2, rate);
            ps.setInt(3, lid);
            ps.setInt(4, r_uid);
            ps.setInt(5, h_uid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }

    }

    public void patchBookingByRenter(int lid, int r_uid, int h_uid, String comment, int rate) throws SQLException {
        String query;
        try {
            query = "UPDATE booking SET commentByRenter = ? , rateByRenter = ? WHERE lid = ? AND r_uid = ? AND h_uid=? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setString(1, comment);
            ps.setInt(2, rate);
            ps.setInt(3, lid);
            ps.setInt(4, r_uid);
            ps.setInt(5, h_uid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public void cancelBooking(int canceledBy_uid, int lid, int r_uid, int h_uid) throws SQLException {
        String query;
        try {
            query = "UPDATE booking SET canceledBy = ? WHERE lid = ? AND r_uid = ? AND h_uid=? ";
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, canceledBy_uid);
            ps.setInt(2, lid);
            ps.setInt(3, r_uid);
            ps.setInt(4, h_uid);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    

}
