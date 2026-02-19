package com.cinema.controller;

import com.cinema.domain.Schedule;
import com.cinema.mapper.MovieMapper;
import com.cinema.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/** **
 * 管理者用 上映スケジュール管理コントローラー
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
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        // 映画リストとスクリーンリストを両方Modelに詰める
        model.addAttribute("movieList", movieMapper.selectAllMovies());
        model.addAttribute("theaterList", scheduleMapper.selectAllTheaters());
        return "admin/schedule_register";
    }

    /*
     * 上映スケジュール登録処理
     */
    @PostMapping("/register")
    public String register(Schedule schedule) {
        log.info("Schedule Register: {}", schedule);
        scheduleMapper.insertSchedule(schedule);
        
        // 登録後は管理者ダッシュボード、またはスケジュール一覧(後で作成)へリダイレクト
        return "redirect:/admin"; 
    }
}