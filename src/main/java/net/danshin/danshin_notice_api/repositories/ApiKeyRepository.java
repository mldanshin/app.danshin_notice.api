package net.danshin.danshin_notice_api.repositories;

public interface ApiKeyRepository {
    String findRoleByApiKey(String keyHash);
}
