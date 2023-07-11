package com.example.junit.service.mapper;

import com.example.junit.domain.Grade;
import com.example.junit.web.dto.GradeDto;
import org.springframework.stereotype.Component;

@Component
class GradeDtoMapper implements Mapper<Grade, GradeDto> {

    @Override
    public GradeDto map(Grade entity) {
        return new GradeDto(entity.getId(),
                            entity.getValue(),
                            entity.getDiscipline());
    }
}
