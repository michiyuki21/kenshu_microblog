package com.example.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.entity.Favorite;
import com.example.demo.repository.FavoriteRepository;

@Repository
public class FavoriteDao implements BaseDao<Favorite> {
	@Autowired
	FavoriteRepository repository;
	
	@Override
	public List<Favorite> findAll() {
		return repository.findAll();
	}
	
	@Override
	public Favorite findById(Integer id) throws DataNotFoundException {
		return this.repository.findById(id).orElseThrow(() -> new DataNotFoundException());
	}
	
	@Override
	public void save(Favorite favorite) {
		this.repository.save(favorite);
	}
	
	@Override
	public void deleteById(Integer id) {
		try {
			Favorite favorite = this.findById(id);
			this.repository.deleteById(favorite.getId());
		} catch (DataNotFoundException e) {
			System.out.println("no data");
		}
	}
	
	public List<Favorite> findByUserId(Integer userId) throws DataNotFoundException {
		List<Favorite> favorites = this.repository.findByUserId(userId);
		if (favorites == null) {
			throw new DataNotFoundException();
		}
		return favorites;
	}
	
	public List<Favorite> findByTweetId(Integer tweetId) throws DataNotFoundException {
		List<Favorite> favorites = this.repository.findByTweetId(tweetId);
		if (favorites == null) {
			throw new DataNotFoundException();
		}
		return favorites;
	}
}
