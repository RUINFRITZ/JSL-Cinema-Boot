package com.cinema.service;

import com.cinema.domain.Member;
import com.cinema.dto.CustomUserDetails;
import com.cinema.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** **
 * カスタムユーザー詳細サービス
 * Spring Security の認証プロセスにおいて、データベースからユーザー情報をロードする役割を担います。
 * {@link UserDetailsService} インターフェースを実装しています。
 ** **/
@Service
@Slf4j // ログ出力用 (Lombok)
@RequiredArgsConstructor // final フィールドのコンストラクタ自動生成
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    /*
     * ユーザー名によるユーザー情報のロード
     * ログイン画面で入力されたユーザー名 (username) を基に、データベースから会員情報を検索します。
     * 該当するユーザーが存在しない場合、{@link UsernameNotFoundException} をスローします。
     *
     * @param username ログインフォームから入力されたID
     * @return UserDetails インターフェースを実装した CustomUserDetails オブジェクト
     * @throws UsernameNotFoundException ユーザーが見つからない場合に発生
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        log.info("ログイン試行 (Login Attempt): username = {}", username);

        // 1. DBから会員情報を検索 (Mapper利用)
        Member member = memberMapper.findByUsername(username);

        // 2. 会員が存在しない場合の例外処理
        if (member == null) {
            log.warn("ユーザーが見つかりません (User Not Found): {}", username);
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }

        // 3. UserDetails (DTO) にラップして返却
        log.info("ユーザー認証成功 (User Found): {}", member);
        return new CustomUserDetails(member);
    }
}