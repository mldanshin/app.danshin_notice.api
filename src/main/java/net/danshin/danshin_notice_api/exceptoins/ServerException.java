package net.danshin.danshin_notice_api.exceptoins;

public class ServerException extends RuntimeException {
    public ServerException(String message) {
        super(message);
    }
}
