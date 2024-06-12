package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
	List<Follow> findByUserId(Integer userId);
	List<Follow> findByFollowingUserId(Integer followingUserId);
}
