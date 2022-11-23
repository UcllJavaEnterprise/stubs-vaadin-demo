package be.ucll.java.ent.model;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LeermoduleDAO  implements Dao<LeermoduleEntity> {

    // JPA EntityManager
    private final EntityManager em;

    // Constructor with EntityManager
    public LeermoduleDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public void create(LeermoduleEntity leermodule) {
        em.persist(leermodule);
    }

    @Override
    public Optional<LeermoduleEntity> get(long leermoduleId) {
        return Optional.ofNullable(em.find(LeermoduleEntity.class, leermoduleId));
    }

    @Override
    public LeermoduleEntity read(long leermoduleId) {
        return em.find(LeermoduleEntity.class, leermoduleId);
    }

    public Optional<LeermoduleEntity> getOneByCode(String code) {
        try {
            LeermoduleEntity stud = null;
            try {
                Query q = em.createQuery("select e from LeermoduleEntity e where lower(e.code) like :p1");
                q.setParameter("p1", "%" + code.trim().toLowerCase().replace("'", "''") + "%");
                stud = (LeermoduleEntity) q.getSingleResult();
            } catch (NoResultException e) {
                // ignore
            }
            return Optional.ofNullable(stud);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(LeermoduleEntity leermodule) {
        em.merge(leermodule);
    }

    @Override
    public void delete(long leermoduleId) {
        LeermoduleEntity ref = em.getReference(LeermoduleEntity.class, leermoduleId);
        if (ref != null){
            em.remove(ref);
        } else {
            // Already gone
        }
    }

    public List<LeermoduleEntity> getLeermoduleByCode(String code) {
        try {
            List<LeermoduleEntity> lst = new ArrayList();
            if (code != null && code.trim().length() > 0) {
                try {
                    String cleanCode = code.toLowerCase().trim().replace("'", "''");
                    // JPQL = Java Persistence Query Language
                    String queryString = "select l from LeermoduleEntity l where lower(l.code) like '%" + cleanCode + "%' ";
                    Query query = em.createQuery(queryString);
                    lst = query.getResultList();
                } catch (NoResultException e) {
                    // ignore, is no problem
                }
            }
            return lst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LeermoduleEntity> getAll() {
        return em.createNamedQuery("Leermodule.getAll").getResultList();
    }

    @Override
    public long countAll() {
        Object o = em.createNamedQuery("Leermodule.countAll").getSingleResult();
        return (Long) o;
    }
}
