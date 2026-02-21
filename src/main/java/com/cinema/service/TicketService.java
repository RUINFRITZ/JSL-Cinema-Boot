package com.cinema.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cinema.domain.Movie;
import com.cinema.mapper.MemberMapper;
import com.cinema.mapper.ReservationMapper;
import com.cinema.mapper.ScheduleMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** **
 * 予約サービス 
 * コントローラーとマッパーの間に位置し、トランザクション、ビジネスロジック、および同時実行制御（排他制御）を管理します。
 ** **/
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final ScheduleMapper scheduleMapper;
    private final ReservationMapper reservationMapper;
    private final MemberMapper memberMapper;

    public List<Movie> getAvailableMovies() {
        return scheduleMapper.selectAvailableMovies();
    }

    /*
     * 予約可能な日付リストを取得します。
     * 本日の場合、現在時刻から10分以上残っているスケジュールが一つもなければ、日付リストから除外します。
     *
     * @param mno 映画番号
     * @return フィルタリングされた日付リスト
     */
    public List<String> getAvailableDates(Long mno) {
        List<String> allDates = scheduleMapper.selectAvailableDates(mno);
        String todayStr = LocalDate.now().toString(); // "yyyy-MM-dd"

        return allDates.stream().filter(date -> {
            if (date.equals(todayStr)) {
                // 本日のスケジュールをすべて取得して検証
                List<Map<String, Object>> todaySchedules = scheduleMapper.selectSchedulesByDate(mno, date);
                LocalTime limitTime = LocalTime.now().plusMinutes(10);

                // 10分後以降に開始するスケジュールが1つでもあれば表示
                return todaySchedules.stream().anyMatch(s -> {
                    LocalTime startTime = LocalTime.parse((String) s.get("start_time"), DateTimeFormatter.ofPattern("HH:mm"));
                    return startTime.isAfter(limitTime);
                });
            }
            return true; // 明日以降の日付はそのまま表示
        }).collect(Collectors.toList());
    }

    /*
     * 特定の映画と日付の上映スケジュールを取得します。
     * 現在時刻から10分以内のスケジュールは除外して返却します。
     *
     * @param mno 映画番号
     * @param sdate 上映日 (yyyy-MM-dd)
     * @return フィルタリングされたスケジュールリスト
     */
    public List<Map<String, Object>> getSchedules(Long mno, String sdate) {
        List<Map<String, Object>> allSchedules = scheduleMapper.selectSchedulesByDate(mno, sdate);
        
        LocalDate today = LocalDate.now();
        LocalDate selectedDate = LocalDate.parse(sdate);

        // 選択された日が今日ではない場合は全リストを返却
        if (!selectedDate.isEqual(today)) {
            return allSchedules;
        }

        // 今日である場合、現在時刻 + 10分以降のスケジュールのみフィルタリング
        LocalTime limitTime = LocalTime.now().plusMinutes(10);

        return allSchedules.stream().filter(s -> {
            LocalTime startTime = LocalTime.parse((String) s.get("start_time"), DateTimeFormatter.ofPattern("HH:mm"));
            return startTime.isAfter(limitTime);
        }).collect(Collectors.toList());
    }
    
    public Map<String, Object> getScheduleDetail(Long sno) {
        return scheduleMapper.selectScheduleDetail(sno);
    }

    public List<String> getBookedSeats(Long sno) {
        return scheduleMapper.selectBookedSeats(sno);
    }
    
    /*
     * 予約決済のトランザクション処理を実行します。
     * 予約テーブル（reservation）における座席の重複チェックおよび悲観的ロックを行い、
     * ポイント決済と予約登録を一連のプロセスとして管理します。
     * * @param sno スケジュール番号
     * @param seatInfo 座席情報 (例: "E-10,E-11")
     * @param price 金額
     * @param userid ユーザーID
     */
    @Transactional(rollbackFor = Exception.class)
    public void processReservation(Long sno, String seatInfo, int price, String userid) {
        log.info(" - Transaction Start: Conflict Check & Point Payment (User: {})", userid);

        try {
            // 0. 座席情報のパースおよび重複予約の検証（排他制御）
            // seatInfoをリストに変換し、既存の予約データとの競合を確認します。
            List<String> seatList = Arrays.asList(seatInfo.split(","));

            // SELECT ... FOR UPDATE を用いて、該当スケジュールの予約状況をロック状態で照会します。
            // 既に同一の座席が登録されている場合、その件数を取得します。
            int conflictCount = reservationMapper.checkExistingReservationForUpdate(sno, seatList);
            
            // 競合する予約が1件以上存在する場合、重複と判断し例外をスローします。
            if (conflictCount > 0) {
                log.warn(" - Transaction Failed: Seat already taken (Schedule: {}, Seat: {})", sno, seatInfo);
                throw new IllegalStateException("誠に恐れ入りますが、選択された座席は既に他のお客様によって予約されております。別の座席をご選択ください。");
            }

            // 1. 現在の保有ポイントの確認
            int currentPoint = memberMapper.selectCurrentPoint(userid);
            
            // 2. ポイント残高の検証
            if (currentPoint < price) {
                log.warn(" - Transaction Failed: Insufficient points (User: {}, Required: {})", userid, price);
                throw new IllegalStateException("ポイントが不足しています。");
            }

            // 3. ポイントの減算処理および履歴の記録
            memberMapper.updateMemberPoint(userid, -price);
            memberMapper.insertPointHistory(userid, -price, "映画チケット決済");

            // 4. 予約情報の新規登録
            // 取得したsno, userid, seatInfo, priceを基に、status='PAID'としてデータを挿入します。
            // ※ 別途のseatsテーブルがないため、この登録が座席占有を意味します。
            reservationMapper.insertReservation(sno, userid, seatInfo, price);
            
            // 5. 会員ランクに応じたポイント還元（リワード）
            double pointRate = memberMapper.selectPointRate(userid);
            int earnedPoints = (int) (price * pointRate);
            
            if (earnedPoints > 0) {
                memberMapper.updateMemberPoint(userid, earnedPoints);
                memberMapper.insertPointHistory(userid, earnedPoints, "チケット決済に伴うリワード積立");
            }
            
            log.info(" - Transaction Success: Reservation Completed. Paid: {} P, Earned: {} P", price, earnedPoints);

        } catch (IllegalStateException e) {
            // ビジネスロジック上の例外（重複、残高不足）はそのままスローし、上位へ通知します。
            throw e;
        } catch (Exception e) {
            // 予期せぬDBエラーなどのシステム例外発生時、ログを記録しロールバックを誘導します。
            log.error(" * Transaction Failed: Critical DB Error -> Rollback", e);
            throw new RuntimeException("システムエラーが発生しました。処理を中断します。", e);
        }
    }
    
}