package no.uio.cesar.Model;

import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;

// User is stored in a SharedPreference

public class User {

    private String name, gender;
    private int height, weight, age;
    private Date createdAt;

    public User(String name, String gender, int height, int weight, int age) {
        this.name = name;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.createdAt = new Date();
    }

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

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", age=" + age +
                ", createdAt=" + createdAt +
                '}';
    }
}
