package com.team9.taads.repository;

import com.team9.taads.entity.PDFEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PDFRepository extends JpaRepository<PDFEntity, Long> {


}
