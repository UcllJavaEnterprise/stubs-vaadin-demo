package be.ucll.java.ent.controller;

import be.ucll.java.ent.domain.StudentDTO;

import javax.ejb.EJBException;
import javax.ejb.Local;
import java.util.List;

@Local
public interface StudentEJBLocal {
    // Create methods
    long createStudent(StudentDTO student) throws EJBException;

    // Read methods
    StudentDTO getStudentById(long id) throws EJBException;
    StudentDTO getStudentByName(String name) throws EJBException;

    // Update methods
    void updateStudent(StudentDTO student) throws EJBException;

    // Delete methods
    void deleteStudent(long id) throws EJBException;

    // Search methods
    List<StudentDTO> getStudents(String naam, String voornaam) throws EJBException;
    List<StudentDTO> getAllStudents();
    long countStudents();
}
