package com.example.demo.Model;

import com.example.demo.Utils.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by snsoft on 13/3/2019.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatfromAble {
    @Id
    @GeneratedValue
    private int id;
    private String content;
    private String state;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PlatfromAble() {
        this.state = Config.Normal;
    }

    @Override
    public String toString() {
        return "PlatfromAble{" +
                "content='" + content + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
