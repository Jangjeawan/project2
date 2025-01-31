package contact;

// 연락처 정보를 저장하는 Data Transfer Object (DTO) 클래스
public class ContactDTO {
    private String name; // 이름
    private String phoneNumber; // 전화번호
    private String address; // 주소
    private String relationship; // 관계 (친구, 가족, 기타 등)

    // 생성자: 객체 생성 시 필수 정보를 설정
    public ContactDTO(String name, String phoneNumber, String address, String relationship) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.relationship = relationship;
    }

    // 이름을 반환하는 Getter 메서드
    public String getName() {
        return name;
    }

    // 이름을 설정하는 Setter 메서드
    public void setName(String name) {
        this.name = name;
    }

    // 전화번호를 반환하는 Getter 메서드
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // 전화번호를 설정하는 Setter 메서드
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // 주소를 반환하는 Getter 메서드
    public String getAddress() {
        return address;
    }

    // 주소를 설정하는 Setter 메서드
    public void setAddress(String address) {
        this.address = address;
    }

    // 관계(relationship)를 반환하는 Getter 메서드
    public String getRelationship() {
        return relationship;
    }

    // 관계(relationship)를 설정하는 Setter 메서드
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    // 객체의 정보를 문자열로 변환하여 반환하는 메서드
    @Override
    public String toString() {
        return "이름: " + name + ", 전화번호: " + phoneNumber + ", 주소: " + address + ", 관계: " + relationship;
    }
}
