package com.lab.smartmobility.billie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter @Setter @ToString @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @ApiModel(value = "직원 정보 엔티티") @Table(name = "tbl_staff")
public class Staff implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "직원 고유 번호")
    @Column(name = "staff_num", insertable = false)
    private Long staffNum;

    @ApiModelProperty(value = "이름")
    private String name;

    @ApiModelProperty(value = "직급")
    private String rank;

    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "전화번호")
    private String phone;

    @ApiModelProperty(value = "부서")
    private String department;

    @ApiModelProperty(value = "비밀번호")
    private String password;

    @ApiModelProperty(value = "권한등급")
    private String role;

    @ApiModelProperty(value = "생년월일")
    private LocalDate birth;

    @ApiModelProperty(value = "입사일")
    @Column(name = "hiredate")
    private LocalDate hireDate;

    @ApiModelProperty(value = "휴가개수")
    @Column(name = "vacation_count")
    private double vacationCount;

    @ApiModelProperty(value = "추가근무개수")
    @Column(name = "overtime_count")
    private double overtimeCount;

    @ApiModelProperty(value = "이메일인증토큰")
    @Column(name = "email_token")
    private String emailToken;

    @ApiModelProperty(value = "이메일인증토큰")
    @Column(name = "email_token_generated_at")
    private LocalDateTime emailTokenGeneratedAt;

    @ApiModelProperty(value = "이메일인증여부")
    @Column(name = "is_verified")
    private int isVerified;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
