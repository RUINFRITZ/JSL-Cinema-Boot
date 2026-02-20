package com.cinema.controller;

import com.cinema.domain.Schedule;
import com.cinema.mapper.MovieMapper;
import com.cinema.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** **
 * 管理者用上映スケジュール管理コントローラー 
 * 映画と上映館を紐付け、上映時間を管理します。
 * URLパスは '/admin/schedule' で始まります。
 ** **/
@Controller
@RequestMapping("/admin/schedule")
@RequiredArgsConstructor
@Slf4j
public class AdminScheduleController {

    private final MovieMapper movieMapper;
    private final ScheduleMapper scheduleMapper;

    /*
     * 上映スケジュール登録画面表示
     * URL: /admin/schedule/register (GET)
     *
     * @param model 画面に渡すデータモデル
     * @return テンプレートパス (admin/schedule_register)
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        log.info("Admin: Schedule Register Form Accessed");
        model.addAttribute("movieList", movieMapper.selectAllMovies());
        model.addAttribute("theaterList", scheduleMapper.selectAllTheaters());
        return "admin/schedule_register";
    }

    /*
     * 上映スケジュール登録処理
     * URL: /admin/schedule/register (POST)
     *
     * @param schedule フォームから送信されたスケジュール情報
     * @param rttr リダイレクト時のメッセージ伝達用
     * @return リダイレクト先 (/admin/schedule/register)
     */
    @PostMapping("/register")
    public String register(Schedule schedule, RedirectAttributes rttr) {
        log.info("Schedule Register: {}", schedule);
        
        // データベースへの登録処理
        scheduleMapper.insertSchedule(schedule);
        
        // [追加] 登録成功のフラッシュメッセージを追加 (Success Message)
        rttr.addFlashAttribute("msg", "上映スケジュールが正常に登録されました。");
        
        // 連続登録のために同じページへリダイレクト
        return "redirect:/admin/schedule/register"; 
    }
}