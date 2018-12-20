package no.uio.cesar.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private int age, height;

    private char gender;

    public User(String name, int age, int height, char gender) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.gender = gender;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public char getGender() {
        return gender;
    }
}
