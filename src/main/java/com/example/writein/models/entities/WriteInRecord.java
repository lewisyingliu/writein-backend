package com.example.writein.models.entities;

import com.example.writein.models.DateAudit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "write_in_records")
@EntityListeners(AuditingEntityListener.class)
public class WriteInRecord extends DateAudit {

    @Size(max = 64)
    private String firstName;

    @Size(max = 64)
    private String lastName;

    @Size(max = 64)
    private String middleName;

    @NotNull
    private Integer recordCount;

    @Size(max = 128)
    private String creatorName;

    @NotNull
    @Size(max = 128)
    private String batchNumber;

    @Size(max = 255)
    private String electionTitle;

    private boolean deletedTag = false;

    private boolean backEditedTag = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "election_id")
    @JsonIgnore
    private Election election;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "office_id", nullable = false)
//    @JsonIgnore
    private Office office;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "countingBoard_id", nullable = false)
//    @JsonIgnore
    private CountingBoard countingBoard;

    @ManyToOne
    @JoinColumn(name = "user_id")
//    @JsonIgnore
    private User user;
}
