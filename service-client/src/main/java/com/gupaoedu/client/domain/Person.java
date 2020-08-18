package com.gupaoedu.client.domain;

import java.io.Serializable;

/**
 * @author fred
 * @date 2020/8/16 5:00 下午
 * @description todo
 */
public class Person implements Serializable {

    private static final long serialVersionUID = 1988733970311918105L;
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
