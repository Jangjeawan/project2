package contact;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ContactTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ContactDBInterface contactDB = new ContactDBDAO();  // DB 처리
        ContactHashMapInterface contactHashMap = new ContactHashMapDAO();  // HashMap 처리

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
//              1. 회원 추가
                case "1":
                    System.out.print("이름: ");
                    String name = scanner.nextLine();

                    String phoneNumber;
                    while (true) {
                        System.out.print("전화번호(ex: 01012345678): ");
                        phoneNumber = scanner.nextLine();
                        // 전화번호 형식 검사
                        if (!phoneNumber.matches("\\d{11}")) {
                            System.out.println("유효하지 않은 전화번호입니다. 숫자 11자리로 입력해주세요.");
                            continue;
                        }
                        // 해시맵에 같은 전화번호가 있는지 확인
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

                    ContactDTO contact = new ContactDTO(name, phoneNumber, address, relationship);
                    contactHashMap.insert(contact);  // HashMap에 추가
                    break;

//              2. 회원 목록 보기
                case "2":
                	int size = ((ContactHashMapDAO) contactHashMap).size();
                	System.out.println("총 " + size + "명의 회원이 저장되어 있습니다. ");
                    Iterable<ContactDTO> contacts = ((ContactHashMapDAO) contactHashMap).getAllContacts();
                    for (ContactDTO c : contacts) {
                        System.out.println("회원정보 : " + c);
                    }
                    break;

//              3. 회원 정보 수정
                case "3":
                    System.out.print("수정할 회원의 이름을 입력하세요: ");
                    String updateName = scanner.nextLine();

                    // 이름으로 검색하여 목록 가져오기
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
                                // 기존 번호와 다르고, 해시맵에 같은 번호가 있을 경우 다시 입력 요청
                                if (!newPhoneNumber.equals(contactToUpdate.getPhoneNumber()) &&
                                    contactHashMap.select(newPhoneNumber) != null) {
                                    System.out.println("이미 존재하는 전화번호입니다. 다른 전화번호를 입력하세요.");
                                } else {
                                    break;
                                }
                            }
                            
                            if (!newPhoneNumber.equals(contactToUpdate.getPhoneNumber())) {
                                // 기존 번호로 된 데이터 삭제
                                contactHashMap.delete(contactToUpdate.getPhoneNumber());
                                // 새로운 번호로 데이터 삽입
                                contactToUpdate.setPhoneNumber(newPhoneNumber);
                            }
                            
                            contactToUpdate.setPhoneNumber(newPhoneNumber);

                            System.out.print("주소: ");
                            contactToUpdate.setAddress(scanner.nextLine());
                            System.out.print("관계(ex.가족, 친구, 기타): ");
                            contactToUpdate.setRelationship(scanner.nextLine());

                            contactHashMap.update(contactToUpdate);  // HashMap에서 수정
                            
                        }
                    }
                    break;

//              4. 회원 삭제
                case "4":
                    System.out.print("삭제할 회원의 이름을 입력하세요: ");
                    String deleteName = scanner.nextLine();

                    // 이름으로 검색하여 목록 가져오기
                    List<ContactDTO> matchedContactsToDelete = searchByName(deleteName, contactHashMap);

                    if (matchedContactsToDelete.isEmpty()) {
                        System.out.println("해당 이름의 회원 정보가 없습니다.");
                    } else {
                        ContactDTO contactToDelete = chooseContact(matchedContactsToDelete, scanner);
                        if (contactToDelete != null) {
                            contactHashMap.delete(contactToDelete.getPhoneNumber());  // HashMap에서 삭제
                        }
                    }
                    break;

//              종료
                case "5":
                	contactDB.clearAndSaveAllContacts(contactHashMap);
                    System.out.println("프로그램을 종료합니다.");
                    scanner.close();
                    return;

                default:
                    System.out.println("1~5의 정수를 입력해주세요.");
            }
        }
    }

    // DB에서 연락처를 로드하여 HashMap에 저장
    private static void loadContactsFromDB(ContactDBInterface contactDB, ContactHashMapInterface contactHashMap) {
        Iterable<ContactDTO> dbContacts = ((ContactDBDAO) contactDB).getAllContacts();
        for (ContactDTO contact : dbContacts) {
            contactHashMap.insert(contact);
        }
    }
    
    // 이름으로 회원 검색 (HashMap 기반)
    private static ArrayList<ContactDTO> searchByName(String name, ContactHashMapInterface contactHashMap) {
        ArrayList<ContactDTO> matchedContacts = new ArrayList<>();
        // HashMap에서 모든 연락처를 가져옴
        for (ContactDTO contact : ((ContactHashMapDAO) contactHashMap).getAllContacts()) {
            if (contact.getName().equals(name)) {
                matchedContacts.add(contact);
            }
        }
        return matchedContacts;
    }

    // 사용자에게 검색된 회원 목록에서 선택하도록 요청
    private static ContactDTO chooseContact(List<ContactDTO> matchedContacts, Scanner scanner) {
        System.out.println("검색된 회원 목록:");
        for (int i = 0; i < matchedContacts.size(); i++) {
            System.out.println((i + 1) + ". " + matchedContacts.get(i));
        }
        System.out.print("회원 번호를 선택하세요: ");
        int choice = 0;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
            return null;  // 잘못된 입력 시 null 반환
        }

        if (choice > 0 && choice <= matchedContacts.size()) {
            return matchedContacts.get(choice - 1);
        } else {
            System.out.println("유효하지 않은 번호입니다.");
            return null;
        }
    }
}
