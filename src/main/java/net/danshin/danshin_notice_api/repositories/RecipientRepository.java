package net.danshin.danshin_notice_api.repositories;

public interface RecipientRepository {
    void createRecipient(String uuidUser);

    void deleteRecipient(String uuidUser);

    Integer existRecipient(String uuidUser);
}
