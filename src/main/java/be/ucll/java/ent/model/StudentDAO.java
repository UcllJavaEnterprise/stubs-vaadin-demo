package be.ucll.java.ent.model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO implements Dao<StudentEntity> {

    // JPA EntityManager
    private final EntityManager em;

    // Constructor with EntityManager
    public StudentDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(StudentEntity student) {
        em.persist(student);
    }

    @Override
    // Gebruik Optional om aanroepende code af te dwingen en rekening te houden met NULL
    public Optional<StudentEntity> get(long studentId) {
        return Optional.ofNullable(em.find(StudentEntity.class, studentId));
    }

    @Override
    // Zonder Optional kan de return value null zijn en kan je alleen maar hopen
    // dat de aanroepende code daarmee rekening houdt
    public StudentEntity read(long studentId) {
        return em.find(StudentEntity.class, studentId);
    }

    public Optional<StudentEntity> getOneByName(String name) {
        try {
            StudentEntity stud = null;
            try {
                Query q = em.createQuery("select e from StudentEntity e where lower(e.naam) = :p1");
                q.setParameter("p1", name);
                stud = (StudentEntity) q.getSingleResult();
            } catch (NoResultException e) {
                // ignore
            }
            return Optional.ofNullable(stud);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(StudentEntity student) {
        em.merge(student);
    }

    @Override
    public void delete(long studentId) {
        StudentEntity ref = em.getReference(StudentEntity.class, studentId);
        if (ref != null){
            em.remove(ref);
        } else {
            // Already gone
        }
    }

    public List<StudentEntity> getStudents(String naam, String voornaam){
        try {
            List<StudentEntity> lst = new ArrayList();
            try {
                String queryString = "select s from StudentEntity s where 1 = 1 ";
                if (naam != null && naam.trim().length() > 0) {
                    String cleanNaam = naam.toLowerCase().trim().replace("'","''");
                    queryString += "and lower(s.naam) like '%" + cleanNaam + "%' ";
                }
                if (voornaam != null && voornaam.trim().length() > 0) {
                    String cleanVoornaam = voornaam.toLowerCase().trim().replace("'","''");
                    queryString += "and lower(s.voornaam) like '%" + cleanVoornaam  + "%' ";
                }

                // System.out.println("Query: " + queryString);
                Query query = em.createQuery(queryString);
                lst = query.getResultList();
            } catch (NoResultException e) {
                // ignore
            }
            return lst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<StudentEntity> getAll() {
        return em.createNamedQuery("Student.getAll").getResultList();
    }

    @Override
    public long countAll() {
        Object o = em.createNamedQuery("Student.countAll").getSingleResult();
        return (Long) o;
    }
}



