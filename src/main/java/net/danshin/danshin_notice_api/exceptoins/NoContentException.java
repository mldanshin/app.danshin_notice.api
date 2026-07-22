package net.danshin.danshin_notice_api.exceptoins;

public class NoContentException extends RuntimeException {
    public NoContentException(String message) {
        super(message);
    }
}
