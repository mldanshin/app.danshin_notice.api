package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsRequest;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsResponse;
import net.danshin.danshin_notice_api.repositories.GenealogyDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenealogyDateService {
    private final GenealogyDateRepository repository;

    @Autowired
    public GenealogyDateService(GenealogyDateRepository repository) {
        this.repository = repository;
    }

    public SettingsResponse get(String uuidUser) {
        return repository.get(uuidUser);
    }

    public void create(String uuidUser, SettingsRequest request) {
        repository.create(uuidUser, request);
    }

    public void delete(String uuidUser) {
        repository.delete(uuidUser);
    }

    public void handleDateCreated(int personId, DateType dateType) {
        repository.createDate(personId, dateType);
    }

    public void handlePersonDeleted(int id) {
        repository.deletePerson(id);
    }
}
