package com.cinema.controller;

import com.cinema.domain.Movie;
import com.cinema.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** **
 * 予約用非同期通信コントローラー (Ticket API)
 * フロントエンド(Fetch API)からのリクエストに対し、JSON形式でデータを返却します。
 ** **/
@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
@Slf4j
public class TicketApiController {

    private final TicketService ticketService;

    // 1. 予約可能な映画リストを取得
    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> getMovies() {
        return ResponseEntity.ok(ticketService.getAvailableMovies());
    }

    // 2. 選択した映画の予約可能な日付リストを取得
    @GetMapping("/dates")
    public ResponseEntity<List<String>> getDates(@RequestParam("mno") Long mno) {
        return ResponseEntity.ok(ticketService.getAvailableDates(mno));
    }

    // 3. 選択した映画と日付の上映スケジュールを取得
    @GetMapping("/schedules")
    public ResponseEntity<List<Map<String, Object>>> getSchedules(
            @RequestParam("mno") Long mno, 
            @RequestParam("sdate") String sdate) {
        return ResponseEntity.ok(ticketService.getSchedules(mno, sdate));
    }
}