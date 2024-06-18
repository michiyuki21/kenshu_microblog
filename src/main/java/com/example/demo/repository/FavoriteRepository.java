package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
	List<Favorite> findByFavoUserId(Integer userId);
	List<Favorite> findByTweetId(Integer tweetId);
	
	// わざわざID出さなくても一対多関係からユーザ照合すればよくね？
	// クエリ作るんだったらinsertとかでお気に入り登録してるかの照合用のカラム追加したりとか
	@Query(value = "SELECT tweet_id FROM favorite " + 
			"WHERE user_id = :userId", nativeQuery = true)
	List<Integer> favoriteTweetIdsSetFindByUserId(@Param("userId") Integer userId);
}
