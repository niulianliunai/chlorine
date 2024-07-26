package com.chlorine.base.mvc.service;

import com.chlorine.base.mvc.entity.TreeEntity;
import com.chlorine.base.mvc.repository.BaseRepository;
import com.chlorine.base.mvc.dto.CommonResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public abstract class TreeService<Entity extends TreeEntity<Entity>>
        extends BaseService<Entity> {

    private final BaseRepository<Entity> repository;
    protected TreeService(BaseRepository<Entity> repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Integer saveAll(List<Entity> entities) {
        entities.forEach(entity -> {
            if (entity.getDeleted()) {
                deleteChildren(entity);
            }
        });
        return super.saveAll(entities);
    }
    private void deleteChildren(Entity entity) {
        if (entity == null) {
            return;
        }
        entity.setDeleted(true);
        if (entity.getChildren() == null) {
            return;
        }
        entity.getChildren().forEach(this::deleteChildren);
    }

    public List<Entity> listTree() {
        Specification<Entity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.where(
                    criteriaBuilder.isNull(root.get("parent"))
            );
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("sort")));
            return criteriaQuery.getRestriction();
        };
        return repository.findAll(specification);
    }



}
