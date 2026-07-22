package net.danshin.danshin_notice_api.exceptoins;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(String message) {
        super(message);
    }
}
