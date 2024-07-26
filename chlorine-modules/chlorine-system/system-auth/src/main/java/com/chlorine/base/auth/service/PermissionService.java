package com.chlorine.base.auth.service;

import com.chlorine.base.auth.entity.Permission;
import com.chlorine.base.auth.repository.PermissionRepository;
import com.chlorine.base.mvc.dto.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {
    @Autowired
    PermissionRepository permissionRepository;

    public CommonResult page(int pageNumber, int pageSize, String contain, Integer menuId) {
        return CommonResult.success(permissionRepository.findAll((Specification<Permission>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!contain.isEmpty()) {
                predicateList.add(criteriaBuilder.like(root.get("name"), "%" + contain + "%"));
            }
//            if (menuId != null) {
//                predicateList.add(criteriaBuilder.equal(root.get("menu"), menuId));
//            }
            return criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()])).getRestriction();
        }, PageRequest.of(pageNumber - 1, pageSize)));
    }

    public CommonResult save(Permission permission) {
        return CommonResult.success(permissionRepository.save(permission));
    }

    public CommonResult list() {
        return CommonResult.success(permissionRepository.findAll());
    }

    public boolean isExist(String path) {
        Specification<Permission> specification = new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("path"), path);
            }
        };
        Optional<Permission> permission = permissionRepository.findOne(specification);
        return permission.isPresent();
    }
}
