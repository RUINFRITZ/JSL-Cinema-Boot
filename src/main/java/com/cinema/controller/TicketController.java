package com.cinema.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cinema.mapper.MemberMapper;
import com.cinema.mapper.ReservationMapper;
import com.cinema.service.TicketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** **
 * 予約画面コントローラー 
 * ユーザーが映画を予約するためのUI(HTML)を提供します。
 ** **/
@Controller
@RequestMapping("/ticket")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

	private final TicketService ticketService;
	private final ReservationMapper reservationMapper;
	private final MemberMapper memberMapper;
	
    /*
     * 予約メイン画面の表示
     * メインページから特定の映画を選択した場合、パラメータ(mno)を受け取ります。
     *
     * @param mno 選択された映画番号 (任意)
     * @param model Viewへデータを渡すモデル
     * @return 予約画面 (ticket/reserve)
     */
    @GetMapping("/reserve")
    public String reserveForm(@RequestParam(value = "mno", required = false) Long mno, Model model) {
        log.info(" - User: Ticket Reservation Page Accessed (mno: {})", mno);
        
        // メイン画面から遷移してきた場合、初期選択状態とするためにmnoをモデルに格納
        if (mno != null) {
            model.addAttribute("selectedMno", mno);
        }
        
        // UIの骨組みだけを返し、実際のデータ（映画一覧、日付など）は
        // フロントエンドのJavaScript (Fetch API) が非同期で取得する設計としています。
        return "ticket/reserve"; 
    }
    
    /*
     * 座席選択画面の表示
     * URL: /ticket/seat
     *
     * @param sno 選択されたスケジュール番号
     * @param model Viewへデータを渡すモデル
     * @return 予約座席選択画面 (ticket/seat)
     */
    @GetMapping("/seat")
    public String seatForm(@RequestParam("sno") Long sno, Principal principal, Model model) {
        log.info(" - User: Seat Selection Page Accessed (sno: {})", sno);

        try {
            if (principal != null) {
                String userid = principal.getName();
                int currentPoint = memberMapper.selectCurrentPoint(userid);
                model.addAttribute("currentPoint", currentPoint);
            } else {
                model.addAttribute("currentPoint", 0);
            }

            // スケジュール詳細と予約済み座席リストを取得
            Map<String, Object> detail = ticketService.getScheduleDetail(sno);
            List<String> bookedSeats = ticketService.getBookedSeats(sno);

            model.addAttribute("detail", detail);
            model.addAttribute("bookedSeats", bookedSeats);
            
        } catch (Exception e) {
            log.error(" * 座席情報の取得中にエラーが発生しました。", e);
            return "redirect:/ticket/reserve"; 
        }

        return "ticket/seat";
    }
    
    /*
     * 決済処理および予約データの保存 (POST)
     * フロントエンドから渡された予約情報をDBに保存し、マイページへリダイレクトします。
     *
     * @param sno スケジュール番号
     * @param seatInfo 選択された座席文字列
     * @param price 決済合計金額
     * @param principal Spring Securityの認証ユーザー情報
     * @param rttr リダイレクト時のフラッシュメッセージ用
     * @return マイページへのリダイレクトURL
     */
    @PostMapping("/process")
    public String processReservation(@RequestParam("sno") Long sno,
                                     @RequestParam("seatInfo") String seatInfo,
                                     @RequestParam("price") int price,
                                     Principal principal,
                                     RedirectAttributes rttr) {
        
        // ログイン状態の確認 (本来はSpring Securityのインターセプターで防ぎますが、念のための安全装置)
        if (principal == null) {
            log.warn(" - Security: Unauthorized Reservation Attempt");
            rttr.addFlashAttribute("errorMsg", "ログインが必要です。");
            return "redirect:/member/login";
        }

        String userid = principal.getName();
        log.info(" - Process Reservation Request (User: {}, Sno: {}, Seats: {})", userid, sno, seatInfo);

        try {
            // トランザクション処理の呼び出し
            ticketService.processReservation(sno, seatInfo, price, userid);
            
            // 成功メッセージをセットしてマイページへ
            rttr.addFlashAttribute("successMsg", "チケットの予約と決済が完了しました。");
            return "redirect:/ticket/my"; 

        } catch (Exception e) {
            log.error(" * 予約処理失敗", e);
            rttr.addFlashAttribute("errorMsg", "予約処理に失敗しました。もう一度お試しください。");
            return "redirect:/ticket/seat?sno=" + sno;
        }
    }
    
    /*
     * マイページ（チケット一覧）画面の表示
     * URL: /ticket/my
     *
     * @param principal Spring Securityの認証ユーザー情報
     * @param model Viewへデータを渡すモデル
     * @return マイページ画面 (ticket/my)
     */
    @GetMapping("/my")
    public String myPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/member/login";
        }

        String userid = principal.getName();
        log.info(" - User: My Page Accessed (User: {})", userid);

        try {
            // 1. チケット履歴の取得
            List<Map<String, Object>> ticketList = reservationMapper.selectMyTickets(userid);
            model.addAttribute("ticketList", ticketList);
            
            // 2. 会員情報の取得 (氏名、等級、残高ポイントなどの表示用)
            // ※ MemberMapper に findByUsername(userid) が実装されている前提です。
            com.cinema.domain.Member member = memberMapper.findByUsername(userid);
            model.addAttribute("member", member);
            model.addAttribute("userid", userid); 
            
            // 3. ポイント履歴の取得
            List<Map<String, Object>> pointList = memberMapper.selectPointHistory(userid);
            model.addAttribute("pointList", pointList);
            
        } catch (Exception e) {
            log.error("マイページデータの取得中にエラーが発生しました。", e);
        }

        return "ticket/my";
    }
    
}