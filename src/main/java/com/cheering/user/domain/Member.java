package com.cheering.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("member")
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends User {

}
