package com.cinema.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** **
 * 管理者メインコントローラー (Admin Main Controller)
 * 管理者ダッシュボードおよび共通の管理機能へのエントリーポイントです。
 ** **/
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    /*
     * 管理者ダッシュボード画面表示
     * URL: /admin または /admin/home
     *
     * @return テンプレートパス (admin/home)
     */
    @GetMapping({"", "/home"})
    public String dashboard() {
        log.info(" - Admin: Dashboard Page Accessed");
        return "admin/home";
    }
}