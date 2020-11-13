package com.roshik.repositories;

import com.roshik.domains.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PeriodRepository extends JpaRepository<Period,Long> {

    @Query("SELECT p FROM Period p WHERE p.start_date=?1 and p.end_date = ?2")
    Period getPeriodByStart_dateAndEnd_date(LocalDate start_date,LocalDate end_date);

    @Query("SELECT count(p) > 0 FROM Period p WHERE p.start_date=?1 and p.end_date = ?2")
    boolean isExistsStart_dateAndEnd_date(LocalDate start_date,LocalDate end_date);
}
