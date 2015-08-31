/*
 * Copyright (c) Frog Development 2015.
 */

package fr.frogdevelopment.assoplus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.frogdevelopment.assoplus.dto.OptionDto;
import fr.frogdevelopment.assoplus.entities.Option;

@Service("optionsService")
public class OptionsServiceImpl extends AbstractService<Option, OptionDto> implements OptionsService {

    OptionDto createDto(Option bean) {
        OptionDto dto = new OptionDto();
        dto.setId(bean.getId());
        dto.setCode(bean.getCode());
        dto.setLabel(bean.getLabel());
        dto.setDegreeCode(bean.getLicenceCode());

        return dto;
    }

    Option createBean(OptionDto dto) {
        Option bean = new Option();
        bean.setId(dto.getId());
        bean.setCode(dto.getCode());
        bean.setLabel(dto.getLabel());
        bean.setLicenceCode(dto.getDegreeCode());

        return bean;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteOption(OptionDto optionDto) {
        dao.delete(createBean(optionDto));
    }

}
