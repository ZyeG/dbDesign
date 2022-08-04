import java.sql.ResultSet;
public class utility {

    public static Integer printResultSetListing(ResultSet rs) throws Exception {
        // ResultSetMetaData rsmd = rs.getMetaData();
        // if (!rs.next()) {
        //     throw new Exception("no listing available in the window specified");
        // }
        int listing_count = 0;
        try {
            while (rs.next()) {
                System.out.printf("lid: %d, ", rs.getInt("lid"));
                System.out.printf("type: '%s', ", rs.getString("type"));
                System.out.printf("country: '%s', ", rs.getString("country"));
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("postalCode: '%s', ", rs.getString("postalCode"));
                System.out.printf("address: '%s', ", rs.getString("address"));
                System.out.printf("amenities: '%s', ", rs.getString("amenities"));
                System.out.printf("price: %d. ", rs.getInt("price"));
                System.out.println();
                listing_count++;
            }
            return listing_count;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
    }

    public static Integer printResultSetBooking (ResultSet rs) throws Exception {
        // ResultSetMetaData rsmd = rs.getMetaData();
        // if (!rs.next()) {
        //     throw new Exception("no listing available in the window specified");
        // }
        int booking_count = 0;
        try {
            while (rs.next()) {
                System.out.printf("bid: %d, ", rs.getInt("bid"));
                System.out.printf("lid: %d, ", rs.getInt("lid"));
                System.out.printf("r_uid: %d, ", rs.getInt("r_uid"));
                System.out.printf("h_uid: %d, ", rs.getInt("h_uid"));
                System.out.printf("rentFrom: %d, ", rs.getInt("rentFrom"));
                System.out.printf("rentTo: %d, ", rs.getInt("rentTo"));
                System.out.println();
                booking_count++;
            }
            return booking_count;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
    }
}



