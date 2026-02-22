package com.cinema.controller;

import com.cinema.domain.Movie;
import com.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** **
 * 映画情報コントローラー
 * 映画の詳細情報の照会およびレビュー管理を担当します。
 ** **/
@Controller
@RequestMapping("/movie")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    /*
     * 映画詳細情報の表示
     * URL: /movie/detail/{mno}
     *
     * @param mno 映画番号
     * @param model Viewへデータを渡すモデル
     * @return 映画詳細画面 (movie/detail)
     */
    @GetMapping("/detail/{mno}")
    public String movieDetail(@PathVariable("mno") Long mno, Model model) {
        log.info(" - User: Movie Detail Page Accessed (mno: {})", mno);

        // 映画の基本情報を取得
        Movie movie = movieService.getMovieDetail(mno);
        
        // 取得失敗時の例外処理
        if (movie == null) {
            log.warn(" - Movie Detail Error: Invalid mno ({})", mno);
            return "redirect:/";
        }

        model.addAttribute("movie", movie);
        
        // 今後、ここにレビューリストや平均点などのデータを追加していく予定です。
        // model.addAttribute("reviews", reviewService.getReviewsByMno(mno));

        return "movie/detail";
    }
    
    /*
     * 映画一覧および検索結果の表示
     * URL: /movie/list?keyword=検索語
     *
     * @param keyword 検索キーワード（任意）
     * @param model Viewへデータを渡すモデル
     * @return 映画一覧画面 (movie/list)
     */
    @GetMapping("/list")
    public String movieList(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        log.info(" - User: Movie List Page Accessed (Keyword: {})", keyword);

        // キーワードに基づく動的検索を実行
        List<Movie> movieList = movieService.getMovieList(keyword);
        
        model.addAttribute("movieList", movieList);
        model.addAttribute("keyword", keyword); // 検索結果画面にキーワードを表示するため

        return "movie/list";
    }
}