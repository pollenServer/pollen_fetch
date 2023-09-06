package pollen.pollen_fetch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pollen.pollen_fetch.domain.Oak;

public interface OakRepository extends JpaRepository<Oak, Long> {
}
