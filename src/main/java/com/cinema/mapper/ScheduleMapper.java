package com.cinema.mapper;

import com.cinema.domain.Schedule;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/** **
 * 上映スケジュール マッパー
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
}