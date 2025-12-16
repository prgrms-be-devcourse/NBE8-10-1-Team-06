package com.back.domain.order.menu.repository;


import com.back.domain.order.menu.entity.menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<menu, Long> {
}
