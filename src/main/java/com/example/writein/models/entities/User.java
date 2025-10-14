package com.example.writein.models.entities;

import com.example.writein.models.DateAudit;
import com.example.writein.models.EUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")})
public class User extends DateAudit {

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String password;

    @Size(max = 64)
    @Email
    private String email;

    @Size(max = 64)
    private String firstName;

    @Size(max = 64)
    private String lastName;

    @Size(max = 64)
    private String userCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EUser status;

    @ManyToOne
    @JoinColumn(name = "election_id")
    @JsonIgnore
    private Election election;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
