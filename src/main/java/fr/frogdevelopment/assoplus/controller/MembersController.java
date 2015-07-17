/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.assoplus.controller;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import fr.frogdevelopment.assoplus.dto.MemberDto;
import fr.frogdevelopment.assoplus.service.MembersService;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Controller("membersController")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MembersController implements Initializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(MembersController.class);

	private ResourceBundle bundle;

	@Autowired
	private MembersService membersService;

	@FXML
	private VBox vbTop;
	@FXML
	private Button btnShowHide;

	@FXML
	private VBox vbLeft;

	@FXML
	private TextField txtStudentNumber;
	@FXML
	private TextField txtLastname;
	@FXML
	private TextField txtFirstname;
	@FXML
	private TextField txtBirthday;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtLicence;
	@FXML
	private TextField txtOption;
	@FXML
	private TextField txtPhone;
	@FXML
	private TextField txtAddress;
	@FXML
	private TextField txtPostalCode;
	@FXML
	private TextField txtCity;

	@FXML
	private TableView<MemberDto> table;

	private ObservableList<MemberDto> data;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		try {
			data = membersService.getAllData();
			table.setItems(data);
		} catch (Exception e) {
			LOGGER.error("FIXME", e);
		}
	}

	public void saveData() {
		MemberDto member = new MemberDto();
		member.setStudentNumber(Integer.parseInt(txtStudentNumber.getText()));
		member.setLastname(txtLastname.getText());
		member.setFirstname(txtFirstname.getText());
		member.setBirthday(txtBirthday.getText());
		member.setEmail(txtEmail.getText());
		member.setLicence(txtLicence.getText());
		member.setOption(txtOption.getText());
		member.setPhone(txtPhone.getText());
		member.setAddress(txtAddress.getText());
		member.setPostalCode(txtPostalCode.getText());
		member.setCity(txtCity.getText());

		membersService.saveData(member);

		data.add(member);
	}

	public void importMembers() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Importer des adh�rents");
		File file = fileChooser.showOpenDialog(vbLeft.getScene().getWindow());

		if (file != null) {
			membersService.importMembers(file);
		}
	}


	public void showHideMember() {
		final boolean isVisible = vbTop.isVisible();
		vbTop.setManaged(!isVisible);
		vbTop.setVisible(!isVisible);

		btnShowHide.setText(isVisible ? "Montrer" : "Cacher");
	}
}