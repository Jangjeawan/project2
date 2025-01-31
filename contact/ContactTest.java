package contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ContactTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ContactDBInterface contactDB = new ContactDBDAO();  // 데이터베이스 처리 객체 생성
        ContactHashMapInterface contactHashMap = new ContactHashMapDAO();  // 해시맵 처리 객체 생성

        // 데이터베이스에서 연락처 정보를 로드하여 해시맵에 저장
        loadContactsFromDB(contactDB, contactHashMap);
        
        while (true) {
            System.out.println();
            System.out.println("============================");
            System.out.println("   다음 메뉴 중 하나를 선택하세요.   ");
            System.out.println("============================");
            System.out.println("1. 회원 추가");
            System.out.println("2. 회원 목록 보기");
            System.out.println("3. 회원 정보 수정");
            System.out.println("4. 회원 삭제");
            System.out.println("5. 종료");

            String choice = scanner.nextLine();
 
            switch (choice) {
                case "1": // 회원 추가
                    System.out.print("이름: ");
                    String name = scanner.nextLine();

                    String phoneNumber;
                    while (true) {
                        System.out.print("전화번호(ex: 01012345678): ");
                        phoneNumber = scanner.nextLine();
                        
                        // 전화번호 형식 검사 (숫자 11자리 확인)
                        if (!phoneNumber.matches("\\d{11}")) {
                            System.out.println("유효하지 않은 전화번호입니다. 숫자 11자리로 입력해주세요.");
                            continue;
                        }
                        // 중복 전화번호 확인
                        if (contactHashMap.select(phoneNumber) != null) {
                            System.out.println("이미 존재하는 전화번호입니다. 다른 전화번호를 입력하세요.");
                        } else {
                            break;
                        }
                    }
                    
                    System.out.print("주소: ");
                    String address = scanner.nextLine();
                    System.out.print("관계(ex.가족, 친구, 기타): ");
                    String relationship = scanner.nextLine();

                    // 연락처 객체 생성 후 해시맵에 추가
                    ContactDTO contact = new ContactDTO(name, phoneNumber, address, relationship);
                    contactHashMap.insert(contact);
                    break;

                case "2": // 회원 목록 보기
                    int size = ((ContactHashMapDAO) contactHashMap).size();
                    System.out.println("총 " + size + "명의 회원이 저장되어 있습니다.");
                    Iterable<ContactDTO> contacts = ((ContactHashMapDAO) contactHashMap).getAllContacts();
                    for (ContactDTO c : contacts) {
                        System.out.println("회원정보 : " + c);
                    }
                    break;

                case "3": // 회원 정보 수정
                    System.out.print("수정할 회원의 이름을 입력하세요: ");
                    String updateName = scanner.nextLine();

                    List<ContactDTO> matchedContacts = searchByName(updateName, contactHashMap);

                    if (matchedContacts.isEmpty()) {
                        System.out.println("해당 이름의 회원 정보가 없습니다.");
                    } else {
                        ContactDTO contactToUpdate = chooseContact(matchedContacts, scanner);
                        if (contactToUpdate != null) {
                            System.out.print("이름: ");
                            contactToUpdate.setName(scanner.nextLine());

                            String newPhoneNumber;
                            while (true) {
                                System.out.print("전화번호(ex: 01012345678): ");
                                newPhoneNumber = scanner.nextLine();
                                
                                // 전화번호가 변경되었는지 확인하고 중복 여부 검사
                                if (!newPhoneNumber.equals(contactToUpdate.getPhoneNumber()) &&
                                    contactHashMap.select(newPhoneNumber) != null) {
                                    System.out.println("이미 존재하는 전화번호입니다. 다른 전화번호를 입력하세요.");
                                } else {
                                    break;
                                }
                            }
                            
                            if (!newPhoneNumber.equals(contactToUpdate.getPhoneNumber())) {
                                contactHashMap.delete(contactToUpdate.getPhoneNumber()); // 기존 데이터 삭제
                                contactToUpdate.setPhoneNumber(newPhoneNumber); // 새 전화번호 설정
                            }

                            System.out.print("주소: ");
                            contactToUpdate.setAddress(scanner.nextLine());
                            System.out.print("관계(ex.가족, 친구, 기타): ");
                            contactToUpdate.setRelationship(scanner.nextLine());

                            contactHashMap.update(contactToUpdate); // 해시맵에서 정보 수정
                        }
                    }
                    break;

                case "4": // 회원 삭제
                    System.out.print("삭제할 회원의 이름을 입력하세요: ");
                    String deleteName = scanner.nextLine();

                    List<ContactDTO> matchedContactsToDelete = searchByName(deleteName, contactHashMap);

                    if (matchedContactsToDelete.isEmpty()) {
                        System.out.println("해당 이름의 회원 정보가 없습니다.");
                    } else {
                        ContactDTO contactToDelete = chooseContact(matchedContactsToDelete, scanner);
                        if (contactToDelete != null) {
                            contactHashMap.delete(contactToDelete.getPhoneNumber()); // 해시맵에서 삭제
                        }
                    }
                    break;

                case "5": // 종료 및 데이터베이스 저장
                    contactDB.clearAndSaveAllContacts(contactHashMap);
                    System.out.println("프로그램을 종료합니다.");
                    scanner.close();
                    return;

                default:
                    System.out.println("1~5의 정수를 입력해주세요.");
            }
        }
    }
}
