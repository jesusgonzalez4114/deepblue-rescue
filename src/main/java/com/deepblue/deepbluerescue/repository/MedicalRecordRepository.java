package com.deepblue.deepbluerescue.repository;

import com.deepblue.deepbluerescue.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
}