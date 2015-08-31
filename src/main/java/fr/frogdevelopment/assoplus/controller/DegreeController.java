/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.assoplus.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import fr.frogdevelopment.assoplus.dto.LicenceDto;
import fr.frogdevelopment.assoplus.dto.OptionDto;
import fr.frogdevelopment.assoplus.dto.ReferenceDto;
import fr.frogdevelopment.assoplus.service.LicencesService;
import fr.frogdevelopment.assoplus.service.OptionsService;

import java.util.Comparator;
import java.util.function.Consumer;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DegreeController extends AbstractCustomDialogController {

    @Autowired
    private LicencesService licencesService;

    @Autowired
    private OptionsService optionsService;

    @FXML
    private TreeTableView<ReferenceDto> treeTableView;
    @FXML
    private TreeTableColumn<ReferenceDto, String> columnCode;
    @FXML
    private TreeTableColumn<ReferenceDto, String> columnLabel;
    @FXML
    private TextField tfLabel;
    @FXML
    private TextField tfCode;
    @FXML
    private Button btnRemove;

    private ObservableList<LicenceDto> licenceDtos;
    private ObservableList<OptionDto> optionDtos;
    private TreeItem<ReferenceDto> rootItem;

    @Override
    @SuppressWarnings("unchecked")
    protected void initialize() {
        initData();

        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                tfCode.textProperty().unbindBidirectional(oldValue.getValue().codeProperty());
                tfLabel.textProperty().unbindBidirectional(oldValue.getValue().labelProperty());
            }

            if (newValue != null) {
                tfCode.textProperty().bindBidirectional(newValue.getValue().codeProperty());
                tfLabel.textProperty().bindBidirectional(newValue.getValue().labelProperty());

                btnRemove.setDisable(false);
            } else {
                tfCode.setText(null);
                tfLabel.setText(null);

                btnRemove.setDisable(true);
            }
        });
    }

    private void initData() {
        licenceDtos = FXCollections.observableArrayList(licencesService.getAll());
        optionDtos = FXCollections.observableArrayList(optionsService.getAll());

        rootItem = new TreeItem<>(new LicenceDto());

        licenceDtos.forEach(licenceDto -> {
            TreeItem<ReferenceDto> treeItem = new TreeItem<>(licenceDto);
            optionDtos.stream()
                    .filter(optionDto -> optionDto.getLicenceCode().equals(licenceDto.getCode()))
                    .forEach(optionDto -> treeItem.getChildren().add(new TreeItem<>(optionDto)));
            rootItem.getChildren().add(treeItem);
        });

        rootItem.getChildren().sort(Comparator.comparing(o1 -> o1.getValue().getCode()));
        rootItem.setExpanded(true);
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);
    }

    public void onSave() {
        licencesService.saveOrUpdateAll(licenceDtos);
        optionsService.saveOrUpdateAll(optionDtos);
        initData();
    }

    public void onClose(Event event) {
        close(event);
    }

    public void onAddLicence() {
        LicenceDto licenceDto = new LicenceDto();
        licenceDtos.add(licenceDto);

        final TreeItem<ReferenceDto> newItem = new TreeItem<>(licenceDto);
        rootItem.getChildren().add(newItem);

        rootItem.setExpanded(true);
        final int rowIndex = treeTableView.getRow(newItem);

        treeTableView.edit(rowIndex, columnCode);
    }

    public void onAddOption() {
        final TreeItem<ReferenceDto> selectedItem = treeTableView.getSelectionModel().getSelectedItem();

        if (!(selectedItem.getValue() instanceof LicenceDto)) {
            return;
        }

        LicenceDto licenceDto = (LicenceDto) selectedItem.getValue();
        OptionDto optionDto = new OptionDto();
        optionDto.setLicenceCode(licenceDto.getCode());

        optionDtos.add(optionDto);

        final TreeItem<ReferenceDto> newItem = new TreeItem<>(optionDto);
        selectedItem.getChildren().add(newItem);

        selectedItem.expandedProperty().set(true);
        final int rowIndex = treeTableView.getRow(newItem);

        treeTableView.edit(rowIndex, columnCode);
    }

    public void onRemove() {
        final TreeItem<ReferenceDto> selectedItem = treeTableView.getSelectionModel().getSelectedItem();

        String message = getMessage("global.confirm.delete");
        Consumer onYes;
        if ((selectedItem.getValue() instanceof LicenceDto)) {
            message = String.format(message, "un Diplôme"); // fixme
            onYes = o -> removeLicence(selectedItem);
        } else if ((selectedItem.getValue() instanceof OptionDto)) {
            message = String.format(message, "une Option"); // fixme
            onYes = o -> removeOption(selectedItem);
        } else {
            return;
        }

        showYesNoDialog(message, onYes);
    }

    private void removeLicence(final TreeItem<ReferenceDto> selectedItem) {
        rootItem.getChildren().remove(selectedItem);

        LicenceDto licenceDto = (LicenceDto) selectedItem.getValue();
        licenceDtos.remove(licenceDto);

        if (licenceDto.getId() != 0) {
            licencesService.deleteLicence(licenceDto);
        }
    }

    private void removeOption(final TreeItem<ReferenceDto> selectedItem) {
        TreeItem<ReferenceDto> parent = selectedItem.getParent();
        parent.getChildren().remove(selectedItem);

        OptionDto optionDto = (OptionDto) selectedItem.getValue();
        optionDtos.remove(optionDto);

        if (optionDto.getId() != 0) {
            optionsService.deleteOption(optionDto);
        }
    }
}
