package com.example.demo.web.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.common.FlashData;
import com.example.demo.entity.Follow;
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
	public String list( Model model, RedirectAttributes ra) {
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
		List<Follow> follows;
		try {
			follows = followService.findByUserId(user.getId());
		} catch (DataNotFoundException e) {
			follows = null;
		}
		List<User> users = userService.findAll();
		model.addAttribute("users", users.remove(users.indexOf(user)));
		model.addAttribute("follows", follows);
		return "admin/users/list";
	}

}
