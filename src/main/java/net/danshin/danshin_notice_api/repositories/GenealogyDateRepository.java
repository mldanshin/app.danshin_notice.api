package net.danshin.danshin_notice_api.repositories;

import net.danshin.danshin_notice_api.models.genealogy_dates.DateType;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsRequest;
import net.danshin.danshin_notice_api.models.genealogy_dates.SettingsResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface GenealogyDateRepository {
    SettingsResponse get(String uuid);

    void create(String uuidUser, SettingsRequest request);

    void delete(String uuidUser);

    void createDate(int personId, DateType dateType);

    void deletePerson(int id);
}
