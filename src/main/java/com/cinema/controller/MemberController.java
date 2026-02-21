package com.cinema.controller;

import com.cinema.domain.Member;
import com.cinema.mapper.MemberMapper;
import com.cinema.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** **
 * 会員コントローラー (Member Controller)
 * 会員関連の画面遷移 (Login, Register) およびリクエスト処理を担当します。
 ** **/
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

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
        return "redirect:/member/login";
    }
    
    /*
     * ログインページへの移動
     * SecurityConfig で設定したカスタムログインページを表示します。
     */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }
    
    /*
     * 会員情報の更新処理 (POST)
     * マイページの「基本情報変更」タブからのフォーム送信を処理します。
     *
     * @param member フォームから送信された会員情報
     * @param principal セキュリティ認証オブジェクト (不正な他人の情報書き換えを防止)
     * @param rttr 更新成功・失敗のトーストメッセージを渡すためのオブジェクト
     * @return マイページへのリダイレクトURL
     */
    @PostMapping("/update")
    public String updateMember(Member member, Principal principal, RedirectAttributes rttr) {
        
        // 1. セキュリティチェック: ログインしていない場合は弾く
        if (principal == null) {
            return "redirect:/member/login";
        }
        
        // 2. セキュリティチェック: フォームのuseridと実際のログインユーザーが一致するか検証
        String loggedInUser = principal.getName();
        if (!loggedInUser.equals(member.getUserid())) {
            log.warn(" - Security Warning: Unauthorized update attempt by user {}", loggedInUser);
            rttr.addFlashAttribute("errorMsg", "不正なアクセスです。");
            return "redirect:/ticket/my";
        }

        try {
            // 3. 情報の更新を実行
            memberMapper.updateMemberInfo(member);
            log.info(" - User Info Updated: {}", member.getUserid());
            
            // 4. 成功メッセージをセット (フロントエンドの SweetAlert2 がキャッチします)
            rttr.addFlashAttribute("successMsg", "基本情報が正常に更新されました。");
            
        } catch (Exception e) {
            log.error("会員情報の更新中にエラーが発生しました。", e);
            rttr.addFlashAttribute("errorMsg", "情報の更新に失敗しました。");
        }

        // 更新後はマイページへリダイレクト
        return "redirect:/ticket/my";
    }
}