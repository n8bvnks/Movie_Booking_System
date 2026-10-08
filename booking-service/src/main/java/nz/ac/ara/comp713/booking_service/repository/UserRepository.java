package nz.ac.ara.comp713.booking_service.repository;

import nz.ac.ara.comp713.booking_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}