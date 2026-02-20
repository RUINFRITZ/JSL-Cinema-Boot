package com.cinema.controller;

import com.cinema.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** **
 * 管理者ダッシュボード用 REST API コントローラー
 * チャート描画のためのJSONデータを提供します。
 ** **/
@RestController // @ResponseBody が全てのアクションに適用されます (JSONを返却)
@RequestMapping("/admin/api")
@RequiredArgsConstructor
@Slf4j
public class AdminApiController {

    private final AdminMapper adminMapper;

    /**
     * 直近7日間の日別売上データを取得します。
     * @return 日付と売上のリスト (List of Maps)
     */
    @GetMapping("/revenue/weekly")
    public List<Map<String, Object>> getWeeklyRevenue() {
        log.info("API Request: 週間売上データの取得 (Fetch Weekly Revenue)");
        // DBから直近7日間のデータを取得 (Fetch from DB for the last 7 days)
        return adminMapper.selectWeeklyRevenue();
    }
}