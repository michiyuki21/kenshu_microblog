package com.example.demo.web.admin;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.common.FlashData;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.TweetService;
import com.example.demo.service.UserService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Controller
@RequestMapping("/admin")
public class Admin {
	@Autowired
	TweetService tweetService;
	@Autowired
	UserService userService;
	@Autowired
	FavoriteService favoriteService;
	@Autowired
	public Validator validator;
	
	/*
	 * タイムライン表示
	 */
	@GetMapping("/timeline")
	public String timeline(Tweet tweet, Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		int userId;
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
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		}
		// タイムライン表示用のクエリを自作
		tweets = tweetService.timelineTweetsFindByUserId(userId);
		
		Set<Integer> favoriteTweetIdsSet;
		favoriteTweetIdsSet = favoriteService.favoriteTweetIdsSetFindByUserId(userId);
		
		model.addAttribute("user", user);
		model.addAttribute("tweet", tweet);
		model.addAttribute("tweets", tweets);
		model.addAttribute("favoriteTweetIdsSet", favoriteTweetIdsSet);
		Errors errors = new BeanPropertyBindingResult(tweet, "tweet");
		model.addAttribute("errors", errors);
		return "admin/timeline";
	}
	
	/*
	 * つぶやき処理
	 */
	@PostMapping("/timeline")
	public String tweet(Tweet tweet, Model model, RedirectAttributes ra) {
//	バリデーションを取るタイミングをずらす
//	public String tweet((@Valid Tweet tweet, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash = null;
		Errors errors = new BeanPropertyBindingResult(tweet, "tweet");
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		try {
			user = userService.findByEmail(email);
			tweet.setUser(user);
			
			// バリデーション結果を取得
			Set<ConstraintViolation<Tweet>> errorResult = validator.validate(tweet);
			for (ConstraintViolation<Tweet> violation : errorResult) {
				errors.rejectValue(violation.getPropertyPath().toString(), "", violation.getMessage());
			}
			if (errors.hasErrors()) {
				model.addAttribute("user", user);
				model.addAttribute("errors", errors);
				List<Tweet> tweets;
				tweets = tweetService.timelineTweetsFindByUserId(user.getId());
				model.addAttribute("tweets", tweets);
				return "admin/timeline";
			}
			tweetService.save(tweet);
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/timeline";
	}

}
