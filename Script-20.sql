create table contacts(
  id int auto_increment primary key,
  name varchar(50) not null,
  phone_number varchar(11) not null unique,
  address varchar(255) not null,
  relationship_id int not null,
  identifier INT NOT NULL DEFAULT 1,
  foreign key(relationship_id) references relationships(id)
  on delete restrict
  on update cascade
);

CREATE TABLE relationships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('친구', '가족', '기타') NOT NULL
);


insert into relationships (type) values
('친구'),
('가족'),
('기타')
;

insert into contacts (name, phone_number, address, relationship_id)
values("은혁", "01011112222", "수원시", 3)
;

insert into contacts (name, phone_number,address, relationship_id)
values("윤아", "01023456789", "부산광역시", 2)
;

DELIMITER $$

CREATE TRIGGER set_identifier_before_insert
BEFORE INSERT ON contacts
FOR EACH ROW
BEGIN
    DECLARE max_identifier INT;

    -- 동일한 이름의 최대 identifier를 가져옵니다
    SELECT COALESCE(MAX(identifier), 0) INTO max_identifier
    FROM contacts
    WHERE name = NEW.name;

    -- identifier 값을 자동으로 설정
    SET NEW.identifier = max_identifier + 1;
END $$

DELIMITER ;































