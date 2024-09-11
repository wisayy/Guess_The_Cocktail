package com.ridango.game.repository;

import com.ridango.game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameRepository extends JpaRepository<Player, Long> {
}