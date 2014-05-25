package net.magicd.io.couchbase.mock;

public class TestData {
    private int id;
    private String name;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    private String gender;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;

    public int getId() {
        return id;
    }

    public TestData setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TestData setName(String name) {
        this.name = name;
        return this;
    }
}

