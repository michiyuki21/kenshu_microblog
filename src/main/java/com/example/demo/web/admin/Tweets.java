package com.example.demo.web.admin;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.common.FlashData;
import com.example.demo.entity.Favorite;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.TweetService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/admin/tweets")
public class Tweets {
	@Autowired
	TweetService tweetService;
	@Autowired
	UserService userService;
	@Autowired
	FavoriteService favoriteService;
	
	/*
	 * 個別ユーザつぶやき一覧表示
	 */
	@GetMapping("/listOfUser/{id}")
	public String listOfUser(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		int userId;
		User targetUser;
		List<Tweet> tweets;
		try {
			user = userService.findByEmail(email);
			// パスワードは表示しないので、空にする
			user.setPassword("");
			userId = user.getId();
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			return "redirect:/users/login";
		}
		try {
			targetUser = userService.findById(id);
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("対象のユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/timeline";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			return "redirect:/admin/timeline";
		}
		tweets = tweetService.findByUserId(targetUser.getId());
		
		Set<Integer> favoriteTweetIdsSet;
		favoriteTweetIdsSet = favoriteService.favoriteTweetIdsSetFindByUserId(userId);
		
		model.addAttribute("user", user);
		model.addAttribute("targetUser", targetUser);
		model.addAttribute("tweets", tweets);
		model.addAttribute("favoriteTweetIdsSet", favoriteTweetIdsSet);
		return "admin/tweets/listOfUser";
	}
	
	/*
	 * 個別つぶやき表示
	 */
	@GetMapping("/individual/{id}")
	public String individual(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		Tweet tweet;
		try {
			user = userService.findByEmail(email);
			user.setPassword("");
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		}
		try {
			tweet = tweetService.findById(id);
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("対象のつぶやきがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/timeline";
		}
		Set<Integer> favoriteTweetIdsSet;
		favoriteTweetIdsSet = favoriteService.favoriteTweetIdsSetFindByUserId(user.getId());
		
		model.addAttribute("user", user);
		model.addAttribute("tweet", tweet);
		model.addAttribute("favoriteTweetIdsSet", favoriteTweetIdsSet);
		return "admin/tweets/individual";
	}
	
	/*
	 * お気に入りつぶやき一覧表示
	 */
	@GetMapping("/listOfFavorite")
	public String listOfFavorte(Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		List<Tweet> tweets;
		try {
			user = userService.findByEmail(email);
			user.setPassword("");
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		}
		tweets = tweetService.favoriteTweetsFindByUserId(user.getId());
		
		Set<Integer> favoriteTweetIdsSet;
		favoriteTweetIdsSet = favoriteService.favoriteTweetIdsSetFindByUserId(user.getId());
		
		model.addAttribute("user", user);
		model.addAttribute("tweets", tweets);
		model.addAttribute("favoriteTweetIdsSet", favoriteTweetIdsSet);
		return "admin/tweets/listOfFavorite";
	}
	
	/*
	 * お気に入り登録処理
	 */
	@GetMapping("/favorite/{id}")
	public String favorite(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		Tweet tweet;
		try {
			user = userService.findByEmail(email);
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		}
		try {
			tweet = tweetService.findById(id);
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("対象のつぶやきがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/tweets/listOfFavorite";
		}
		try {
			Favorite favorite = new Favorite();
			favorite.setFavoUser(user);
			favorite.setTweet(tweet);
			favoriteService.save(favorite);
			flash = new FlashData().success("お気に入りに追加しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/tweets/listOfFavorite";
	}

}
