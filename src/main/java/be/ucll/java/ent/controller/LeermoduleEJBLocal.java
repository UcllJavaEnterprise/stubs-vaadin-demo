package be.ucll.java.ent.controller;

import be.ucll.java.ent.domain.LeermoduleDTO;

import javax.ejb.EJBException;
import javax.ejb.Local;
import java.util.List;

@Local
public interface LeermoduleEJBLocal {
    // Create methods
    long createLeermodule(LeermoduleDTO student) throws EJBException;

    // Read methods
    LeermoduleDTO getLeermoduleById(long leermoduleId) throws EJBException;
    LeermoduleDTO getLeermoduleByCode(String searchTerm) throws EJBException;

    // Update methods
    void updateLeermodule(LeermoduleDTO student) throws EJBException;

    // Delete methods
    void deleteLeermodule(long leermoduleId) throws EJBException;

    // Search methods
    List<LeermoduleDTO> getLeermodules(String code) throws EJBException;
    List<LeermoduleDTO> getAllLeermodules();
    long countLeermodules();
}
