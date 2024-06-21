package com.chlorine.base.mvc.repository;

import com.chlorine.base.mvc.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BaseRepository<Entity extends BaseEntity> extends JpaRepository<Entity,Long>, JpaSpecificationExecutor<Entity> {
}
