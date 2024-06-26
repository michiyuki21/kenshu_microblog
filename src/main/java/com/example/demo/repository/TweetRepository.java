package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Tweet;

public interface TweetRepository extends JpaRepository<Tweet, Integer>{
	List<Tweet> findByUserIdOrderByCreatedAtDesc(Integer userId);
	
	@Query(value = "SELECT t.* FROM tweet t INNER JOIN favorite f " + 
			"ON t.id = f.tweet_id " + 
			"WHERE f.user_id = :userId " + 
			"ORDER BY created_at DESC", nativeQuery = true)
	List<Tweet> favoriteTweetsFindByUserId(@Param("userId") Integer userid);
	
	@Query(value = "SELECT * FROM tweet " + 
			"WHERE user_id = :userId OR user_id = " + 
			"(SELECT following_user_id FROM follow " + 
			"WHERE user_id = :userId) " + 
			"ORDER BY created_at DESC", nativeQuery = true)
	List<Tweet> timelineTweetsFindByUserId(@Param("userId") Integer userId);
}
