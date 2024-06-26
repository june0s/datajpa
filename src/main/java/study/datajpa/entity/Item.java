package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
//@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity implements Persistable<String> {

//    @Id @GeneratedValue
//    private Long id;

    @Id
    private String id;

//    @CreatedDate
//    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    // 처음 DB 에 넣는 객체인지 아닌 지, 내가 로직을 짜야 한다.
    @Override
    public boolean isNew() {
        return getCreatedDate() == null;
    }
}
