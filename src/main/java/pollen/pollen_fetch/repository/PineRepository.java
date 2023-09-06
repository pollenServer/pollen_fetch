package pollen.pollen_fetch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pollen.pollen_fetch.domain.Pine;

public interface PineRepository extends JpaRepository<Pine, Long> {
}
