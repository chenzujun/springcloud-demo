package Bean;

import excel.in.ExcelCell;
import excel.in.ImpImportLog;

import java.io.Serializable;

public class User extends ImpImportLog implements Serializable {

    @ExcelCell(value = "姓名")
    private String name;
    @ExcelCell(value = "年龄")
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
