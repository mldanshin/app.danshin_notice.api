package net.danshin.danshin_notice_api.models.genealogy_dates;

public enum DateType {
    BIRTH(1), DEATH(2);

    private final int value;

    DateType(int value) {
        this.value = value;
    }

    public static DateType fromValue(int value) {
        for (DateType type : DateType.values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }

    public int getValue() {
        return value;
    }
}
