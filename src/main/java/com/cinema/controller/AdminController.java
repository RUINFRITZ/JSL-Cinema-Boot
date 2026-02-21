package com.cinema.controller;

import com.cinema.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** **
 * 管理者メインコントローラー 
 * 管理者ダッシュボードおよび共通の管理機能へのエントリーポイントです。
 ** **/
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // [追加] 依存性注入(DI)のためのアノテーション
@Slf4j
public class AdminController {

    // [追加] AdminMapperを注入
    private final AdminMapper adminMapper;

    /*
     * 管理者ダッシュボード画面表示
     * URL: /admin または /admin/home
     *
     * @param model 画面に渡すデータモデル
     * @return テンプレートパス (admin/home)
     */
    @GetMapping({"", "/home"})
    public String dashboard(Model model) { // [修正] Modelオブジェクトを引数に追加
        log.info(" - Admin: Dashboard Page Accessed");

        // 1. 各種統計データをDBから取得 (Fetch data from DB)
        Integer todayRevenue = adminMapper.getTodayRevenue();
        Integer totalMembers = adminMapper.getTotalMembers();
        Integer activeMovies = adminMapper.getActiveMovies();

        // 2. Modelにデータを格納し、Viewへ伝達 (Bind data to Model)
        model.addAttribute("todayRevenue", todayRevenue);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("activeMovies", activeMovies);
        
        // ※ 予約率は現在ダミー値(78)を設定。後日ロジック実装時に変更可能。
        model.addAttribute("reservationRate", 78);

        // 3. 最新予約リストを取得して格納 (Fetch recent reservations)
        model.addAttribute("recentList", adminMapper.getRecentReservations());

        return "admin/home";
    }
    
    /*
     * 予約一覧（全体）画面表示
     * URL: /admin/reservation/list
     *
     * @param model ビューに渡すデータを格納するModelオブジェクト
     * @return テンプレートパス (admin/reservation_list)
     */
    @GetMapping("/reservation/list")
    public String reservationList(Model model) {
        log.info(" - Admin: Reservation List Page Accessed");

        try {
            // 全予約データを取得し、モデルに格納します
            model.addAttribute("reservationList", adminMapper.getAllReservations());
        } catch (Exception e) {
            log.error(" * 予約一覧データの取得中にエラーが発生しました。", e);
        }

        return "admin/reservation_list"; // 新規作成するHTMLファイル名
    }
}