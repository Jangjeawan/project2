package contact;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * @packageName : contact
 * @fileName 	: ContactDAO.java
 * @author 		: TJ
 * @date		: 2025.01.22
 * @description : contact table 고객정보 처리
 * ==============================================
 * DATE				AUTHOR				NOTE
 * ----------------------------------------------
 * 2025.01.22 JW JANG		최초 생성
 */

public class ContactDAO implements ContactDAOInterface{
    private static final String url = "jdbc:mysql://localhost:3306/doitsql";
    private static final String id = "root";
    private static final String pw = "doitmysql";

    private Scanner scanner = new Scanner(System.in);

    public enum RelationshipType {
        friend("친구"),
        family("가족"),
        other("기타");

        private final String type;

        RelationshipType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static RelationshipType fromString(String text) {
            for (RelationshipType rt : RelationshipType.values()) {
                if (rt.type.equalsIgnoreCase(text)) {
                    return rt;
                }
            }
            return null;
        }

        // MySQL 데이터베이스에서 사용할 때, 값을 직접 반환
        public static RelationshipType fromMysqlValue(String value) {
            for (RelationshipType rt : RelationshipType.values()) {
                if (rt.getType().equals(value)) {
                    return rt;
                }
            }
            return null;
        }
    }

//  1. 회원 추가
    @Override
    public void insert() {
        System.out.print("이름: ");
        String name = scanner.nextLine();
        
        String phoneNumber;
        while(true) {
        	System.out.print("전화번호(ex: 01012345678): ");
        	phoneNumber = scanner.nextLine();
        	
        	String checkPhoneQuery = "select count(*) as count"
        						   + "  from contacts c"
        						   + " where c.phone_number = ?";
        	try(Connection con = DriverManager.getConnection(url, id, pw);
        		PreparedStatement checkpstmt = con.prepareStatement(checkPhoneQuery)){
        		
        		checkpstmt.setString(1, phoneNumber);
        		ResultSet rs = checkpstmt.executeQuery();
        		if(rs.next() && rs.getInt("count")>0) {
        			System.out.println("이미 존재하는 전화번호입니다. 다시 입력하세요.");
        		} else {
        			break;
        		}
        	} catch(SQLException e) {
        		System.out.println("전화번호 확인 중 오류가 발생했습니다: " + e.getMessage());
                e.printStackTrace();
                return;
        	}
        }

        System.out.print("주소: ");
        String address = scanner.nextLine();
        
        RelationshipType relationshipType = null;
        while(relationshipType == null) {
        	System.out.println("관계(ex.가족, 친구, 기타): ");
        	String relationshipTypeInput = scanner.nextLine();
        	relationshipType = RelationshipType.fromString(relationshipTypeInput);
        	if(relationshipType == null) {
        		System.out.println("잘못 입력하셨습니다. 다시 입력해주세요.");
        	}
        }

        String identifierQuery = "select ifnull(max(identifier), 0) + 1 as next_identifier "
        		               + "  from contacts "
        		               + " where name = ?";
        String insertQuery = "insert into contacts (name, phone_number, address, relationship_id, identifier) "
                           + "values (?, ?, ?, (select id from relationships where type = ?), ?)";
        
        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스를 찾을 수 없다.");
		}
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement identifierpstmt = con.prepareStatement(identifierQuery);
             PreparedStatement insertpstmt = con.prepareStatement(insertQuery)) {
        	
            con.setAutoCommit(false);

            identifierpstmt.setString(1, name);
            ResultSet rs = identifierpstmt.executeQuery();
            int identifier = 1;
            if (rs.next()) {
                identifier = rs.getInt("next_identifier");
            }

            insertpstmt.setString(1, name);
            insertpstmt.setString(2, phoneNumber);
            insertpstmt.setString(3, address);
            insertpstmt.setString(4, relationshipType.getType());
            insertpstmt.setInt(5, identifier);

            int result = insertpstmt.executeUpdate();
            if (result > 0) {
                con.commit();
                System.out.println("회원이 성공적으로 추가되었습니다.");
            } else {
                con.rollback();
                System.out.println("회원 추가 실패.");
            }
        } catch (SQLException e) {
            System.out.println("회원 추가 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }



//  2. 회원 목록 보기
    @Override
    public void select() {
        String countQuery = "select count(*) as total_count "
        				  + "  from  contacts";
        String selectQuery = "select c.name, c.phone_number, c.address, r.type as relationship 	"
                           + "  from contacts c 												"
                           + "  join relationships r on c.relationship_id = r.id				";

        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement countpstmt = con.prepareStatement(countQuery);
             PreparedStatement selectpstmt = con.prepareStatement(selectQuery)) {

            // 회원 수 조회
            ResultSet countRs = countpstmt.executeQuery();
            if (countRs.next()) {
                int totalCount = countRs.getInt("total_count");
                System.out.printf("총 %d명의 회원이 저장되어 있습니다.\n", totalCount);
            }

            // 회원 목록 조회
            ResultSet selectRs = selectpstmt.executeQuery();
            while (selectRs.next()) {
                System.out.printf("회원정보 : 이름 = %s, 전화번호 : %s, 주소 : %s, 관계 = %s\n",
                        selectRs.getString("name"),
                        selectRs.getString("phone_number"),
                        selectRs.getString("address"),
                        selectRs.getString("relationship"));
            }
        } catch (SQLException e) {
            System.out.println("회원 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }


//  3. 회원 정보 수정
    @Override
    public void update() {
    	while(true) {
        System.out.print("수정할 회원의 이름을 입력하세요: ");
        String name = scanner.nextLine();

        String selectQuery = "select c.identifier, c.name, c.phone_number, c.address, r.type as relationship"
                           + "  from contacts c 															"
                           + "  join relationships r on c.relationship_id = r.id 							"
                           + " where c.name = ?																";
        
        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스를 찾을 수 없다.");
		}
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement selectpstmt = con.prepareStatement(selectQuery)) {

            con.setAutoCommit(false);

            selectpstmt.setString(1, name);
            ResultSet rs = selectpstmt.executeQuery();

            // 목록 출력
            while (rs.next()) {
                System.out.printf("%d. 이름 = %s, 전화번호 : %s, 주소 : %s, 관계 = %s\n",
                        rs.getInt("identifier"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("relationship"));
            }

            // 수정할 identifier 입력
            System.out.print("수정할 회원 번호를 선택하세요: ");
            int identifierToUpdate = Integer.parseInt(scanner.nextLine());

            // 수정할 회원의 정보를 업데이트하는 쿼리
            String updateQuery = "update contacts c "
                               + "   set c.name = ?, c.phone_number = ?, c.address = ?, relationship_id = (select id from relationships where type = ?) "
                               + " where c.identifier = ? and c.name = ?";

            System.out.print("이름: ");
            String newName = scanner.nextLine();

            System.out.print("전화번호(ex: 01012345678): ");
            String newPhoneNumber = scanner.nextLine();

            System.out.print("주소: ");
            String newAddress = scanner.nextLine();

            System.out.print("관계(ex.가족, 친구, 기타): ");
            String newRelationshipTypeInput = scanner.nextLine();
            RelationshipType newRelationshipType = RelationshipType.fromString(newRelationshipTypeInput);

            try {
    			Class.forName("com.mysql.cj.jdbc.Driver");
    		} catch (ClassNotFoundException e) {
    			System.out.println("드라이버 클래스를 찾을 수 없다.");
    		}
            try (PreparedStatement updatepstmt = con.prepareStatement(updateQuery)) {
                updatepstmt.setString(1, newName);
                updatepstmt.setString(2, newPhoneNumber);
                updatepstmt.setString(3, newAddress);
                updatepstmt.setString(4, newRelationshipType.getType());
                updatepstmt.setInt(5, identifierToUpdate);
                updatepstmt.setString(6, name);

                int result = updatepstmt.executeUpdate();
                if (result > 0) {
                    con.commit();  // 트랜잭션 커밋
                    System.out.println("회원 정보가 성공적으로 수정되었습니다.");
                } else {
                    con.rollback();  // 실패 시 롤백
                    System.out.println("회원 정보 수정 실패.");
                }
            }
        } catch (SQLException e) {
            System.out.println("회원 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    	}
    }


//  4. 회원 삭제
    @Override
    public void delete() {
        // 이름을 입력받고, 해당 이름의 연락처를 모두 보여준다
        System.out.print("삭제할 회원의 이름을 입력하세요: ");
        String name = scanner.nextLine();

        // 해당 이름에 맞는 identifier 목록을 조회
        String selectQuery = "select c.identifier, c.name, c.phone_number, c.address, r.type as relationship "
                           + "  from contacts c join relationships r on c.relationship_id = r.id "
                           + " where c.name = ?";

        try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 클래스를 찾을 수 없다.");
		}
        try (Connection con = DriverManager.getConnection(url, id, pw);
             PreparedStatement selectpstmt = con.prepareStatement(selectQuery)) {

            selectpstmt.setString(1, name); // name을 바인딩
            ResultSet rs = selectpstmt.executeQuery();

            // 목록 출력
            while (rs.next()) {
                System.out.printf("%d. 이름 = %s, 전화번호 : %s, 주소 : %s, 관계 = %s\n",
                        rs.getInt("identifier"),
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("relationship"));
            }

            // 삭제할 identifier 입력
            System.out.print("삭제할 회원 번호: ");
            int identifierToDelete = Integer.parseInt(scanner.nextLine());

            // 삭제 쿼리
            String deleteQuery = "delete from contacts where identifier = ? and name = ?";

            try (PreparedStatement deletepstmt = con.prepareStatement(deleteQuery)) {
                deletepstmt.setInt(1, identifierToDelete); // identifier를 바인딩
                deletepstmt.setString(2, name); // name을 바인딩

                int result = deletepstmt.executeUpdate();
                if (result > 0) {
                    System.out.println("회원이 성공적으로 삭제되었습니다.");
                } else {
                    System.out.println("회원 삭제 실패.");
                }
            }

        } catch (SQLException e) {
            System.out.println("회원 삭제 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
