package net.danshin.danshin_notice_api.services;

import net.danshin.danshin_notice_api.helpers.MessageHelper;
import net.danshin.danshin_notice_api.models.genealogy_dates.upcoming.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageFormatterService {
    private static final String HTML_TEMPLATE = """
            <html>
            <head><meta charset='UTF-8'></head>
            <body style='font-family: Arial, sans-serif;'>
                <h2>%s</h2>
                %s
            </body>
            </html>
            """;
    private static final String TELEGRAM_TEMPLATE = "%s\n\n%s";
    private final MessageHelper messageHelper;
    private final String siteUrlPerson;

    public MessageFormatterService(
            MessageHelper messageHelper,
            @Value("${genealogy-site-url-person}") String siteUrlPerson
    ) {
        this.messageHelper = messageHelper;
        this.siteUrlPerson = siteUrlPerson;
    }

    public String formatEmailMessage(GenealogyResponse response) {
        String title = messageHelper.get("genealogy.title");
        String content = buildMessageContent(response, "html");
        return String.format(HTML_TEMPLATE, title, content);
    }

    public String formatTelegramMessage(GenealogyResponse response) {
        String title = messageHelper.get("genealogy.title");
        String content = buildMessageContent(response, "text");
        return String.format(TELEGRAM_TEMPLATE, title, content);
    }

    private String buildMessageContent(GenealogyResponse response, String format) {
        StringBuilder content = new StringBuilder();

        if (response.isEmpty()) {
            return messageHelper.get("genealogy.no.events");
        }

        boolean hasPastEvents = hasEvents(response.beforeBirth(), response.beforeBirthWould(), response.beforeDeath());
        if (hasPastEvents) {
            addTitle(content, "genealogy.past", format);
            addEvents(content, response.beforeBirth(), "genealogy.birthday", false, false, format);
            addEvents(content, response.beforeBirthWould(), "genealogy.birthday", true, false, format);
            addEvents(content, response.beforeDeath(), "genealogy.memory", false, false, format);
        }

        boolean hasTodayEvents = hasEvents(response.todayBirth(), response.todayBirthWould(), response.todayDeath());
        if (hasTodayEvents) {
            addTitle(content, "genealogy.today", format);
            addEvents(content, response.todayBirth(), "genealogy.birthday", false, false, format);
            addEvents(content, response.todayBirthWould(), "genealogy.birthday", true, false, format);
            addEvents(content, response.todayDeath(), "genealogy.memory", false, false, format);
        }

        boolean hasFutureEvents = hasEvents(response.afterBirth(), response.afterBirthWould(), response.afterDeath());
        if (hasFutureEvents) {
            addTitle(content, "genealogy.future", format);
            addEvents(content, response.afterBirth(), "genealogy.birthday", false, true, format);
            addEvents(content, response.afterBirthWould(), "genealogy.birthday", true, true, format);
            addEvents(content, response.afterDeath(), "genealogy.memory", false, true, format);
        }

        return content.toString();
    }

    private boolean hasEvents(List<?>... eventLists) {
        for (List<?> list : eventLists) {
            if (list != null && !list.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void addTitle(StringBuilder content, String key, String format) {
        String title = messageHelper.get(key);
        if ("html".equals(format)) {
            content.append("<h3>").append(title).append("</h3>");
        } else {
            content.append(title).append(":\n");
        }
    }

    private void addEvents(StringBuilder content, List<?> events, String eventTypeKey, boolean isWould, boolean isFuture, String format) {
        if (events == null || events.isEmpty()) {
            return;
        }

        String eventType = messageHelper.get(eventTypeKey);

        if ("html".equals(format)) {
            content.append("<ul>");
            for (Object event : events) {
                content.append("<li>").append(formatEvent(event, eventType, isWould, isFuture, "html")).append("</li>");
            }
            content.append("</ul>");
        } else {
            for (Object event : events) {
                content.append("  • ").append(formatEvent(event, eventType, isWould, isFuture, "text")).append("\n");
            }
        }
        content.append("\n");
    }

    private String formatEvent(Object event, String eventType, boolean isWould, boolean isFuture, String format) {
        if (event instanceof BirthEvent birth) {
            return formatBirthEvent(birth, eventType, isWould, isFuture, format);
        } else if (event instanceof BirthWouldEvent birthWould) {
            return formatBirthWouldEvent(birthWould, eventType, isWould, format);
        } else if (event instanceof DeathEvent death) {
            return formatDeathEvent(death, format);
        }
        return "";
    }

    private String formatBirthEvent(BirthEvent event, String eventType, boolean isWould, boolean isFuture, String format) {
        Date date = event.date();
        Person person = event.person();
        Age age = event.age();

        String dateStr = formatDate(date, format);
        String personStr = formatPerson(person);
        String ageStr = formatAge(age, isWould, false, isFuture);

        return String.format("%s %s %s (%s)", dateStr, eventType, personStr, ageStr);
    }

    private String formatBirthWouldEvent(BirthWouldEvent event, String eventType, boolean isWould, String format) {
        Date date = event.date();
        Person person = event.person();
        Age age = event.age();

        String dateStr = formatDate(date, format);
        String personStr = formatPerson(person);
        String ageStr = formatAge(age, isWould, false, false);

        return String.format("%s %s %s (%s)", dateStr, eventType, personStr, ageStr);
    }

    private String formatDeathEvent(DeathEvent event, String format) {
        Date date = event.date();
        Person person = event.person();
        Age age = event.age();
        Interval interval = event.interval();

        String dateStr = formatDate(date, format);
        String personStr = formatPerson(person);
        String ageStr = formatAge(age, false, true, false);
        String intervalStr = formatInterval(interval);

        String memoryText = messageHelper.get("genealogy.memory");
        String agoText = messageHelper.get("genealogy.ago");
        String ageText = messageHelper.get("genealogy.age");

        return String.format("%s %s %s (%s %s %s %s)",
                dateStr, memoryText, personStr, intervalStr, agoText, ageText, ageStr);
    }

    private String formatDate(Date date, String format) {
        if (date == null) return messageHelper.get("genealogy.date.unknown");

        String dateStr = String.format("%s-%s-%s", date.year(), date.month(), date.day());

        if ("html".equals(format)) {
            return String.format("<b>%s</b>", dateStr);
        }
        return dateStr;
    }

    private String formatPerson(Person person) {
        if (person == null) return messageHelper.get("genealogy.person.unknown");

        String fullName = formatPersonFullName(person);
        String profileUrl = siteUrlPerson + "/" + person.id();

        return String.format("<a href='%s'>%s</a>", profileUrl, fullName);
    }

    private String formatPersonFullName(Person person) {
        StringBuilder sb = new StringBuilder();
        if (person.surname() != null) sb.append(person.surname()).append(" ");
        if (person.name() != null) sb.append(person.name()).append(" ");
        if (person.patronymic() != null) sb.append(person.patronymic());
        return sb.toString().trim();
    }

    private String formatAge(Age age, boolean isWould, boolean isDeath, boolean isFuture) {
        if (age == null) return messageHelper.get("genealogy.age.unknown");

        int years = age.y();
        String yearsStr = formatYears(years);

        if (isDeath) {
            return yearsStr;
        } else if (isWould) {
            return String.format(messageHelper.get("genealogy.age.would"), years);
        } else if (isFuture) {
            return String.format(messageHelper.get("genealogy.age.future"), yearsStr);
        } else {
            return String.format(messageHelper.get("genealogy.age.turned"), yearsStr);
        }
    }

    private String formatInterval(Interval interval) {
        if (interval == null) return messageHelper.get("genealogy.interval.unknown");

        int years = interval.y();
        int months = interval.m();
        int days = interval.d();

        if (years > 0) {
            return formatYears(years);
        } else if (months > 0) {
            return formatMonths(months);
        } else if (days > 0) {
            return formatDays(days);
        }

        return "";
    }

    private String formatYears(int years) {
        if (years % 10 == 1 && years % 100 != 11) {
            return String.format(messageHelper.get("genealogy.year.one"), years);
        } else if (years % 10 >= 2 && years % 10 <= 4 && (years % 100 < 10 || years % 100 >= 20)) {
            return String.format(messageHelper.get("genealogy.year.few"), years);
        } else {
            return String.format(messageHelper.get("genealogy.year.many"), years);
        }
    }

    private String formatMonths(int months) {
        if (months % 10 == 1 && months % 100 != 11) {
            return String.format(messageHelper.get("genealogy.month.one"), months);
        } else if (months % 10 >= 2 && months % 10 <= 4 && (months % 100 < 10 || months % 100 >= 20)) {
            return String.format(messageHelper.get("genealogy.month.few"), months);
        } else {
            return String.format(messageHelper.get("genealogy.month.many"), months);
        }
    }

    private String formatDays(int days) {
        if (days % 10 == 1 && days % 100 != 11) {
            return String.format(messageHelper.get("genealogy.day.one"), days);
        } else if (days % 10 >= 2 && days % 10 <= 4 && (days % 100 < 10 || days % 100 >= 20)) {
            return String.format(messageHelper.get("genealogy.day.few"), days);
        } else {
            return String.format(messageHelper.get("genealogy.day.many"), days);
        }
    }
}