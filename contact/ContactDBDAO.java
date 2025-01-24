package contact;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactDBDAO implements ContactDBInterface {
    private static final String url = "jdbc:mysql://localhost:3306/doitsql";
    private static final String id = "root";
    private static final String pw = "doitmysql";

    @Override
    public void insert(ContactDTO contact) {
        String query = "insert into contacts (name, phone_number, address, relationship_id) "
        			 + "values (?, ?, ?, (select id from relationships where type = ?))";
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhoneNumber());
            pstmt.setString(3, contact.getAddress());
            pstmt.setString(4, contact.getRelationship());

        } catch (SQLException e) {
            System.out.println("DB에서 회원 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public ContactDTO select(String phoneNumber) {
        String query = "select c.name, c.phone_number, c.address, r.type as relationship "
                     + "  from contacts c "
                     + "  join relationships r on c.relationship_id = r.id "
                     + " where c.phone_number = ?";
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ContactDTO(rs.getString("name"), rs.getString("phone_number"), 
                        rs.getString("address"), rs.getString("relationship"));
            }
        } catch (SQLException e) {
            System.out.println("DB에서 회원 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(ContactDTO contact) {
        String query = "update contacts set name = ?, phone_number = ?, address = ?, "
                     + "relationship_id = (select id from relationships where type = ?) "
                     + " where phone_number = ?";
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getPhoneNumber());
            pstmt.setString(3, contact.getAddress());
            pstmt.setString(4, contact.getRelationship());
            pstmt.setString(5, contact.getPhoneNumber());
            
        } catch (SQLException e) {
            System.out.println("DB에서 회원 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public void delete(String phoneNumber) {
        String query = "delete from contacts where phone_number = ?";
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, phoneNumber);
           
        } catch (SQLException e) {
            System.out.println("DB에서 회원 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public void listAll() {
        String query = "select c.name, c.phone_number, c.address, r.type as relationship "
                     + "  from contacts c"
                     + "  join relationships r on c.relationship_id = r.id";
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println(new ContactDTO(rs.getString("name"), rs.getString("phone_number"), 
                        rs.getString("address"), rs.getString("relationship")));
            }
        } catch (SQLException e) {
            System.out.println("DB에서 회원 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // DB에서 모든 연락처를 가져오는 메서드 추가
    public Iterable<ContactDTO> getAllContacts() {
        ArrayList<ContactDTO> contactList = new ArrayList<>();
        String query = "select c.name, c.phone_number, c.address, r.type as relationship "
                     + "  from contacts c "
                     + "  join relationships r on c.relationship_id = r.id";
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement pstmt = con.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                contactList.add(new ContactDTO(rs.getString("name"), rs.getString("phone_number"), 
                        rs.getString("address"), rs.getString("relationship")));
            }
        } catch (SQLException e) {
            System.out.println("DB에서 회원 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        return contactList;
    }

    // DB에서 모든 연락처를 삭제하고 HashMap의 데이터를 저장하는 메서드 추가
    public void clearAndSaveAllContacts(ContactHashMapInterface contactHashMap) {
        String deleteQuery = "delete from contacts";  // 기존 데이터 삭제
        String insertQuery = "insert into contacts (name, phone_number, address, relationship_id) "
                           + "values (?, ?, ?, (select id from relationships where type = ?))";
        
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
             PreparedStatement insertStmt = con.prepareStatement(insertQuery)) {
        
            // DB에서 기존 데이터 삭제
            deleteStmt.executeUpdate();
            
            // HashMap에서 데이터 가져와서 DB에 저장
            for (ContactDTO contact : ((ContactHashMapDAO) contactHashMap).getAllContacts()) {
                insertStmt.setString(1, contact.getName());
                insertStmt.setString(2, contact.getPhoneNumber());
                insertStmt.setString(3, contact.getAddress());
                insertStmt.setString(4, contact.getRelationship());
                insertStmt.executeUpdate();
            }
            
            System.out.println("모든 연락처가 DB에 저장되었습니다.");
            
        } catch (SQLException e) {
            System.out.println("DB에 데이터를 저장하는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
