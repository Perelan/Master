package no.uio.testmodule;

import java.util.Date;

public class User {
    private String name, gender;
    private int height, weight, age;
    private Date createdAt;

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
