package com.cinema.mapper;

import com.cinema.domain.Movie;
import com.cinema.domain.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/** **
 * 上映スケジュール マッパー
 * 予約画面にて、映画、日付、時間帯のデータを非同期で提供するためのDAOです。
 ** **/
@Mapper
public interface ScheduleMapper {

    /*
     * スクリーン(上映館)の全リストを取得します。
     * @return スクリーンリスト
     */
    List<Map<String, Object>> selectAllTheaters();

    /*
     * 上映スケジュールを新規登録します。
     * @param schedule 登録するスケジュール情報
     */
    void insertSchedule(Schedule schedule);
    
    /*
     * 現在上映スケジュールが存在する映画のリストを取得します。
     * @return 予約可能な映画リスト
     */
    List<Movie> selectAvailableMovies();

    /*
     * 選択した映画（mno）の予約可能な日付リストを取得します。
     * @param mno 映画番号
     * @return 予約可能な日付の文字列リスト (例: "2026-02-21")
     */
    List<String> selectAvailableDates(@Param("mno") Long mno);

    /*
     * 選択した映画と日付に基づく具体的な上映スケジュールを取得します。
     * @param mno 映画番号
     * @param sdate 選択された日付 (YYYY-MM-DD)
     * @return スケジュール情報（時間、上映館、残席数など）のマップリスト
     */
    List<Map<String, Object>> selectSchedulesByDate(@Param("mno") Long mno, @Param("sdate") String sdate);
    
    /*
     * 選択されたスケジュール(sno)の詳細情報(映画、上映館の座席数など)を取得します。
     * @param sno スケジュール番号
     * @return スケジュール詳細情報マップ
     */
    Map<String, Object> selectScheduleDetail(@Param("sno") Long sno);

    /*
     * 該当スケジュールの既に予約済みの座席リストを取得します。
     * @param sno スケジュール番号
     * @return 予約済み座席のリスト (例: ["A-1", "B-2"])
     */
    List<String> selectBookedSeats(@Param("sno") Long sno);
}