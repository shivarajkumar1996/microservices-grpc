package com.microservices.grpc.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/** This is a POJO representation of User protobuf message. You can find the exact same fields in
 * User message defined in common.proto file
 */
@Getter
@Setter
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPojo {

    @NotNull(message = "Id cannot be null")
    @Positive(message = "Id should be a positive number")
    @XmlElement(name = "id")
    private int id;

    @NotEmpty(message = "Name cannot be empty")
    @NotBlank(message = "Name cannot be blank")
    @XmlElement(name = "name")
    @Pattern(regexp = "^[a-zA-Z]+(?:\\s+[a-zA-Z]+)*$", message = "Name must contain only alphabets along with white spaces.")
    private String name;


    @Pattern(regexp = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$", message = "DOB must of pattern: yyyy-mm-dd")
    @XmlElement(name = "dob")
    private String dob;

    @PositiveOrZero(message = "Salary should be greater than or equal to 0")
    @XmlElement(name = "salary")
    private double salary;


    @Override
    public String toString() {
        return "{" +
                "id: " + id +
                ", name:'" + name + '\'' +
                ", dob: '" + dob + '\'' +
                ", salary: " + salary +
                '}';
    }
}
