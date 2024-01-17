package com.cheering.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("member")
public class Member extends User {
    
}
