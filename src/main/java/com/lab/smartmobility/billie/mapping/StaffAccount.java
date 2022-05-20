package com.lab.smartmobility.billie.mapping;

import com.lab.smartmobility.billie.entity.Staff;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class StaffAccount extends User {
    private final Staff staff;

    public StaffAccount(Staff staff) {
        super(staff.getEmail(), staff.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.staff=staff;
    }
}
