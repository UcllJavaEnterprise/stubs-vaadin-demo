package be.ucll.java.ent.view;

import be.ucll.java.ent.controller.StudentEJBLocal;
import be.ucll.java.ent.domain.StudentDTO;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import java.util.ArrayList;
import java.util.Date;

@Route("student")
@PageTitle("StuBS")
public class StudentView extends VerticalLayout implements AfterNavigationObserver {
    @EJB
    private StudentEJBLocal studentController;

    private SplitLayout splitLayout;
    private VerticalLayout c1;
    private VerticalLayout c2;
    private FormLayout frm;
    private HorizontalLayout hl;

    private Grid<StudentDTO> grid;

    private Label lblID;
    private TextField txtNaam;
    private Button btnCancel;
    private Button btnCreate;
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

        grid = new Grid<>();
        grid.setItems(new ArrayList<StudentDTO>(0));
        // grid.addColumn(student -> student.getNaam()).setHeader("Naam").setSortable(true);
        grid.addColumn(StudentDTO::getNaam).setHeader("Naam").setSortable(true);
        grid.setHeightFull();

        //when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        c1.add(grid);
        c1.setWidth("75%");
        return c1;
    }

    private Component createEditorLayout() {
        c2 = new VerticalLayout();

        lblID = new Label("");

        txtNaam = new TextField();
        txtNaam.setRequired(true);
        txtNaam.setMaxLength(128);
        txtNaam.setErrorMessage("Verplicht veld!");

        frm = new FormLayout();
        frm.addFormItem(txtNaam, "Naam");

        hl = new HorizontalLayout();
        hl.setWidthFull();
        hl.setSpacing(true);

        btnCancel = new Button("Annuleren");
        btnCancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCancel.addClickListener(e -> handleClickCancel(e));

        btnCreate = new Button("Toevoegen");
        btnCreate.addClickListener(e -> handleClickCreate(e));

        btnDelete = new Button("Verwijderen");
        btnDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnDelete.addClickListener(e -> handleClickDelete(e));
        btnDelete.setVisible(false);

        hl.add(btnCancel, btnCreate, btnDelete);

        c2.add(frm);
        c2.add(hl);
        c2.setWidth("25%");

        return c2;
    }

    @Override
    // When anything automatic needs to happen just before the page is shown
    // Such as the loading of data
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(studentController.getAllStudents());
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

        StudentDTO s = new StudentDTO(0L, txtNaam.getValue(), "ToBeCompleted", new Date());
        try {
            long i = studentController.createStudent(s);

            Notification.show("Student created (id: " + i + ")", 3000, Notification.Position.TOP_CENTER);
            grid.setItems(studentController.getAllStudents());
            resetForm();
        } catch (EJBException e) {
            Notification.show(e.getCausedByException().getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void handleClickDelete(ClickEvent event) {
        try {
            studentController.deleteStudent(Integer.parseInt(lblID.getText()));

            Notification.show("Student verwijderd", 3000, Notification.Position.TOP_CENTER);
            grid.setItems(studentController.getAllStudents());
            resetForm();
        } catch (EJBException e) {
            Notification.show(e.getCausedByException().getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void populateForm(StudentDTO s) {
        btnCreate.setVisible(false);
        btnDelete.setVisible(true);

        if (s != null) {
            lblID.setText("" + s.getId());

            if (s.getNaam() != null) {
                txtNaam.setValue(s.getNaam());
            } else {
                txtNaam.setValue("");
            }
        }
    }

    private void resetForm(){
        lblID.setText("");
        txtNaam.clear();
        txtNaam.setInvalid(false);

        btnCreate.setVisible(true);
        btnDelete.setVisible(false);
    }

    private boolean isformValid(){
        if (txtNaam.getValue() == null || txtNaam.getValue().trim().length() == 0) {
            txtNaam.setInvalid(true);
            return false;
        }

        return true;
    }
}
