package contact;

public class ContactDTO {
    private String name;
    private String phoneNumber;
    private String address;
    private String relationship;

    public ContactDTO(String name, String phoneNumber, String address, String relationship) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.relationship = relationship;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return "이름: " + name + ", 전화번호: " + phoneNumber + ", 주소: " + address + ", 관계: " + relationship;
    }
}
