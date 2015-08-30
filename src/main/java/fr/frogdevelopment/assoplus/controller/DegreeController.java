/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.assoplus.controller;

import fr.frogdevelopment.assoplus.dto.LicenceDto;
import fr.frogdevelopment.assoplus.dto.OptionDto;
import fr.frogdevelopment.assoplus.dto.ReferenceDto;
import fr.frogdevelopment.assoplus.service.LicencesService;
import fr.frogdevelopment.assoplus.service.OptionsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Comparator;

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

	private ObservableList<LicenceDto> licenceDtos;
	private ObservableList<OptionDto> optionDtos;
	private TreeItem<ReferenceDto> rootItem;

	@Override
	@SuppressWarnings("unchecked")
	protected void initialize() {
		initData();

		columnCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
		columnCode.setCellFactory(p -> new TextFieldTreeTableCell(new StringConverter<String>() {
			@Override
			public String toString(String object) {
				return object;
			}

			@Override
			public String fromString(String string) {
				return string;
			}
		}));

		columnLabel.setCellValueFactory(new TreeItemPropertyValueFactory<>("label"));
		columnLabel.setCellFactory(p -> new TextFieldTreeTableCell(new StringConverter<String>() {
			@Override
			public String toString(String object) {
				return object;
			}

			@Override
			public String fromString(String string) {
				return string;
			}
		}));
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
		treeTableView.setEditable(true);
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

	public void onRemoveLicence() {
		final TreeItem<ReferenceDto> selectedItem = treeTableView.getSelectionModel().getSelectedItem();

		if (!(selectedItem.getValue() instanceof LicenceDto)) {
			return;
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setHeaderText("ATTENTION");
		dialog.setContentText("Vous allez supprimer un Diplôme, voulez-vous continuer ?");
		dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);

		dialog.showAndWait()
				.filter(response -> response == ButtonType.YES)
				.ifPresent(response -> removeLicence(selectedItem));
	}

	private void removeLicence(final TreeItem<ReferenceDto> selectedItem) {
		rootItem.getChildren().remove(selectedItem);

		LicenceDto licenceDto = (LicenceDto) selectedItem.getValue();
		licenceDtos.remove(licenceDto);

		if (licenceDto.getId() != 0) {
			licencesService.deleteLicence(licenceDto);
		}
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

	public void onRemoveOption() {
		final TreeItem<ReferenceDto> selectedItem = treeTableView.getSelectionModel().getSelectedItem();

		if (!(selectedItem.getValue() instanceof OptionDto)) {
			return;
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setHeaderText("ATTENTION");
		dialog.setContentText("Vous allez supprimer une Option, voulez-vous continuer ?");
		dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);

		dialog.showAndWait()
				.filter(response -> response == ButtonType.YES)
				.ifPresent(response -> removeOption(selectedItem));
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