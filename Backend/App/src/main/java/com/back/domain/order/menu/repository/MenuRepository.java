package com.back.domain.order.menu.repository;


import com.back.domain.order.menu.entity.Menu;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    int deleteByIdAndCustomer_Email(Long menuId, @NotBlank @Email String email);
}
