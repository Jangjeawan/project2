package contact;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

// HashMap을 이용하여 연락처 데이터를 관리하는 DAO 클래스
public class ContactHashMapDAO implements ContactHashMapInterface {
    private HashMap<String, ContactDTO> contactMap = new HashMap<>(); // 전화번호를 키로 하는 연락처 저장소

    // 연락처를 추가하는 메서드
    @Override
    public void insert(ContactDTO contact) {
        contactMap.put(contact.getPhoneNumber(), contact);
    }

    // 전화번호를 이용하여 연락처를 조회하는 메서드
    @Override
    public ContactDTO select(String phoneNumber) {
        return contactMap.get(phoneNumber);
    }

    // 연락처 정보를 업데이트하는 메서드
    @Override
    public void update(ContactDTO contact) {
        contactMap.put(contact.getPhoneNumber(), contact);
    }

    // 전화번호를 이용하여 연락처를 삭제하는 메서드
    @Override
    public void delete(String phoneNumber) {
        contactMap.remove(phoneNumber);
    }

    // 저장된 모든 연락처를 출력하는 메서드
    @Override
    public void listAll() {
        for (ContactDTO contact : contactMap.values()) {
            System.out.println(contact);
        }
    }

    // 저장된 모든 연락처를 Iterable<ContactDTO> 형태로 반환하는 메서드
    public Iterable<ContactDTO> getAllContacts() {
        return contactMap.values();
    }

    // HashMap에 저장된 모든 연락처를 DB에 저장하는 메서드
    public void saveAllToDB(ContactDBInterface contactDB) {
        for (ContactDTO contact : contactMap.values()) {
            contactDB.insert(contact); // DB에 삽입 (업데이트가 필요하면 별도 구현 가능)
        }
    }

    // 저장된 연락처 개수를 반환하는 메서드
    public int size() {
        return contactMap.size();
    }
}
