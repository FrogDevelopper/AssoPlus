/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.assoplus.service;

import fr.frogdevelopment.assoplus.bean.Bean;
import fr.frogdevelopment.assoplus.dao.CommonDao;
import fr.frogdevelopment.assoplus.dto.Dto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

abstract class AbstractService<B extends Bean, D extends Dto> implements fr.frogdevelopment.assoplus.service.Service<D> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

	@Autowired
	protected CommonDao<B> dao;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public ObservableList<D> getAllData() {
		ObservableList<D> data = FXCollections.observableArrayList();

		List<B> entities = dao.getAll();

		entities.forEach(member -> data.add(createDTO(member)));

		return data;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public D saveData(D dto) {
		B bean = createBean(dto);
		dao.save(bean);
		dto.setId(bean.getId());

		return dto;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public D updateData(D dto) {
		B bean = createBean(dto);
		dao.update(bean);
		dto.setId(bean.getId());

		return dto;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void deleteData(D dto) {
		dao.delete(createBean(dto));
	}

	abstract protected D createDTO(B entity);

	abstract protected B createBean(D dto);

}
