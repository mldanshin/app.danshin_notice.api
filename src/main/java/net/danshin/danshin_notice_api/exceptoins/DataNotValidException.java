package net.danshin.danshin_notice_api.exceptoins;

public class DataNotValidException extends RuntimeException {
    public DataNotValidException(String message) {
        super(message);
    }
}
