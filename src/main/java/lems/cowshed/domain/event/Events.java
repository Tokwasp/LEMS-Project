package lems.cowshed.domain.event;

import java.util.List;

public class Events {

    private List<Event> events;

    private Events(List<Event> events) {
        this.events = events;
    }

    public static Events of(List<Event> events){
        return new Events(events);
    }

    public List<Long> extractIds() {
        return events.stream()
                .map(Event::getId)
                .toList();
    }
}
