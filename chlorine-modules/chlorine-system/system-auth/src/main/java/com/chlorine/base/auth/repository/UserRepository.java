package com.chlorine.base.auth.repository;

import com.chlorine.base.auth.entity.User;
import com.chlorine.base.mvc.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
*
* @author chenlong
* @date 2020/12/7
*/
public interface UserRepository extends BaseRepository<User> {
}
