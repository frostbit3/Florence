package com.florence.model.player;

public enum Gender {

    MALE(0),
    FEMALE(1);

    private final int value;

    private Gender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
