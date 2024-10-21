package lems.cowshed.domain.event;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaEventRepository implements EventRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public void save(Event event) {
        em.persist(event);
    }

    @Override
    public Optional<Event> findById(Long id) {
        Event event = em.find(Event.class, id);
        return Optional.ofNullable(event);
    }

    @Override
    public List<Event> findAll() {
        return em.createQuery("select e from Event e", Event.class)
                .getResultList();
    }

    @Override
    public List<Event> findByCategory(Category category) {
        return em.createQuery("select e from Event e where e.category" +
                "= :category", Event.class)
                .setParameter("category", category)
                .getResultList();
    }

    @Override
    public List<Event> findByKeyword(String keyword) {
        return em.createQuery("select e from Event e where e.content" +
                "like %:keyword%", Event.class)
                .setParameter("keyword", keyword)
                .getResultList();

    }

    @Override
    public List<Event> findByDistance() {
        return null;
    }

    @Override
    public void delete(Event event) {
        em.remove(event);
    }

    @Override
    public void edit(Event event) {

    }
}
