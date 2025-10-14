package com.example.writein.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@MappedSuperclass
@Getter
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private int version;

//    public Long getId() {
//        return id;
//    }

//    public int getVersion() {
//        return version;
//    }

//    public void setVersion(int version) {
//        this.version = version;
//    }

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractEntity other = (AbstractEntity) obj;
        return version == other.version && Objects.equals(id, other.id);
    }
}