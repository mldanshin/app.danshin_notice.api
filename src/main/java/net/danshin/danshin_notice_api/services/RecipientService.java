package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.repositories.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecipientService {
    private final RecipientRepository repository;

    @Autowired
    public RecipientService(RecipientRepository repository) {
        this.repository = repository;
    }

    public void createRecipient(String uuidUser) {
        repository.createRecipient(uuidUser);
    }

    public void deleteRecipient(String uuidUser) {
        repository.deleteRecipient(uuidUser);
    }
}
