package com.cinema.controller;

import com.cinema.domain.Movie;
import com.cinema.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/** **
 * 管理者用映画管理コントローラー 
 * 映画の登録、修正、削除、一覧表示などの管理機能を提供します。
 * URLパスは '/admin/movie' で始まります。
 ** **/
@Controller
@RequestMapping("/admin/movie")
@RequiredArgsConstructor
@Slf4j
public class AdminMovieController {

    private final MovieService movieService;

    /*
     * 映画リスト画面表示
     * URL: /admin/movie/list
     *
     * @param model 画面に渡すデータモデル
     * @return テンプレートパス (admin/movie_list)
     */
    @GetMapping("/list")
    public String list(Model model) {
        List<Movie> list = movieService.getMovieList();
        model.addAttribute("list", list);
        log.info(" - Admin: Movie List Page Accessed");
        return "admin/movie_list";
    }

    /*
     * 映画登録フォーム表示
     * URL: /admin/movie/register (GET)
     *
     * @return テンプレートパス (admin/movie_register)
     */
    @GetMapping("/register")
    public String registerForm() {
        log.info(" - Admin: Register Form Accessed");
        return "admin/movie_register";
    }

    /*
     * 映画登録処理
     * URL: /admin/movie/register (POST)
     *
     * @param movie フォームから送信された映画情報
     * @param file アップロードされたポスター画像
     * @param rttr リダイレクト時のメッセージ伝達用
     * @return リダイレクト先 (/admin/movie/list)
     */
    @PostMapping("/register")
    public String register(Movie movie, @RequestParam("file") MultipartFile file, RedirectAttributes rttr) {
        try {
            movieService.registerMovie(movie, file);
            rttr.addFlashAttribute("msg", " - 映画が正常に登録されました。");
        } catch (IOException e) {
            log.error("File Upload Failed", e);
            rttr.addFlashAttribute("error", " * ファイルアップロード失敗");
        }
        return "redirect:/admin/movie/list";
    }

    /*
     * 映画詳細・修正画面表示
     * URL: /admin/movie/detail
     *
     * @param mno 映画番号 (PK)
     * @param model データモデル
     * @return テンプレートパス (admin/movie_modify)
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("mno") Long mno, Model model) {
        Movie movie = movieService.getMovieDetail(mno);
        model.addAttribute("movie", movie);
        return "admin/movie_modify";
    }

    /*
     * 映画修正処理
     * URL: /admin/movie/modify (POST)
     *
     * @param movie 修正された映画情報
     * @param file 新しいポスター画像 (任意)
     * @param rttr リダイレクト属性
     * @return リダイレクト先
     */
    @PostMapping("/modify")
    public String modify(Movie movie, @RequestParam(value = "file", required = false) MultipartFile file, RedirectAttributes rttr) {
        try {
            movieService.modifyMovie(movie, file);
            rttr.addFlashAttribute("msg", " - 修正成功");
        } catch (IOException e) {
            log.error("Modify Failed", e);
            rttr.addFlashAttribute("error", " * 修正失敗");
        }
        return "redirect:/admin/movie/list";
    }

    /*
     * 映画削除処理
     * URL: /admin/movie/delete (POST)
     *
     * @param mno 削除する映画番号
     * @param rttr リダイレクト属性
     * @return リダイレクト先
     */
    @PostMapping("/delete")
    public String delete(@RequestParam("mno") Long mno, RedirectAttributes rttr) {
        movieService.deleteMovie(mno);
        rttr.addFlashAttribute("msg", " - 削除成功");
        return "redirect:/admin/movie/list";
    }
}