package com.example.writein.models.entities;

import com.example.writein.models.DateAudit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "counting_boards")
@EntityListeners(AuditingEntityListener.class)
public class CountingBoard extends DateAudit {

    @NotBlank
    @Size(max = 128)
    private String title;

    @NotNull
    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "election_id")
    @JsonIgnore
    private Election election;

    @ManyToMany(mappedBy = "countingBoards", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIgnore
    private List<Office> offices;

    @PreRemove
    public void removeOfficeFromCountingBoard() {
        for (Office office : offices) {
            office.getCountingBoards().remove(this);
        }
    }

}
