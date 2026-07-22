package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.repositories.ApiKeyRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {
    private final ApiKeyRepository repository;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public String getRoleIfValid(String apiKey) {
        return repository.findRoleByApiKey(apiKey);
    }
}
