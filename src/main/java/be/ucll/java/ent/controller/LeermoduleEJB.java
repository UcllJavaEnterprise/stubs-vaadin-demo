package be.ucll.java.ent.controller;

import be.ucll.java.ent.domain.LeermoduleDTO;
import be.ucll.java.ent.model.LeermoduleDAO;
import be.ucll.java.ent.model.LeermoduleEntity;
import be.ucll.java.ent.model.StudentDAO;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
// Student - Enterprise Java Bean
public class LeermoduleEJB implements LeermoduleEJBLocal {

    @PersistenceContext(unitName = "LocalPostgresPU")
    private EntityManager em;
    private LeermoduleDAO dao;

    @PostConstruct
    public void init() {
        dao = new LeermoduleDAO(em);
    }

    @Override
    public long createLeermodule(LeermoduleDTO leermodule) throws IllegalArgumentException {
        // Code en beschrijving verondersteld als verplicht veld
        if (leermodule == null) throw new IllegalArgumentException("Leermodule aanmaken gefaald. Inputdata ontbreekt");
        if (leermodule.getCode() == null || leermodule.getCode().trim().length() == 0)
            throw new IllegalArgumentException("Leermodule aanmaken gefaald. Code ontbreekt");
        if (leermodule.getBeschrijving() == null || leermodule.getBeschrijving().trim().length() == 0)
            throw new IllegalArgumentException("Leermodule aanmaken gefaald. Beschrijving ontbreekt");

        LeermoduleDTO lm = null;
        try {
            lm = getLeermoduleByCode(leermodule.getCode());
            if (lm != null) {
                throw new IllegalArgumentException("Een leermodule met de code " + lm.getCode() + " bestaat al met beschrijving " + lm.getBeschrijving());
            }
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("Geen leermodule gevonden")){
                // No problem, continue
            } else {
                throw e;
            }
        }

        LeermoduleEntity lme = new LeermoduleEntity(0L,leermodule.getCode(), leermodule.getBeschrijving(), leermodule.getSchooljaar());
        dao.create(lme);

        leermodule.setId(lme.getId());
        return lme.getId();
    }

    @Override
    public LeermoduleDTO getLeermoduleById(long leermoduleId) throws IllegalArgumentException{
        if (leermoduleId <= 0) throw new IllegalArgumentException("Leermodule ophalen gefaald. Leermodule ID ontbreekt");

        Optional<LeermoduleEntity> value = dao.get(leermoduleId);
        if (value.isPresent()) {
            return new LeermoduleDTO(value.get().getId(), value.get().getCode(), value.get().getBeschrijving(), value.get().getSchooljaar());
        } else {
            throw new IllegalArgumentException("Geen leermodule gevonden met ID: " + leermoduleId);
        }
    }

    @Override
    public LeermoduleDTO getLeermoduleByCode(String code) throws IllegalArgumentException {
        if (code == null || code.trim().length() == 0)
            throw new IllegalArgumentException("Leermodule opzoeken op code gefaald. Inputdata ontbreekt");

        Optional<LeermoduleEntity> value = dao.getOneByCode(code);
        if (value.isPresent()) {
            return new LeermoduleDTO(value.get().getId(), value.get().getCode(), value.get().getBeschrijving(), value.get().getSchooljaar());
        } else {
            throw new IllegalArgumentException("Geen leermodule gevonden met code: " + code);
        }
    }

    @Override
    public void updateLeermodule(LeermoduleDTO leermodule) throws IllegalArgumentException {
        if (leermodule == null) throw new IllegalArgumentException("Leermodule wijzigen gefaald. Inputdata ontbreekt");
        if (leermodule.getId() <= 0) throw new IllegalArgumentException("Leermodule wijzigen gefaald. Leermodule ID ontbreekt");
        if (leermodule.getCode() == null || leermodule.getCode().trim().length() == 0)
            throw new IllegalArgumentException("Leermodule aanmaken gefaald. Code ontbreekt");
        if (leermodule.getBeschrijving() == null || leermodule.getBeschrijving().trim().length() == 0)
            throw new IllegalArgumentException("Leermodule aanmaken gefaald. Beschrijving ontbreekt");

        LeermoduleEntity lme = new LeermoduleEntity(leermodule.getId(),leermodule.getCode(), leermodule.getBeschrijving(), leermodule.getSchooljaar());
        dao.update(lme);
    }

    @Override
    public void deleteLeermodule(long leermoduleId) throws IllegalArgumentException {
        if (leermoduleId <= 0) throw new IllegalArgumentException("Leermodule verwijderen gefaald. Leermodule ID ontbreekt");
        dao.delete(leermoduleId);
    }

    @Override
    public List<LeermoduleDTO> getLeermodules(String code) throws IllegalArgumentException {
        if (code == null || code.trim().length() == 0) {
            throw new IllegalArgumentException("Leermodules ophalen met de code gefaald. Code leeg");
        }

        List<LeermoduleEntity> lst = dao.getLeermoduleByCode(code);
        return queryListToLeermoduleDTOList(lst);
    }

    @Override
    public List<LeermoduleDTO> getAllLeermodules() {
        return queryListToLeermoduleDTOList(dao.getAll());
    }

    @Override
    public long countLeermodules() {
        return dao.countAll();
    }

    private List<LeermoduleDTO> queryListToLeermoduleDTOList(List<LeermoduleEntity> lst){
        Stream<LeermoduleDTO> stream = lst.stream()
                .map(rec -> {
                    LeermoduleDTO dto = new LeermoduleDTO();
                    dto.setId(rec.getId());
                    dto.setCode(rec.getCode());
                    dto.setBeschrijving(rec.getBeschrijving());
                    dto.setSchooljaar(rec.getSchooljaar());
                    return dto;
                });
        return stream.collect(Collectors.toList());
    }

    // Methods hereunder only needed for Unit testing.
    public void setEm(EntityManager em) {
        this.em = em;
    }
    public void setDao(LeermoduleDAO dao) {
        this.dao = dao;
    }
}

