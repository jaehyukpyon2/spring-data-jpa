package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Item implements Persistable<String> {
    @Id
    //@GeneratedValue
    private String id;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
