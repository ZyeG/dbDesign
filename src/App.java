import java.sql.*;

public class App {  
    public static void main(String args[]) throws Exception {
		/* A static method of the Class class. It loads the  specified driver */
		
		String url =
		"jdbc:mysql://127.0.0.1/mydb";
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			Connection conn =
			DriverManager.getConnection(url, "root", "cscc43s2022");
			PreparedStatement execStat=conn.prepareStatement(
			"SELECT * FROM Student");
			ResultSet rs = execStat.executeQuery(); 
			/* Extract data from result set*/
			while (rs.next()) {
			int sid  = rs.getInt("sid");
			// String sname = rs.getString("sname");
		// 	int rating = rs.getInt("rating");
		// 	int age = rs.getInt("age");
			System.out.println(sid);
			/* Continued ... */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }  
}  