package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter @Setter
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    // entity가 persist 되고 난 이후, createdDAte가 자동으로 할당되는데, 할당되고 난 이후 set을 호출하여 강제로 새로운 시간을 넣어도
    // "객체의 인스턴스 필드"에서만 새로운 LocalDateTime이 있을 뿐, 실제 DB에 insert될 때는 최초 persist 됐을 때 그 시간이 들어간다.

    @LastModifiedDate
    private LocalDateTime updatedDate;
}
