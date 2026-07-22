package net.danshin.danshin_notice_api.models.genealogy_dates.upcoming;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record GenealogyResponse(
        @Nullable List<BirthEvent> beforeBirth,
        @Nullable List<BirthWouldEvent> beforeBirthWould,
        @Nullable List<DeathEvent> beforeDeath,
        @Nullable List<BirthEvent> todayBirth,
        @Nullable List<BirthWouldEvent> todayBirthWould,
        @Nullable List<DeathEvent> todayDeath,
        @Nullable List<BirthEvent> afterBirth,
        @Nullable List<BirthWouldEvent> afterBirthWould,
        @Nullable List<DeathEvent> afterDeath,
        boolean isEmpty
) {
}
