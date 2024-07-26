package com.chlorine.base.mvc.service;

import com.chlorine.base.mvc.util.FieldUtil;
import com.chlorine.base.mvc.entity.BaseEntity;
import com.chlorine.base.mvc.repository.BaseRepository;
import com.chlorine.base.util.UpdateUtil;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

public abstract class  BaseService<Entity extends BaseEntity> {
    protected final BaseRepository<Entity> repository;

    protected BaseService(BaseRepository<Entity> repository) {
        this.repository = repository;
    }

    public Entity saveIfNotExists(Entity entity) {
        Optional<Entity> exist = findByEntity(entity);
        return exist.orElseGet(() -> repository.save(entity));
    }
    @Transactional
    public Entity save(Entity entity) {
        if (entity.getId() != null) {
            try {
                Optional<Entity> optionalEntity = repository.findById(entity.getId());
                if (optionalEntity.isPresent()) {
                    Entity target = optionalEntity.get();
                    // 使用乐观锁，假设Entity类有个version字段
//                    if (entity.getVersion() != null && !entity.getVersion().equals(target.getVersion())) {
//                        throw new OptimisticLockingFailureException("Entity was updated by another transaction");
//                    }
                    UpdateUtil.copyNullProperties(entity, target);
//                    target.setVersion(target.getVersion() + 1); // 增加版本号
                    return repository.save(target);
                } else {
                    throw new EntityNotFoundException("Entity not found with id " + entity.getId());
                }
            } catch (NoSuchElementException e) {
                throw new EntityNotFoundException("Entity not found with id " + entity.getId());
            }
        }
        return repository.save(entity);
    }
    public List<Entity> findByField(String field, String value) {
        return repository.findAll((Specification<Entity>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get(field), value));
            return criteriaQuery.where(predicateList.toArray(new Predicate[0])).getRestriction();
        });
    }

    public List<Entity> findAll(Specification<Entity> specification) {
        return repository.findAll(specification);
    }

    public List<Entity> findAll() {
        return repository.findAll();
    }

    public List<Entity> findAll(Entity entity) {
        return repository.findAll(buildSpecification(entity));
    }

    public Integer saveAllPartition(List<Entity> entities, Integer batchSize) {
        int res = 0;
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = i + batchSize;
            if (end > entities.size()) {
                end = entities.size();
            }
            res += saveAll(entities.subList(i, end));
        }
        return res;

    }
    public Integer saveAll(List<Entity> entities) {
        return (int) entities.stream().map(this::save).filter(Objects::nonNull).count();
    }


    public void logicDelete(Long id) {
        Optional<Entity> exist = repository.findById(id);
        if(exist.isPresent()) {
            Entity entity = exist.get();
            entity.setDeleted(true);
            repository.save(entity);
        }
    }
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void delete(Entity entity) {
        Optional<Entity> o = findByEntity(entity);
        o.ifPresent(value -> deleteById(value.getId()));
    }


    public List<Entity> list() {
        return repository.findAll((Specification<Entity>) (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("deleted"),true));
    }


    public Page<Entity> page(Entity entity) {
        return repository.findAll(buildSpecification(entity), PageRequest.of(entity.getPageNumber() - 1, entity.getPageSize()));
    }

    public Specification<Entity> buildSpecification(Entity entity) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            List<Order> orderList = new ArrayList<>();
            handleEntityParam(predicateList, orderList, criteriaBuilder, root, entity);
            predicateList.add(criteriaBuilder.notEqual(root.get("deleted"),true));
            if (orderList.isEmpty()) {
                orderList.add(criteriaBuilder.desc(root.get("id")));
            }
            return criteriaQuery.orderBy(orderList.toArray(new Order[0])).where(predicateList.toArray(new Predicate[0])).getRestriction();
        };
    }
    public void handleEntityParam(List<Predicate> predicateList, List<Order> orderList, CriteriaBuilder criteriaBuilder, Path root, Object entity) {
        List<Map<String, Object>> fieldList = FieldUtil.getFiledsInfo(entity);
        fieldList.stream().filter(field -> field.get("value") != null).forEach(field -> {
            String name = field.get("name").toString();
            Object value = field.get("value");
            Class<?> type = (Class<?>) field.get("type");
            if (!Collection.class.isAssignableFrom(type) && !type.cast(value).toString().isEmpty()) {
                if (BaseEntity.class.isAssignableFrom(type)) {
                    handleEntityParam(predicateList, orderList, criteriaBuilder, root.get(name), value);
                } else if (name.equals("params")) {
                    ((Map<String, Object>) value).forEach((key, value1) -> {
                        if (value1 != null && !String.valueOf(value1).isEmpty()) {
                            if (key.endsWith("Contains")) {
                                predicateList.add(criteriaBuilder.like(root.get(key.replace("Contains", "")), "%" + value1 + "%"));
                            } else if (key.endsWith("In")) {
                                List<Predicate> orConditions = new ArrayList<>();
                                String valueStr = String.valueOf(value1);

                                if (valueStr.contains(",")) {
                                    String[] split = valueStr.split(",");

                                    for (String s : split) {
                                        // 注意这里我们没有再次使用criteriaBuilder.or(...)
                                        orConditions.add(criteriaBuilder.like(criteriaBuilder.concat(criteriaBuilder.concat(",", root.get(key.replace("In", ""))), ","), ",%" + s + ",%"));
                                    }
                                } else {
                                    orConditions.add(criteriaBuilder.like(criteriaBuilder.concat(criteriaBuilder.concat(",", root.get(key.replace("In", ""))), ","), ",%" + valueStr + ",%"));
                                }

                                // 使用criteriaBuilder.or(...)将所有orConditions组合成一个单一的Predicate
                                Predicate orPredicate = criteriaBuilder.or(orConditions.toArray(new Predicate[0]));

                                // 将这个OR Predicate添加到predicateList中
                                predicateList.add(orPredicate);
                            } else if (key.endsWith("And")) {
                                String valueStr = String.valueOf(value1);
                                if (valueStr.contains(",")) {
                                    String[] split = valueStr.split(",");
                                    for (String s : split) {
                                        predicateList.add(criteriaBuilder.like(criteriaBuilder.concat(criteriaBuilder.concat(",", root.get(key.replace("And", ""))), ","), "%," + s + ",%"));
                                    }
                                } else {
                                    predicateList.add(criteriaBuilder.like(criteriaBuilder.concat(criteriaBuilder.concat(",", root.get(key.replace("And", ""))), ","), "%," + valueStr + ",%"));
                                }

                            } else if (key.endsWith("Equals")) {
                                predicateList.add(criteriaBuilder.equal(root.get(key.replace("Equals", "")), value1));
                            } else if (key.endsWith("GreaterThan")) {
                                if (key.contains("Time")) {
                                    predicateList.add(criteriaBuilder.greaterThan(root.get(key.replace("GreaterThan", "")), LocalDateTime.parse(String.valueOf(value1))));
                                } else {
                                    predicateList.add(criteriaBuilder.greaterThan(root.get(key.replace("GreaterThan", "")), String.valueOf(value1)));
                                }
                            } else if (key.endsWith("LessThan")) {
                                if (key.contains("Time")) {
                                    predicateList.add(criteriaBuilder.lessThan(root.get(key.replace("LessThan", "")), LocalDateTime.parse(String.valueOf(value1))));
                                } else {
                                    predicateList.add(criteriaBuilder.lessThan(root.get(key.replace("LessThan", "")), String.valueOf(value1)));
                                }
                            } else if (key.endsWith("Sort")) {
                                if (String.valueOf(value1).contains("desc")) {
                                    orderList.add(criteriaBuilder.desc(root.get(key.replace("Sort", ""))));
                                } else if (String.valueOf(value1).contains("asc")) {
                                    orderList.add(criteriaBuilder.asc(root.get(key.replace("Sort", ""))));
                                }
                            }
                        }
                    });
                }
            }
        });
    }
    public Optional<Entity> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Entity> findByEntity(Entity entity) {
        return repository.findOne((Specification<Entity>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            List<Map<String, Object>> fieldList = FieldUtil.getFiledsInfo(entity);
            fieldList.stream().filter(field -> field.get("value") != null && !field.get("name").equals("createTime") && !field.get("name").equals("pageSize") && !field.get("name").equals("pageNumber") && !field.get("name").equals("params")).forEach(field -> {
                String name = field.get("name").toString();
                Object value = field.get("value");
                Class<?> type = (Class<?>) field.get("type");
                if (!Collection.class.isAssignableFrom(type) && !type.cast(value).toString().isEmpty()) {
                    predicateList.add(criteriaBuilder.equal(root.get(name), type.cast(value)));
                }

            });
            return criteriaQuery.where(predicateList.toArray(new Predicate[0])).getRestriction();
        });
    }
    public List<Entity> findAllByEntity(Entity entity) {
        return repository.findAll((Specification<Entity>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            List<Map<String, Object>> fieldList = FieldUtil.getFiledsInfo(entity);
            fieldList.stream().filter(field -> field.get("value") != null && !field.get("name").equals("createTime") && !field.get("name").equals("pageSize") && !field.get("name").equals("pageNumber") && !field.get("name").equals("params")).forEach(field -> {
                String name = field.get("name").toString();
                Object value = field.get("value");
                Class<?> type = (Class<?>) field.get("type");
                if (!Collection.class.isAssignableFrom(type) && !type.cast(value).toString().isEmpty()) {
                    predicateList.add(criteriaBuilder.equal(root.get(name), type.cast(value)));
                }

            });
            return criteriaQuery.where(predicateList.toArray(new Predicate[0])).getRestriction();
        });
    }
}
