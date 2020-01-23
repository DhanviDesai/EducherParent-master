package com.example.educher_parent;

import java.io.Serializable;

public class GraphModel implements Serializable {

    private String name;
    private int usageTime;

    public GraphModel(String name, int usageTime) {
        this.name = name;
        this.usageTime = usageTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(int usageTime) {
        this.usageTime = usageTime;
    }
}
