package com.example.application.views.fitnessdetail;

import com.example.application.data.FitnessDetail;
import com.example.application.services.FitnessDetailService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Fitness-Detail")
@Route(value = "/:fitnessDetailID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class FitnessDetailView extends Div implements BeforeEnterObserver {

    private final String FITNESSDETAIL_ID = "fitnessDetailID";
    private final String FITNESSDETAIL_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<FitnessDetail> grid = new Grid<>(FitnessDetail.class, false);

    private DatePicker dates;
    private TextField moves;
    private TextField exercise;
    private TextField stand;
    private TextField steps;
    private TextField calories;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<FitnessDetail> binder;

    private FitnessDetail fitnessDetail;

    private final FitnessDetailService fitnessDetailService;

    public FitnessDetailView(FitnessDetailService fitnessDetailService) {
        this.fitnessDetailService = fitnessDetailService;
        addClassNames("fitness-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("dates").setAutoWidth(true);
        grid.addColumn("moves").setAutoWidth(true);
        grid.addColumn("exercise").setAutoWidth(true);
        grid.addColumn("stand").setAutoWidth(true);
        grid.addColumn("steps").setAutoWidth(true);
        grid.addColumn("calories").setAutoWidth(true);
        grid.setItems(query -> fitnessDetailService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FITNESSDETAIL_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FitnessDetailView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(FitnessDetail.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(moves).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("moves");
        binder.forField(exercise).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("exercise");
        binder.forField(stand).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("stand");
        binder.forField(steps).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("steps");
        binder.forField(calories).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("calories");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.fitnessDetail == null) {
                    this.fitnessDetail = new FitnessDetail();
                }
                binder.writeBean(this.fitnessDetail);
                fitnessDetailService.update(this.fitnessDetail);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(FitnessDetailView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> fitnessDetailId = event.getRouteParameters().get(FITNESSDETAIL_ID).map(Long::parseLong);
        if (fitnessDetailId.isPresent()) {
            Optional<FitnessDetail> fitnessDetailFromBackend = fitnessDetailService.get(fitnessDetailId.get());
            if (fitnessDetailFromBackend.isPresent()) {
                populateForm(fitnessDetailFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested fitnessDetail was not found, ID = %s", fitnessDetailId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FitnessDetailView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        dates = new DatePicker("Dates");
        moves = new TextField("Moves");
        exercise = new TextField("Exercise");
        stand = new TextField("Stand");
        steps = new TextField("Steps");
        calories = new TextField("Calories");
        formLayout.add(dates, moves, exercise, stand, steps, calories);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(FitnessDetail value) {
        this.fitnessDetail = value;
        binder.readBean(this.fitnessDetail);

    }
}
