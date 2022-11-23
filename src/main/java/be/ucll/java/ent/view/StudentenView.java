package be.ucll.java.ent.view;

import be.ucll.java.ent.controller.StudentEJBLocal;
import be.ucll.java.ent.domain.StudentDTO;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@Route("studenten")
@PageTitle("StuBS")
public class StudentenView extends VerticalLayout implements AfterNavigationObserver {
    @EJB
    private StudentEJBLocal studentController;

    private SplitLayout splitLayout;
    private VerticalLayout c1;
    private HorizontalLayout r1;
    private VerticalLayout c2;
    private FormLayout frm;
    private HorizontalLayout r3;

    private Label lblNaam;
    private TextField txtNaamSearch;
    private Button btnSearch;

    private Grid<StudentDTO> grid;

    private Label lblID;
    private TextField txtVoornaam;
    private TextField txtNaam;
    private DatePicker datGeboorte;
    private Button btnCancel;
    private Button btnCreate;
    private Button btnUpdate;
    private Button btnDelete;

    @PostConstruct
    public void buildUI() {
        this.setSizeFull();

        splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.addToPrimary(createGridLayout());
        splitLayout.addToSecondary(createEditorLayout());
        add(splitLayout);
    }

    private Component createGridLayout() {
        c1 = new VerticalLayout();
        c1.setWidthFull();

        r1 = new HorizontalLayout();
        lblNaam = new Label("Naam (bevat)");
        txtNaamSearch = new TextField();
        //txtNaamSearch.setValueChangeMode(ValueChangeMode.EAGER);
        //txtNaamSearch.addValueChangeListener(e -> handleClickSearch(null));
        r1.add(lblNaam);
        r1.add(txtNaamSearch);

        btnSearch = new Button("Zoeken");
        // Very old Java 7 way of handling events
        btnSearch.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                handleClickSearch(event);
            }
        });
        r1.add(btnSearch);

        grid = new Grid<>();
        grid.setItems(new ArrayList<StudentDTO>(0));
        //grid.addColumn(StudentDTO::getVoornaam).setHeader("Voornaam").setSortable(true);
        grid.addColumn(student -> student.getVoornaam()).setHeader("Voornaam").setSortable(true);
        grid.addColumn(StudentDTO::getNaam).setHeader("Naam").setSortable(true);
        grid.addColumn(StudentDTO::getGeboortedatumstr).setHeader("Geboortedatum");
        grid.setHeightFull();

        //when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        c1.add(r1);
        c1.add(grid);
        c1.setWidth("75%");
        return c1;
    }

    private Component createEditorLayout() {
        c2 = new VerticalLayout();

        lblID = new Label("");

        txtVoornaam = new TextField();
        txtVoornaam.setRequired(true);
        txtVoornaam.setMaxLength(128);
        txtVoornaam.setErrorMessage("Verplicht veld!");

        txtNaam = new TextField();
        txtNaam.setRequired(true);
        txtNaam.setMaxLength(128);
        txtNaam.setErrorMessage("Verplicht veld!");

        datGeboorte = new DatePicker();
        LocalDate now = LocalDate.now();
        datGeboorte.setPlaceholder("dd/mm/jjjj");
        //datGeboorte.setValue(now);
        datGeboorte.setMin(now.minusYears(100));
        datGeboorte.setMax(now);
        datGeboorte.addInvalidChangeListener(e -> datGeboorte.setErrorMessage("Verplicht veld. Ongeldig datumformaat of datum in de toekomst"));
        datGeboorte.setLocale(new Locale("nl", "BE"));
        datGeboorte.setClearButtonVisible(true);

        frm = new FormLayout();
        frm.addFormItem(txtVoornaam, "Voornaam");
        frm.addFormItem(txtNaam, "Naam");
        frm.addFormItem(datGeboorte, "Geboortedatum");

        r3 = new HorizontalLayout();
        r3.setWidthFull();
        r3.setSpacing(true);

        btnCancel = new Button("Annuleren");
        btnCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCancel.addClickListener(e -> handleClickCancel(e));

        btnCreate = new Button("Toevoegen");
        btnCreate.addClickListener(e -> handleClickCreate(e));

        btnUpdate = new Button("Opslaan");
        btnUpdate.addClickListener(e -> handleClickUpdate(e));
        btnUpdate.setVisible(false);

        btnDelete = new Button("Verwijderen");
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnDelete.addClickListener(e -> handleClickDelete(e));
        btnDelete.setVisible(false);

        r3.add(btnCancel, btnCreate, btnUpdate, btnDelete);

        c2.add(frm);
        c2.add(r3);
        c2.setWidth("25%");

        return c2;
    }

    @Override
    // When anything automatic needs to happen just before the page is shown
    // Such as the loading of data
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(studentController.getAllStudents());
    }

    private void handleClickSearch(ClickEvent event) {
        if (txtNaamSearch.getValue().trim().length() == 0) {
            grid.setItems(studentController.getAllStudents());
        } else {
            grid.setItems(studentController.getStudents(txtNaamSearch.getValue().trim(), null));
        }
    }

    private void handleClickCancel(ClickEvent event) {
        grid.asSingleSelect().clear();
        resetForm();
    }

    private void handleClickCreate(ClickEvent event) {
        if (!isformValid()){
            Notification.show("Er zijn validatiefouten", 3000, Notification.Position.MIDDLE);
            return;
        }

        Date d = Date.from(datGeboorte.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        StudentDTO s = new StudentDTO(0L, txtNaam.getValue(), txtVoornaam.getValue(), d);
        try {
            long i = studentController.createStudent(s);

            Notification.show("Student created (id: " + i + ")", 3000, Notification.Position.TOP_CENTER);
            handleClickSearch(null);
            resetForm();
        } catch (EJBException e) {
            Notification.show(e.getCausedByException().getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void handleClickUpdate(ClickEvent event) {
        if (!isformValid()){
            Notification.show("Er zijn validatiefouten", 3000, Notification.Position.MIDDLE);
            return;
        }

        Date d = Date.from(datGeboorte.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        StudentDTO s = new StudentDTO(Integer.parseInt(lblID.getText()), txtNaam.getValue(), txtVoornaam.getValue(), d);
        try {
            studentController.updateStudent(s);

            Notification.show("Student aangepast", 3000, Notification.Position.TOP_CENTER);
            handleClickSearch(null);
            resetForm();
        } catch (EJBException e) {
            Notification.show(e.getCausedByException().getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void handleClickDelete(ClickEvent event) {
        try {
            studentController.deleteStudent(Integer.parseInt(lblID.getText()));

            Notification.show("Student verwijderd", 3000, Notification.Position.TOP_CENTER);
            handleClickSearch(null);
            resetForm();
        } catch (EJBException e) {
            Notification.show(e.getCausedByException().getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void populateForm(StudentDTO s) {
        btnCreate.setVisible(false);
        btnUpdate.setVisible(true);
        btnDelete.setVisible(true);

        if (s != null) {
            lblID.setText("" + s.getId());
            if (s.getVoornaam() != null) {
                txtVoornaam.setValue(s.getVoornaam());
            } else {
                txtVoornaam.setValue("");
            }

            if (s.getNaam() != null) {
                txtNaam.setValue(s.getNaam());
            } else {
                txtNaam.setValue("");
            }

            if (s.getGeboortedatum() != null) {
                try {
                    datGeboorte.setValue(s.getGeboortedatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } catch (NullPointerException e) {
                    datGeboorte.setValue(null);
                }
            } else {
                datGeboorte.setValue(null);
            }
        }
    }

    private void resetForm(){
        lblID.setText("");
        txtVoornaam.clear();
        txtVoornaam.setInvalid(false);
        txtNaam.clear();
        txtNaam.setInvalid(false);
        datGeboorte.clear();
        datGeboorte.setInvalid(false);

        btnCreate.setVisible(true);
        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);
    }

    private boolean isformValid(){
        if (txtNaam.getValue() == null || txtNaam.getValue().trim().length() == 0) {
            txtNaam.setInvalid(true);
            return false;
        }
        if (txtVoornaam.getValue() == null || txtVoornaam.getValue().trim().length() == 0) {
            txtVoornaam.setInvalid(true);
            return false;
        }
        if (datGeboorte.getValue() == null) {
            datGeboorte.setInvalid(true);
            return false;
        }
        return true;
    }
}
