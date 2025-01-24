package contact;

public interface ContactHashMapInterface {
    void insert(ContactDTO contact);  // 회원 추가
    ContactDTO select(String phoneNumber);  // 특정 회원 조회
    void update(ContactDTO contact);  // 회원 정보 수정
    void delete(String phoneNumber);  // 회원 삭제
    void listAll();  // 모든 회원 목록 조회
}
