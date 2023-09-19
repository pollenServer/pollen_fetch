package pollen.pollen_fetch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pollen.pollen_fetch.domain.Weeds;

public interface WeedsRepository extends JpaRepository<Weeds, String> {
}
