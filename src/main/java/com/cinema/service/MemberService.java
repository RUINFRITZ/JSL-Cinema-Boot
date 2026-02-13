package com.cinema.service;

import com.cinema.domain.Member;
import com.cinema.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** **
 * 会員管理サービス (Member Service)
 * 
 * 会員登録、情報修正、退会などのビジネスロジックを担当します。
 * トランザクション管理 (@Transactional) を適用し、データの整合性を保ちます。
 ** **/
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    /*
     * 新規会員登録処理
     * 1. 入力されたパスワードを BCrypt アルゴリズムで暗号化します。
     * 2. 権限 (Role) を 'ROLE_USER' に設定します。
     * 3. データベースに保存します。
     *
     * @param member 画面から入力された会員情報
     */
    @Transactional // エラー発生時に自動ロールバック (Auto Rollback on Error)
    public void join(Member member) {
        
        // 1. パスワード暗号化 (Password Encryption)
        // 生のパスワード (Raw Password) をそのままDBに保存するのはセキュリティ上、厳禁です。
        String rawPassword = member.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        member.setPassword(encPassword);

        // 2. 基本権限設定 (Default Role Setup)
        member.setRole("ROLE_USER");
        
        // 3. DB保存 (Save to DB)
        memberMapper.insertMember(member);
        
        log.info("新規会員登録完了 (New Member Registered): {}", member.getUserid());
    }
    
    /*
     * ID重複チェック
     * @param userid 確認するID
     * @return 使用可能なら true, 重複していれば false
     */
    public boolean checkIdDuplicate(String userid) {
        return memberMapper.existsById(userid) == 0;
    }
}