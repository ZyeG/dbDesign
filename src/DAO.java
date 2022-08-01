

import java.sql.*;

public class DAO {

    public Connection conn;
    public PreparedStatement ps;

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

    /* user  */
    // 
    // public ResultSet userRegister(String email, String name, String password, 
    // String addr, int birth, String occupation, int SIN, String payinfo, Boolean ishost) throws SQLException {
    //     String query =  "INSERT INTO user (uid, email, name, password, addr, birth, occupation, SIN, payinfo, ishost)" + 
    //     "VALUES(nextval('uid'), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    //     try {
    //         PreparedStatement ps = this.conn.prepareStatement(query);
    //         ps.setString(1, email);
    //         ps.setString(2, name);
    //         ps.setString(3, password);
    //         ps.setString(4, addr);
    //         ps.setInt(5, birth);
    //         ps.setString(6, occupation);
    //         ps.setInt(7, SIN);
    //         ps.setString(8, payinfo);
    //         ps.setBoolean(9, ishost);
    //         return ps.executeQuery();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }

    public void postRegister(String email, String name, String password, Boolean ishost, String birth) throws SQLException {
        // System.out.println("to print in dao");
        // System.out.println(email);
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
            e.printStackTrace();
            throw new SQLException();
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
                query = "UPDATE users SET occupation = ? WHERE uid = ? ";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setString(1, occupation);
                ps.setInt(2, uid);
                this.ps.execute(query);
            }
            if (SIN!= -1) {
                query = "UPDATE users SET SIN = ? WHERE uid = ? ";
                PreparedStatement ps = this.conn.prepareStatement(query);
                ps.setInt(1, SIN);
                ps.setInt(2, uid);
                this.ps.execute(query);
            }
        }
        catch (Exception e){
            throw new SQLException();
        }
    }

    // TODO delete, cancel all booking not started yet, if renter delete all listings
    
}
