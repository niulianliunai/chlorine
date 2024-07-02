package com.chlorine.base.auth.service;

import com.chlorine.base.auth.entity.Role;
import com.chlorine.base.auth.repository.RoleRepository;
import com.chlorine.base.mvc.dto.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public CommonResult page(int pageNumber, int pageSize, String contain) {
        return CommonResult.success(roleRepository.findAll((Specification<Role>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (!StringUtils.hasLength(contain)) {
                predicateList.add(criteriaBuilder.like(root.get("name"), "%" + contain + "%"));
            }
            return criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()])).getRestriction();
        }, PageRequest.of(pageNumber - 1, pageSize)));
    }
    public CommonResult list() {
        return CommonResult.success(roleRepository.findAll());
    }
    public CommonResult save(Role role) {
        return CommonResult.success(roleRepository.save(role));
    }
}
