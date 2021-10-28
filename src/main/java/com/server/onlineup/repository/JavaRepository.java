package com.server.onlineup.repository;

import com.server.onlineup.model.entity.JavaObj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface JavaRepository extends JpaRepository<JavaObj, Long> {
    @Query(
            value = "select * from test_obj_2 where id < 15",
            nativeQuery = true)
    Collection<JavaObj> findCustom();

    @Query(
            value = "select * from test_obj_2 where id < 15",
            nativeQuery = true)
    Collection<JavaObj> findA();
}