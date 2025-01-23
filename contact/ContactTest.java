package contact;

import java.util.InputMismatchException;
import java.util.Scanner;

import contact.ContactDAO;

public class ContactTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ContactDAOInterface contactDao = new ContactDAO();
        
        while(true) {
        	System.out.println();
            System.out.println("============================");
            System.out.println("   다음 메뉴 중 하나를 선택하세요.   ");
            System.out.println("============================");
            System.out.println("1. 회원 추가");
            System.out.println("2. 회원 목록 보기");
            System.out.println("3. 회원 정보 수정하기");
            System.out.println("4. 회원 삭제");
            System.out.println("5. 종료");
            
            String choice = scanner.nextLine();
            
            try {
            	switch(choice) {
            		case "1":
            			contactDao.insert();
            			break;
            			
            		case "2":
            			contactDao.select();
            			break;
            			
            		case "3":
            			contactDao.update();
            			break;
            			
            		case "4":
            			contactDao.delete();
            			break;
            			
            		case "5":
            			System.out.println("종료되었습니다.");
            			return;
            			
            		default:
            			System.out.println("1~5의 정수를 입력해주세요.");
            	}
            } catch (InputMismatchException e) { // 잘못된 입력 처리
                System.out.println("올바른 형식을 입력해주세요.");
                scanner.nextLine(); // 입력 버퍼 초기화
            } catch (Exception e) { // 기타 예외 처리
                System.out.println("알 수 없는 오류가 발생했습니다. 다시 시도해주세요.");
                e.printStackTrace();
            }
        }
    }
}
