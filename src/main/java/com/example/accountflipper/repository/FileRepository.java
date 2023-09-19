package com.example.accountflipper.repository;

import com.example.accountflipper.entity.InputEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<InputEntity, Long> {


}
