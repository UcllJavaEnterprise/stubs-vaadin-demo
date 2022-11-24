package be.ucll.java.ent.controller;

import be.ucll.java.ent.domain.StudentDTO;
import be.ucll.java.ent.model.StudentDAO;
import org.junit.*;
import org.junit.rules.ExpectedException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class StudentEJBTest {
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private StudentEJB ejb;
    private StudentDTO testStudent;
    private long testStudentID;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before //Before every single test
    public void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("LocalPostgresPU2");
        entityManager = entityManagerFactory.createEntityManager();
        ejb = new StudentEJB();
        ejb.setEm(entityManager);
        ejb.setDao(new StudentDAO(entityManager));

        entityManager.getTransaction().begin();

        // Create some testdata
        Calendar myCalendar = new GregorianCalendar(1999, 11, 25);
        Date myDate = myCalendar.getTime();
        testStudent = new StudentDTO(0,"D'hooghe", "Sophie", myDate);
        testStudentID = ejb.createStudent(testStudent);
        // System.out.println("Unit tests. Created test student: " + testStudent);

        entityManager.getTransaction().commit();
    }

    // 1. Alle READ / Opzoeken testen

    @Test // Test een student ID 0
    public void getStudentById0(){
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Student ID ontbreekt");

        ejb.getStudentById(0);
    }

    @Test // Test een student ID met een negatief getal
    public void getStudentByIdNegGetal(){
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Student ID ontbreekt");

        ejb.getStudentById(-1);
    }

    @Test
    public void getStudentByIdOK(){
        StudentDTO s = ejb.getStudentById(testStudentID);
        assertEquals(s, testStudent);
    }

    @Test // Test dat student Naam niet null is
    public void getStudentByNameNull(){
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Ongeldige naam meegegeven");

        ejb.getStudentByName(null);
    }

    @Test // Test dat student Naam niet leeg is
    public void getStudentByNameLeeg(){
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Geen naam meegegeven");

        ejb.getStudentByName("");
    }

    @Test // Test dat student Naam gelijk aan 'TestNaam' 1 student terug geeft => OK test
    public void getStudentsOKNaam(){
        List<StudentDTO> lst = ejb.getStudents("D'hooghe", null);
        assertEquals(1, lst.size());
    }

    @Test // Test dat student Naam gelijk aan 'TestNaam' 1 student terug geeft => OK test case insensitive
    public void getStudentsOKCaseInsensitive(){
        List<StudentDTO> lst = ejb.getStudents("d'hooghe", null);
        assertEquals(1, lst.size());
    }

    @Test // Test dat student Naam gelijk aan 'TestNaam' 1 student terug geeft => OK test
    public void getStudentsOKPartial(){
        List<StudentDTO> lst = ejb.getStudents("D'hoogh", null);
        assertEquals(1, lst.size());
    }

    @Test
    public void getStudentsOK() {
        List<StudentDTO> lst = ejb.getStudents("D'hooghe", "Sophie");
        assertEquals(1, lst.size());
    }

    @Test
    public void getStudentsPartialLowercase() {
        List<StudentDTO> lst = ejb.getStudents("d'hooghe", "soph");
        assertEquals(1, lst.size());
    }

    @Test
    public void getStudentsNaamNull() {
        List<StudentDTO> lst = ejb.getStudents(null, "Sophie");
        assertEquals(1, lst.size());
    }

    @Test
    public void getStudentsNaamLeeg() {
        List<StudentDTO> lst = ejb.getStudents("", "Sophie");
        assertEquals(1, lst.size());
    }

    @Test
    public void getStudentsNaamWhitespace() {
        List<StudentDTO> lst = ejb.getStudents(" ", "Sophie");
        assertEquals(1, lst.size());
    }

    @Test
    public void getAllStudentsOK(){
        List<StudentDTO> lst = ejb.getAllStudents();
        assert(lst.size() > 0);
    }

    @Test
    public void getAllStudentsCount(){
        List<StudentDTO> lst = ejb.getAllStudents();
        long cnt = ejb.countStudents();
        assertEquals(lst.size(), cnt);
    }

    // 2. Alle UPDATE / wijzigen testen

    @Test
    public void updateStudentNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Inputdata ontbreekt");

        StudentDTO student = null;
        ejb.updateStudent(student);
    }

    @Test // Test dat student ID niet 0 is
    public void updateStudent0() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Student wijzigen gefaald. Student ID ontbreekt");

        StudentDTO student = new StudentDTO();
        student.setId(0);
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student ID gen negatief getal is
    public void updateStudentNegGetal() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Student ID ontbreekt");

        StudentDTO student = new StudentDTO();
        student.setId(-1);
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Naam niet null is
    public void updateStudentNaamNull() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam(null);
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Naam niet leeg is
    public void updateStudentNaamEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam(" ");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Naam niet te lang is
    public void updateStudentNaamTeLang() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("128");

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);

        String teststring = "";
        for (int i = 0; i < 128; i++){
            teststring += "X";
        }

        student.setNaam(teststring);
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Voornaam niet null is
    public void updateStudentVoornaamNull() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam("TestNaam");
        student.setVoornaam(null);
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Voornaam niet leeg is
    public void updateStudentVoornaamEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam("TestNaam");
        student.setVoornaam("");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Voornaam niet te lang is
    public void updateStudentVoornaamTeLang() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam("TestNaam");

        String teststring = "";
        for (int i = 0; i < 128; i++){
            teststring += "X";
        }
        student.setVoornaam(teststring);
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student Geboortedatum niet null is
    public void updateStudentGeboortedatumNull() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(null);
        ejb.updateStudent(student);
    }

    @Test // Test dat student Geboortedatum niet in de toekomst ligt
    public void updateStudentGeboortedatum() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(2100, 1, 1).getTime());
        ejb.updateStudent(student);
    }

    @Test // Test dat student OK update
    public void updateStudentOK() {

        StudentDTO student = new StudentDTO();
        student.setId(testStudentID);
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.updateStudent(student);

        StudentDTO tempStudent = ejb.getStudentById(testStudentID);
        assertEquals(student, tempStudent);
    }

    // 3. Alle Create / Aanmaken testen

    @Test
    public void createStudentNull() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Alle data vereist voor het aanmaken van een student ontbreekt");

        StudentDTO student = null;
        ejb.createStudent(student);
    }

    @Test // Test dat student Naam niet null is
    public void createStudentNaamNull() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam(null);
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // Test dat student Naam niet leeg is
    public void createStudentNaamEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam("");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // Test dat student Naam niet te lang is
    public void createStudentNaamTeLang() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        String teststring = "";
        for (int i = 0; i < 128; i++){
            teststring += "X";
        }
        student.setNaam(teststring);
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // Test dat student Voornaam niet null is
    public void createStudentVoornaamNull() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam");
        student.setVoornaam(null);
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // Test dat student Voornaam niet leeg is
    public void createStudentVoornaamEmpty() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam");
        student.setVoornaam("");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // Test dat student Voornaam niet te lang is
    public void createStudentVoornaamTeLang() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam");
        String teststring = "";
        for (int i = 0; i < 128; i++){
            teststring += "X";
        }
        student.setVoornaam(teststring);
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // Test dat student Geboortedatum niet null is
    public void createStudentGeboortedatumNull() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        student.setGeboortedatum(null);
        ejb.createStudent(student);
    }

    @Test
    public void createStudentDOBFuture(){
        // 0. Checken of de verwachte exceptie
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("toekomst");

        // 1. Testdata aanmaken
        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam");
        student.setVoornaam("TestVoornaam");
        Date testDate = new GregorianCalendar(2099, 1, 1).getTime();
        student.setGeboortedatum(testDate);

        // 2. Test uitvoeren
        ejb.createStudent(student);
    }

    @Test // Maak eenzelfde student aan als de testdata
    public void createStudentDuplicate() {
        expectedEx.expect(IllegalArgumentException.class);

        StudentDTO student = new StudentDTO();
        student.setNaam("D'hooghe");
        student.setVoornaam("Sophie");
        student.setGeboortedatum(new GregorianCalendar(1999, 11, 25).getTime());
        ejb.createStudent(student);
    }

    @Test // OK Test
    public void createStudentOK() {

        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam2");
        student.setVoornaam("TestVoornaam2");
        student.setGeboortedatum(new GregorianCalendar(1999, 1, 1).getTime());
        long tempid = ejb.createStudent(student);

        StudentDTO tempStudent = ejb.getStudentById(tempid);
        assertEquals(student, tempStudent);

        ejb.deleteStudent(tempid);
    }

    // 4. Alle Delete / Verwijderen testen

    @Test // Test dat student ID niet 0 is
    public void deleteStudent0(){
        expectedEx.expect(IllegalArgumentException.class);

        ejb.deleteStudent(0);
    }

    @Test // Test dat student Geboortedatum niet in de toekomst ligt
    public void deleteStudentOK() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Geen student gevonden met ID");

        StudentDTO student = new StudentDTO();
        student.setNaam("TestNaam3");
        student.setVoornaam("TestVoornaam3");
        student.setGeboortedatum(new GregorianCalendar(2001, 5, 6).getTime());
        long tempid = ejb.createStudent(student);

        ejb.deleteStudent(tempid);

        StudentDTO s = ejb.getStudentById(tempid);
        assertNull(s);
    }

    @After // After every single test
    public void tearDown(){
        entityManager.getTransaction().begin();
        ejb.deleteStudent(testStudentID);
        entityManager.getTransaction().commit();

        entityManager.close();
        entityManagerFactory.close();
    }

}
