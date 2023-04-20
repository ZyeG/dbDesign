import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
            
            throw e;
        }
        
    }

    public static void printReportBooking1(ResultSet rs) throws Exception {
        System.out.println("1. total number of bookings in a specific date range by city");
        try {
            while (rs.next()) {
                
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static void printReportBooking2(ResultSet rs) throws Exception {
        System.out.println("2. total number of bookings in a specific date range by zip code within a city.");
        try {
            while (rs.next()) {
                
                System.out.printf("postalCode: '%s', ", rs.getString("postalCode"));
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static void printReportBooking3(ResultSet rs) throws Exception {
        System.out.println("3. rank the renters by the number of bookings");
        try {
            while (rs.next()) {
                
                System.out.printf("r_uid: %d, ", rs.getInt("r_uid"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }


    public static void printReportBooking4(ResultSet rs) throws Exception {
        System.out.println("4. rank the renters by the number of bookings");
        try {
            while (rs.next()) {
                
                System.out.printf("r_uid: %d, ", rs.getInt("r_uid"));
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static boolean canDeleteUser(ResultSet rs, int todayDate_int) throws Exception {
        boolean canDeleteUser = true;
        while (rs.next()) {
            // if any booking is ongoing & not canceled, set canDelete to false
            if ((rs.getInt("rentTo")>todayDate_int) && (rs.getInt("canceledBy")!=-1)) {
                canDeleteUser = false;
                break;
            }
        }
        return canDeleteUser;
    }

    public static String findWord(String[] arr)
    {
 
        // Create HashMap to store word and it's frequency
        HashMap<String, Integer> hs = new HashMap<String, Integer>();
 
        // Iterate through array of words
        for (int i = 0; i < arr.length; i++) {
            // If word already exist in HashMap then increase it's count by 1
            if (hs.containsKey(arr[i])) {
                hs.put(arr[i], hs.get(arr[i]) + 1);
            }
            // Otherwise add word to HashMap
            else {
                hs.put(arr[i], 1);
            }
        }
 
        // Create set to iterate over HashMap
        Set<Map.Entry<String, Integer> > set = hs.entrySet();
        String key = "";
        int value = 0;
 
        for (Map.Entry<String, Integer> me : set) {
            // Check for word having highest frequency
            if (me.getValue() > value) {
                value = me.getValue();
                key = me.getKey();
            }
        }
 
        // Return word having highest frequency
        return key;
    }
 

    public static void printReportListing1(ResultSet rs) throws Exception {
        System.out.println("1. total number of listings per country");
        try {
            while (rs.next()) {
                System.out.printf("coutry: '%s', ", rs.getString("country"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static void printReportListing2(ResultSet rs) throws Exception {
        System.out.println("2. total number of listings per country and city");
        try {
            while (rs.next()) {
                System.out.printf("coutry: '%s', ", rs.getString("country"));
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static void printReportListing3(ResultSet rs) throws Exception {
        System.out.println("3. total number of listings per country, city and postalCode");
        try {
            while (rs.next()) {
                System.out.printf("coutry: '%s', ", rs.getString("country"));
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("postalCode: '%s', ", rs.getString("postalCode"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }


    public static void printReportListing4(ResultSet rs) throws Exception {
        System.out.println("4. rank hosts by the total number of listings they have per country");
        try {
            while (rs.next()) {
                System.out.printf("h_uid: %d, ", rs.getInt("h_uid"));
                System.out.printf("coutry: '%s', ", rs.getString("country"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static void printReportListing5(ResultSet rs) throws Exception {
        System.out.println("5. rank hosts by the total number of listings they have per city");
        try {
            while (rs.next()) {
                System.out.printf("h_uid: %d, ", rs.getInt("h_uid"));
                System.out.printf("city: '%s', ", rs.getString("city"));
                System.out.printf("count: %d, ", rs.getInt("count(*)"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            
            throw e;
        }
    }

    public static void printReportListing6(ResultSet rs) throws Exception {
        System.out.println("6. find hosts with a number of listings more than 10% total listings per city and country");
        try {
            while (rs.next()) {
                System.out.printf("h_uid: %d, ", rs.getInt("h_uid"));
                System.out.printf("h_uid listing count: %d, ", rs.getInt("count_huid"));
                System.out.printf("total listing count: %d, ", rs.getInt("count_all"));
                System.out.printf("city: %s, ", rs.getString("city_all"));
                System.out.printf("country: %s, ", rs.getString("country_all"));
                System.out.println("\n");
            }
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}



