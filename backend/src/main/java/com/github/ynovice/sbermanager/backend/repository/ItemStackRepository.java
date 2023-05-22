package com.github.ynovice.sbermanager.backend.repository;

import com.github.ynovice.sbermanager.backend.model.ItemStack;
import com.github.ynovice.sbermanager.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemStackRepository extends JpaRepository<ItemStack, Long> {

    List<ItemStack> findAllByActiveAndOwner(boolean active, User owner);

    Optional<ItemStack> findByIdAndOwner(Long id, User owner);

    List<ItemStack> findAllByOwner(User owner);
}
