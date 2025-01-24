package contact;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class ContactHashMapDAO implements ContactHashMapInterface {
    private HashMap<String, ContactDTO> contactMap = new HashMap<>();

    @Override
    public void insert(ContactDTO contact) {
        contactMap.put(contact.getPhoneNumber(), contact);
    }

    @Override
    public ContactDTO select(String phoneNumber) {
        return contactMap.get(phoneNumber);
    }

    @Override
    public void update(ContactDTO contact) {
        contactMap.put(contact.getPhoneNumber(), contact);
    }

    @Override
    public void delete(String phoneNumber) {
        contactMap.remove(phoneNumber);
    }

    @Override
    public void listAll() {
        for (ContactDTO contact : contactMap.values()) {
            System.out.println(contact);
        }
    }

    // Collection<Contact>을 Iterable<Contact>로 반환
    public Iterable<ContactDTO> getAllContacts() {
        return contactMap.values();
    }

	public void saveAllToDB(ContactDBInterface contactDB) {
		for (ContactDTO contact : contactMap.values()) {
	        // DB에 업데이트 또는 삽입
	        contactDB.insert(contact);  // DB에 삽입 (update도 필요하면 추가)
	    }
	}
	public int size() {
        return contactMap.size(); // 해시맵 크기 반환
    }

}
