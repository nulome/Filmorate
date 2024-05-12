package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.related.EventType;
import ru.yandex.practicum.filmorate.related.Operation;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Builder
@Data
public class Event {
    @JsonProperty("eventId")
    int id;
    int userId;
    int entityId;
    @Enumerated(EnumType.STRING)
    EventType eventType;
    @Enumerated(EnumType.STRING)
    Operation operation;
    @JsonProperty("timestamp")
    Long eventDate;
}
