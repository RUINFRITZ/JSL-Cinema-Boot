package com.cinema.controller;

import com.cinema.domain.Member;
import com.cinema.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** **
 * 会員コントローラー (Member Controller)
 * 会員関連の画面遷移 (Login, Register) およびリクエスト処理を担当します。
 ** **/
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /*
     * 会員登録ページへの移動
     * @return "member/join" (src/main/resources/templates/member/join.html)
     */
    @GetMapping("/register")
    public String joinForm() {
        return "member/join";
    }

    /*
     * 会員登録処理 (Action)
     * フォームから送信されたデータを受け取り、Serviceを通じてDBに保存します。
     * 処理完了後はログインページへリダイレクトします。
     *
     * @param member フォームデータ (自動マッピング)
     * @return ログインページへのリダイレクトURL
     */
    @PostMapping("/register")
    public String join(Member member, RedirectAttributes redirectAttributes) {
        // ビジネスロジック呼び出し
        memberService.join(member);
        
        // フラッシュメッセージ (Flash Message) を設定
        // リダイレクト先に一度だけデータを渡す機能です。
        // "msg" という名前で "会員登録が完了しました" というメッセージを梱包します。
        redirectAttributes.addFlashAttribute("msg", "会員登録が完了しました。ログインしてください。");
        redirectAttributes.addFlashAttribute("title", "登録成功");
        
        // 登録完了後、ログインページへ移動 (PRGパターン: Post-Redirect-Get)
        return "redirect:/login";
    }
    
    /*
     * ログインページへの移動
     * SecurityConfig で設定したカスタムログインページを表示します。
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }
}