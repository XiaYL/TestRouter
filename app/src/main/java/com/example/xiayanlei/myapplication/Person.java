package com.example.xiayanlei.myapplication;

import java.io.Serializable;

/**
 * Created by xiayanlei on 2017/3/14.
 */

public class Person implements Serializable {
    String name;
    int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
