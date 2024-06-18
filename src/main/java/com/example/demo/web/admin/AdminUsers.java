package com.example.demo.web.admin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.common.FlashData;
import com.example.demo.common.ValidationGroups.Update;
import com.example.demo.entity.User;
import com.example.demo.service.FollowService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUsers {
	@Autowired
	UserService userService;
	@Autowired
	FollowService followService;
	
	/*
	 * ユーザ一覧表示
	 */
	@GetMapping("/list")
	public String list(Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		try {
			user = userService.findByEmail(email);
			// パスワードは表示しないので、空にする
			user.setPassword("");
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			return "redirect:/users/login";
		}
		List<User> users = userService.findAll();
		users.remove(users.indexOf(user));
		
		// 一対多の関係からリストを抽出 ストリームに流してIDのリストを生成
		Set<Integer> followinUserIdsSet = user.getFollowUser().stream()
				.map(e -> e.getFollowingUser().getId())
				.collect(Collectors.toSet());
		
		model.addAttribute("user", user);
		model.addAttribute("followinUserIdsSet", followinUserIdsSet);
		model.addAttribute("users", users);
		return "admin/users/list";
	}
	
	/*
	 * フォロー処理
	 */
	@GetMapping("/follow/{id}")
	public String follow(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		return "redirect:/admin/timeline";
	}
	
	/*
	 * ユーザ情報更新画面表示
	 */
	@GetMapping("/edit")
	public String edit(Model model, RedirectAttributes ra) {
		FlashData flash;
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user;
		try {
			user = userService.findByEmail(email);
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("ユーザデータがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/users/login";
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
			return "redirect:/users/login";
		}
		model.addAttribute("user", user);
		return "admin/users/edit";
	}
	
	/*
	 * ユーザ情報更新処理
	 */
	@PostMapping("/edit")
	public String update(
			@Validated(Update.class) User editUser, 
			BindingResult result, 
			Model model, 
			RedirectAttributes ra
			) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		FlashData flash;
		try {
			User authUser = userService.findByEmail(email);
			if (result.hasErrors() || !authUser.getEmail().equals(editUser.getEmail()) ) {
				model.addAttribute("user", authUser);
				return "admin/users/edit";
			}
			authUser.setNickname(editUser.getNickname());
			userService.save(authUser);
			flash = new FlashData().success("更新しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/timeline";
	}
	
}
