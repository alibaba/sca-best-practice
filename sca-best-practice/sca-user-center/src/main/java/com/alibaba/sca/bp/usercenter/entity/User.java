package com.alibaba.sca.bp.usercenter.entity;

import com.alibaba.codeless.framework.autoconfigure.annotation.EnableCodeless;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zuoxiaolong
 */
@EnableCodeless
@Entity(name = "us_user")
@Data
public class User {

    @Id
    @GeneratedValue
    private Integer userId;

    @NotNull
    @Size(min = 10, max = 30)
    private String userName;

    @NotNull
    @Size(min = 10, max = 30)
    private String password;

    @Size(min = 11, max = 11)
    @Pattern(regexp = "[0-9]{11}")
    private String phone;

}