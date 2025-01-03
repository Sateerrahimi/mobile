package org.example.mobile.repository;

import org.example.mobile.Mobile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MobileRepository extends JpaRepository<Mobile, Long> {
    // Custom query methods can go here if needed
}
