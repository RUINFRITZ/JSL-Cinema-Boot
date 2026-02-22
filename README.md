

# 🎬 JSL Cinema REMASTER (Spring Boot & MyBatis)
**JSL人材開発院 Cloud連携Web開発者課程26期 個人プロジェクト II**

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.9-6DB33F?style=flat-square&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java)
![MyBatis](https://img.shields.io/badge/MyBatis-3.0.3-black?style=flat-square)
![MySQL](https://img.shields.io/badge/MySQL-8.0.42-4479A1?style=flat-square&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-005F0F?style=flat-square&logo=thymeleaf)

## 📌 Project Overview (プロジェクト概要)
既存のJSPベースのレガシー映画予約システムを、最新の **Spring Boot 3.x & MyBatis** アーキテクチャへと全面リファクタリング（Remaster）した個人プロジェクトです。

単なる機能実装にとどまらず、データ整合性を保証するトランザクション処理（ACID）、SSR（Server-Side Rendering）の構造理解、そして純粋なJavaScript（Vanilla JS）を活用した非同期通信（Fetch API）など、**バックエンドエンジニアとしての確かな基礎体力とSQL制御能力**を証明することに重点を置いて開発しました。

---

## 🛠 Tech Stack & Architecture (技術スタックと選定理由)

| Category | Technology | Version | Selection Reason & Appeal (選定理由) |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 17 (LTS) | **[標準と安定性]** Recordパターンなどを活用し、不要なコードを削減して可読性を向上。 |
| **Framework** | Spring Boot | 3.5.9 | **[生産性とモダン開発]** AutoConfigurationにより設定を短縮し、予約・決済などのコアビジネスロジックの実装に集中。 |
| **Database** | MySQL | 8.0.42 | **[データ整合性]** 映画予約システムの要であるトランザクション(ACID)処理を完璧に遂行するため。 |
| **O/R Mapper** | MyBatis | 3.0.3 | **[SQL制御能力]** 日本のSI現場で求められる複雑なクエリ作成に対応するため、JPAではなくSQLの直接チューニングが可能なMyBatisを選定。 |
| **Security** | Spring Security | 6.x | **[最新セキュリティ]** `SecurityFilterChain` (Lambda DSL) を適用し、堅牢な認証・認可ロジックを実装。 |
| **Template** | Thymeleaf | 3.x | **[SSRの理解]** JSPの動作原理(SSR)を理解しつつ、HTML構造を崩さずに保守性を高めるため。 |
| **Frontend** | Vanilla JS + Fetch | ES6+ | **[基礎体力]** React等に依存せず、DOM操作と非同期通信(Async/Await)の原理を深く理解するため純粋なJSに固執。 |
| **Build Tool** | Gradle / STS | 8.x / 4.31 | **[効率性]** ビルド速度の改善と効率的な依存関係の管理。 |

---

## 🚀 Key Features (主要機能)

### 1. Security & Authentication (セキュリティと認証)
* `CustomUserDetails`を用いた認証および権限（USER / ADMIN）の徹底分離。
* ログイン・会員登録時にSweetAlert2を用いた洗練された非同期トースト通知(Toast UI)を提供。

### 2. CMS & Master Data (管理者システム)
* `MultipartFile`を活用したポスター画像のI/Oトランザクションと静的リソースマッピング。
* 映画(1)と上映館(1)を組み合わせてスケジュール(N)を生成する多対多(N:M)関係の解消と直感的なUIマッピング。

### 3. Core Business Logic (予約および同時実行制御)
* MyBatis `<if>` タグを活用した動的クエリ検索と、CSS Gridを用いたレスポンシブな映画ギャラリーの実装。
* **同時実行制御 (Concurrency Control):** Fetch APIによるリアルタイムな座席状態の反映と、オーバーブッキングを防ぐ制約・ロックロジックの実装。
* **トランザクション管理:** `座席選択 -> 決済(PortOne API) -> ポイント減算 -> チケット発行`の一連のプロセスを `@Transactional` で束ね、例外発生時の完全なロールバック(Rollback)を保証。

### 4. UX Refinement & Optimization (ユーザー体験の高度化)
* Glassmorphismデザインを取り入れたモダンなUIと、アコーディオン(Accordion)形式のカスタマーサポートページ実装。
* ページ遷移のない非同期レビューCRUDと、インラインエディティング(Inline Editing)機能。
* レビュー更新時、サーバーへ再リクエストせずにクライアント側(JS `reduce`)で平均評価スコアを動的に再計算する状態同期(State Synchronization)の最適化。

---

## 📂 Project Structure (ディレクトリ構造)
MVCパターンと関心事の分離(SoC)に基づき、ドメインごとにパッケージを構造化しています。

```text
src/main/java/com/cinema/
 ├── config/       # Spring Security, WebMvc 設定
 ├── controller/   # 画面遷移およびREST APIエンドポイント (Admin, Member, Movie, Ticket等)
 ├── domain/       # テーブルと1:1でマッピングされるエンティティ (Builderパターン適用)
 ├── dto/          # データ転送オブジェクト (CustomUserDetails等)
 ├── mapper/       # MyBatisインターフェース (SQLマッピング)
 └── service/      # コアビジネスロジックおよびトランザクション制御
 
src/main/resources/
 ├── mapper/       # SQLクエリを記述したXMLファイル
 ├── static/       # CSS (Glassmorphism適用), JS, 画像等の静的リソース
 └── templates/    # Thymeleaf View テンプレート
      ├── admin/   # 管理者用画面 (映画登録、スケジュール管理)
      ├── layout/  # 共通レイアウト (Header, Footer)
      ├── member/  # ログイン、会員登録
      ├── movie/   # 映画一覧、詳細ページ (レビュー機能含む)
      ├── support/ # FAQ、利用規約、プライバシーポリシー
      └── ticket/  # 予約進行、座席選択、マイページ(チケット確認)
