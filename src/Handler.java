import java.io.EOFException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.cj.protocol.Resultset;

import java.time.LocalDate;  
import java.time.Period;  

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
        parser.userRegister(scan);

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
    public Integer editProfile(Scanner scan, int uid) {
        // sanity check
        try {
            ResultSet rs = dao.getUserFromUid(uid);
            if (!rs.next()){
                System.out.println("uid not found, should not seen this");
            }
        } catch (SQLException e) {
            System.out.println("uid not found e, should not seen this");
        }
        
        // parser
        parser.editProfile(scan);

        // dao
        try {
            dao.patchUser(uid, this.parser.occupation, this.parser.SIN);
            return 1;
        } catch(SQLException e){
            e.printStackTrace();
            return -1;
        }
    }
}
