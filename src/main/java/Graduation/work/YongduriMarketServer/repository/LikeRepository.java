package Graduation.work.YongduriMarketServer.repository;

import Graduation.work.YongduriMarketServer.domain.Board;
import Graduation.work.YongduriMarketServer.domain.BoardLike;
import Graduation.work.YongduriMarketServer.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
@Transactional
public interface LikeRepository  extends JpaRepository<BoardLike, Long> {
     Optional<BoardLike> findByBoardAndUser(Board board, User user);


}
