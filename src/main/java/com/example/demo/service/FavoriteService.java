package com.example.demo.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.dao.FavoriteDao;
import com.example.demo.entity.Favorite;

@Service
public class FavoriteService implements BaseService<Favorite> {
	@Autowired
	private FavoriteDao dao;
	
	@Override
	public List<Favorite> findAll() {
		return dao.findAll();
	}
	
	@Override
	public Favorite findById(Integer id) throws DataNotFoundException {
		return dao.findById(id);
	}
	
	@Override
	public void save(Favorite favorite) {
		dao.save(favorite);
	}
	
	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}
	
	public List<Favorite> findByUserId(Integer userId) {
		return dao.findByUserId(userId);
	}
	
	public List<Favorite> findByTweetId(Integer tweetId) {
		return dao.findByTweetId(tweetId);
	}
	
	public Set<Integer> favoriteTweetIdsSetFindByUserId(Integer userId) {
		return dao.favoriteTweetIdsSetFindByUserId(userId);
	}
}
