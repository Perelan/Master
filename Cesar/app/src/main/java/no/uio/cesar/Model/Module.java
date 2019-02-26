package no.uio.cesar.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "module_table")
public class Module {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name, packageName;

    public Module(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
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

    public String getPackageName() {
        return packageName;
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
