DROP DATABASE IF EXISTS jsl26db; 
CREATE DATABASE jsl26db;         
CREATE USER jsl26@localhost identified BY '1234';
GRANT ALL privileges ON jsl26db.* to 'jsl26'@'localhost';
FLUSH PRIVILEGES;
USE jsl26db;                 

-- --------------------------------------------------------
-- JSL Cinema REMASTER (For Spring Boot + MySQL)
-- 設計者: RUINFRITZ
-- --------------------------------------------------------
use jsl26db;

-- 1. 회원 등급 (Member Grade) - 기존 유지하되 Role 매핑 고려
CREATE TABLE member_grade (
    mgrade INT PRIMARY KEY,
    grade_name VARCHAR(20) NOT NULL COMMENT '등급명 (Member, VIP...)',
    point_rate DECIMAL(3, 2) DEFAULT 0.01 COMMENT '포인트 적립율',
    discount_rate DECIMAL(3, 2) DEFAULT 0 COMMENT '할인율'
);

-- 2. 회원 (Member) - Security 호환 구조로 변경
CREATE TABLE member_cinema (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '내부 관리용 PK',
    userid VARCHAR(50) NOT NULL UNIQUE COMMENT '로그인 ID',
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt 암호화 비번',
    name VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    point INT DEFAULT 0,
    mgrade INT DEFAULT 1 COMMENT 'FK: member_grade',
    role VARCHAR(20) DEFAULT 'ROLE_USER' COMMENT 'Security 권한 (ROLE_USER, ROLE_ADMIN)',
    enabled TINYINT(1) DEFAULT 1 COMMENT '계정 활성 여부 (1:활성, 0:탈퇴/정지)',
    regdate DATETIME DEFAULT NOW(),
    deldate DATETIME DEFAULT NULL,
    FOREIGN KEY (mgrade) REFERENCES member_grade(mgrade)
);

-- 3. 포인트 이력 (Point History)
CREATE TABLE point_history (
    pno INT AUTO_INCREMENT PRIMARY KEY,
    userid VARCHAR(50) NOT NULL,
    amount INT NOT NULL COMMENT '양수:충전/적립, 음수:사용',
    description VARCHAR(100) COMMENT '내용',
    regdate DATETIME DEFAULT NOW(),
    FOREIGN KEY (userid) REFERENCES member_cinema(userid) ON DELETE CASCADE
);

-- 4. 영화 (Movie)
CREATE TABLE movie (
    mno INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT COMMENT 'Oracle VARCHAR2(4000) -> MySQL TEXT',
    poster VARCHAR(500) DEFAULT 'default.jpg',
    runtime INT COMMENT '상영시간(분)',
    opendate DATE,
    catchphrase VARCHAR(500),
    regdate DATETIME DEFAULT NOW()
);

-- 5. 상영관 (Theater)
CREATE TABLE theater (
    tno INT AUTO_INCREMENT PRIMARY KEY,
    tname VARCHAR(50) NOT NULL,
    trow INT NOT NULL,
    tcol INT NOT NULL,
    total_seats INT NOT NULL
);

-- 6. 상영 시간표 (Schedule)
CREATE TABLE schedule (
    sno INT AUTO_INCREMENT PRIMARY KEY,
    mno INT NOT NULL,
    tno INT NOT NULL,
    sdate DATETIME NOT NULL COMMENT '상영 시작 시간',
    FOREIGN KEY (mno) REFERENCES movie(mno) ON DELETE CASCADE,
    FOREIGN KEY (tno) REFERENCES theater(tno) ON DELETE CASCADE
);

-- 7. 예매 (Reservation) - 상태값(status) 추가
CREATE TABLE reservation (
    rno INT AUTO_INCREMENT PRIMARY KEY,
    sno INT NOT NULL,
    userid VARCHAR(50) NOT NULL,
    seat_info VARCHAR(20) NOT NULL COMMENT '좌석번호 (예: A-1)',
    price INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PAID' COMMENT 'PAID(결제), CANCEL(취소)',
    rdate DATETIME DEFAULT NOW(),
    FOREIGN KEY (sno) REFERENCES schedule(sno) ON DELETE CASCADE,
    FOREIGN KEY (userid) REFERENCES member_cinema(userid) ON DELETE CASCADE
);

-- 8. 리뷰 (Review)
CREATE TABLE review (
    rev_no INT AUTO_INCREMENT PRIMARY KEY,
    mno INT NOT NULL,
    userid VARCHAR(50) NOT NULL,
    star INT DEFAULT 5 COMMENT '1~5点の星評価',
    comments VARCHAR(1000),
    regdate DATETIME DEFAULT NOW(),
    FOREIGN KEY (mno) REFERENCES movie(mno) ON DELETE CASCADE,
    FOREIGN KEY (userid) REFERENCES member_cinema(userid) ON DELETE CASCADE
);

-- --------------------------------------------------------
-- 초기 데이터 (Initial Data)
-- --------------------------------------------------------

-- 등급 생성
INSERT INTO member_grade VALUES (1, 'Member', 0.01, 0);
INSERT INTO member_grade VALUES (2, 'VIP', 0.03, 0.05);
INSERT INTO member_grade VALUES (3, 'VVIP', 0.05, 0.1);
INSERT INTO member_grade VALUES (4, 'MVP', 0.1, 0.15);
INSERT INTO member_grade VALUES (0, 'Admin', 0, 0);

-- 관리자 계정 (비번 '1234' 암호화 필요, 일단 테스트용 평문)
-- 실제 운영 시에는 반드시 BCrypt로 암호화된 값을 넣어야 함!
INSERT INTO member_cinema (userid, password, name, email, mgrade, role)
VALUES ('admin', '$2a$12$oYfvjtSgeFch38IXCAJByu81TVfdPQJgCvQbYA0kZkOD4GwzsEo66', '관리자', 'admin@jsl.com', 0, 'ROLE_ADMIN');
INSERT INTO member_cinema (userid, password, name, email, mgrade, role)
VALUES ('test', '$2a$12$oYfvjtSgeFch38IXCAJByu81TVfdPQJgCvQbYA0kZkOD4GwzsEo66', '테스트유저', 'test@jsl.com', 1, 'ROLE_USER');
SELECT * FROM member_cinema;

-- 상영관
INSERT INTO theater (tname, trow, tcol, total_seats) VALUES ('1館', 5, 10, 50);
INSERT INTO theater (tname, trow, tcol, total_seats) VALUES ('2館', 6, 15, 90);
INSERT INTO theater (tname, trow, tcol, total_seats) VALUES ('Dolby Cinema', 10, 20, 200);

-- --------------------------------------------------------
-- 映画マスタテーブルの初期データ挿入 (Insert Movie Seed Data)
-- ※ 各作品の魅力を伝える「キャッチフレーズ (catchphrase)」カラムを追加しています。
-- --------------------------------------------------------

INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('The Lord of the Rings I : The Fellowship of the Ring', '全ての力を支配する黄金の指輪。平凡なホビット族のフロドは、中つ国の平和を守るため、指輪を破壊する過酷な旅に出る。', 'lotr_1.jpg', 178, '2026-02-02', '一つの指輪が、すべてを支配する。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('The Lord of the Rings II : The Two Towers', '旅の仲間が散り散りになる中、フロドとサムは指輪を捨てるべくモルドールへ向かう。一方、アラゴルンたちは迫りくる闇の軍勢との決戦に挑む。', 'lotr_2.jpg', 179, '2026-02-02', '決戦の時、来たる。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('The Lord of the Rings III : The Return of the King', '全てをかけた最後の戦い。王の血を引くアラゴルンが立ち上がり、フロドは滅びの山へと辿り着く。指輪を巡る壮大な物語、ついに完結。', 'lotr_3.jpg', 199, '2026-02-02', 'この日、世界は伝説となる。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('GLADIATOR 2', '復讐を誓った元ローマ軍将軍マキシマス。奴隷に身を落としながらも、剣闘士（グラディエーター）として再び立ち上がり, 皇帝への逆襲を始める。', 'gladiator2.jpg', 155, '2026-02-11', '英雄の血は、闘技場（アリーナ）で再び滾る。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('TRANSFORMERS', '地球に隠された究極のエネルギー源を巡り、正義の「オートボット」と悪の「ディセプティコン」が激突する。人類の命運をかけた戦いが今、始まる。', 'transformers.jpg', 143, '2026-02-11', '変形（トランスフォーム）せよ、未来のために。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('IRON MAN', '億万長者にして天才発明家トニー・スターク。テロリストに拉致された極限状態でパワードスーツを開発し、自ら「アイアンマン」となって悪に立ち向かう。ヒーローの常識を覆す、アクション超大作。', 'iron_man.jpg', 126, '2026-02-11', '私が、アイアンマンだ。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('THE SHAWSHANK REDEMPTION', '無実の罪でショーシャンク刑務所に投獄された銀行家アンディ。絶望の中でも決して希望を捨てず、囚人レッドとの友情を育みながら、自由への奇跡を起こす感動のヒューマンドラマ。', 'shawshank.jpg', 142, '2026-02-18', '希望は、誰にも奪えない。');
INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('LA LA LAND', '夢を追う人々が集う街、ロサンゼルス。女優志望のミアとジャズピアニストのセバスチャンは恋に落ち、互いの夢を応援し合う。甘く切ない恋と夢を描いた、極上のミュージカル・エンターテインメント。', 'lalaland.jpg', 128, '2026-02-18', '夢と恋が交差する、魔法の街へ。');

INSERT INTO movie (title, content, poster, runtime, opendate, catchphrase)
VALUES ('LOVE LETTER', '婚約者を山の事故で亡くした渡辺博子は、忘れられない彼への思いから、彼が昔住んでいた小樽へと一通の手紙を送る。しかし、その手紙は彼と同姓同名で顔も瓜二つの女性のもとへ届き、そこから奇妙な文通が始まる。雪降る小樽を舞台に描かれる、喪失と再生のラブストーリー。', 'love_letter.jpg', 117, '2026-02-18', 'お元気ですか。私は元気です。');

COMMIT;

-- --------------------------------------------------------
-- 上映スケジュールの動的ダミーデータ挿入 (Insert Dynamic Schedule Dummy Data)
-- 前提条件: movie テーブルに mno = 1, 2, 3 が存在すること
-- 実行日の日付 (CURDATE) を基準に、スケジュール時間を結合 (CONCAT) します。
-- --------------------------------------------------------

-- 1. 映画1番のスケジュール (本日)
INSERT INTO schedule (mno, tno, sdate) VALUES 
(1, 1, CONCAT(CURDATE(), ' 10:30:00')), -- 1館 / 午前
(1, 3, CONCAT(CURDATE(), ' 14:00:00')), -- Dolby Cinema / 午後
(1, 1, CONCAT(CURDATE(), ' 19:00:00')); -- 1館 / 夜間

-- 2. 映画2番のスケジュール (本日)
INSERT INTO schedule (mno, tno, sdate) VALUES 
(2, 2, CONCAT(CURDATE(), ' 11:15:00')), -- 2館 / 午前
(2, 2, CONCAT(CURDATE(), ' 15:30:00')); -- 2館 / 午後

-- 3. 映画3番のスケジュール (明日: DATE_ADD を使用)
INSERT INTO schedule (mno, tno, sdate) VALUES 
(3, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 19:30:00')), -- Dolby Cinema / 明日夜間
(3, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 22:00:00')); -- 1館 / レイトショー

-- --------------------------------------------------------
-- 2. 予約(決済)データの動的追加 (Reservation Data for Last 7 Days)
-- スクリプト実行日を基準に、過去7日間のデータを自動生成します。
-- --------------------------------------------------------

-- [本日: CURDATE()]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(1, 'test', 'A-1', 15000, 'PAID', CONCAT(CURDATE(), ' 10:00:00')),
(2, 'user01', 'B-2', 12000, 'PAID', CONCAT(CURDATE(), ' 14:30:00')),
(3, 'user02', 'C-3', 15000, 'PAID', CONCAT(CURDATE(), ' 18:00:00')),
(4, 'user03', 'D-4', 18000, 'CANCEL', CONCAT(CURDATE(), ' 20:15:00'));

-- [1日前: DATE_SUB(CURDATE(), INTERVAL 1 DAY)]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(4, 'user04', 'D-5', 18000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 10:20:00')),
(5, 'test', 'A-5', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:00')),
(6, 'user01', 'E-3', 20000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 17:30:00')),
(7, 'user02', 'F-4', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 21:15:00'));

-- [2日前: DATE_SUB(CURDATE(), INTERVAL 2 DAY)]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(7, 'test', 'F-3', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:00:00')),
(1, 'user01', 'G-4', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 12:45:00')),
(2, 'user02', 'H-5', 12000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 15:20:00')),
(3, 'user03', 'C-4', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:10:00'));

-- [3日前: DATE_SUB(CURDATE(), INTERVAL 3 DAY)]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(3, 'user01', 'C-1', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 11:30:00')),
(4, 'user02', 'D-1', 18000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 16:10:00')),
(5, 'user03', 'A-4', 15000, 'CANCEL', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 19:00:00')),
(6, 'user04', 'E-2', 20000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 22:30:00'));

-- [4日前: DATE_SUB(CURDATE(), INTERVAL 4 DAY)]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(6, 'user02', 'E-1', 20000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 10:15:00')),
(7, 'user03', 'F-2', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 14:50:00')),
(1, 'user04', 'G-3', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 18:30:00')),
(2, 'test', 'H-4', 12000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 20:45:00'));

-- [5日前: DATE_SUB(CURDATE(), INTERVAL 5 DAY)]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(2, 'user03', 'B-1', 12000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 09:30:00')),
(3, 'user04', 'C-2', 15000, 'CANCEL', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 13:20:00')),
(4, 'test', 'D-3', 18000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 17:40:00')),
(5, 'user01', 'A-3', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 21:00:00'));

-- [6日前: DATE_SUB(CURDATE(), INTERVAL 6 DAY)]
INSERT INTO reservation (sno, userid, seat_info, price, status, rdate) VALUES
(5, 'user04', 'A-2', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 11:00:00')),
(6, 'test', 'E-5', 20000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 15:45:00')),
(7, 'user01', 'F-6', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 19:20:00')),
(1, 'user02', 'G-7', 15000, 'PAID', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 22:10:00'));

COMMIT;

-- --------------------------------------------------------
-- 上映スケジュールの動的ダミーデータ挿入 (Dynamic Schedule Dummy Data)
-- 実行日(CURDATE)を基準に、本日〜3日後（計4日間）のスケジュールを自動生成します。
-- 全8作品(mno: 1~8)が3つのシアター(tno: 1,2,3)で上映される構成です。
-- --------------------------------------------------------

-- 1. 本日のスケジュール (Today: CURDATE)
INSERT INTO schedule (mno, tno, sdate) VALUES
(1, 1, CONCAT(CURDATE(), ' 10:30:00')), (1, 3, CONCAT(CURDATE(), ' 19:00:00')),
(2, 2, CONCAT(CURDATE(), ' 11:15:00')), (2, 2, CONCAT(CURDATE(), ' 15:30:00')),
(3, 3, CONCAT(CURDATE(), ' 13:00:00')), (3, 1, CONCAT(CURDATE(), ' 21:30:00')),
(4, 1, CONCAT(CURDATE(), ' 14:00:00')), (4, 3, CONCAT(CURDATE(), ' 22:15:00')),
(5, 2, CONCAT(CURDATE(), ' 09:30:00')), (5, 2, CONCAT(CURDATE(), ' 18:45:00')),
(6, 3, CONCAT(CURDATE(), ' 10:00:00')), (6, 1, CONCAT(CURDATE(), ' 16:30:00')),
(7, 1, CONCAT(CURDATE(), ' 12:45:00')), (7, 2, CONCAT(CURDATE(), ' 20:00:00')),
(8, 2, CONCAT(CURDATE(), ' 14:15:00')), (8, 3, CONCAT(CURDATE(), ' 17:00:00'));

-- 2. 1日後のスケジュール (Tomorrow: +1 DAY)
INSERT INTO schedule (mno, tno, sdate) VALUES
(1, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 09:30:00')), (1, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 15:00:00')),
(2, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 10:45:00')), (2, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 19:30:00')),
(3, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 12:00:00')), (3, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 20:15:00')),
(4, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 13:30:00')), (4, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 21:45:00')),
(5, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 11:15:00')), (5, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 17:30:00')),
(6, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 09:00:00')), (6, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 16:15:00')),
(7, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 14:45:00')), (7, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 22:00:00')),
(8, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 13:00:00')), (8, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY), ' 18:30:00'));

-- 3. 2日後のスケジュール (Day After Tomorrow: +2 DAY)
INSERT INTO schedule (mno, tno, sdate) VALUES
(1, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 11:00:00')), (1, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 20:30:00')),
(2, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 09:15:00')), (2, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 14:45:00')),
(3, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 12:30:00')), (3, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 18:15:00')),
(4, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 15:00:00')), (4, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 22:30:00')),
(5, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 10:30:00')), (5, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 16:00:00')),
(6, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 13:45:00')), (6, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 21:00:00')),
(7, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 09:45:00')), (7, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 19:15:00')),
(8, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 11:30:00')), (8, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 2 DAY), ' 17:45:00'));

-- 4. 3日後のスケジュール (3 Days Later: +3 DAY)
INSERT INTO schedule (mno, tno, sdate) VALUES
(1, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 13:15:00')), (1, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 21:15:00')),
(2, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 10:00:00')), (2, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 16:30:00')),
(3, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 14:30:00')), (3, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 19:45:00')),
(4, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 11:45:00')), (4, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 18:00:00')),
(5, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 12:15:00')), (5, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 20:45:00')),
(6, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 09:30:00')), (6, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 15:45:00')),
(7, 1, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 10:15:00')), (7, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 17:30:00')),
(8, 2, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 11:00:00')), (8, 3, CONCAT(DATE_ADD(CURDATE(), INTERVAL 3 DAY), ' 22:00:00'));

COMMIT;

-- --------------------------------------------------------
-- ポイント履歴(point_history)の動的ダミーデータ挿入
-- reservationテーブルの予約データと完全に同期する金融トランザクション履歴
-- 基本積立率: 1% (0.01)
-- --------------------------------------------------------

-- [事前準備] 7日前: 全ユーザーへの初期システムチャージ
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('test', 100000, 'システム初期ポイントチャージ', CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 00:00:00')),
('user01', 100000, 'システム初期ポイントチャージ', CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 00:00:00')),
('user02', 100000, 'システム初期ポイントチャージ', CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 00:00:00')),
('user03', 100000, 'システム初期ポイントチャージ', CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 00:00:00')),
('user04', 100000, 'システム初期ポイントチャージ', CONCAT(DATE_SUB(CURDATE(), INTERVAL 7 DAY), ' 00:00:00'));

-- [6日前: DATE_SUB(CURDATE(), INTERVAL 6 DAY)]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('user04', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 11:00:00')),
('user04', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 11:00:01')),
('test', -20000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 15:45:00')),
('test', 200, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 15:45:01')),
('user01', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 19:20:00')),
('user01', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 19:20:01')),
('user02', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 22:10:00')),
('user02', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), ' 22:10:01'));

-- [5日前: DATE_SUB(CURDATE(), INTERVAL 5 DAY)]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('user03', -12000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 09:30:00')),
('user03', 120, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 09:30:01')),
('user04', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 13:20:00')),
('user04', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 13:20:01')),
('user04', 15000, '予約キャンセルに伴うポイント返還', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 13:30:00')), -- Cancel Refund
('user04', -150, '予約キャンセルに伴うリワード回収', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 13:30:01')),
('test', -18000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 17:40:00')),
('test', 180, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 17:40:01')),
('user01', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 21:00:00')),
('user01', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), ' 21:00:01'));

-- [4日前: DATE_SUB(CURDATE(), INTERVAL 4 DAY)]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('user02', -20000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 10:15:00')),
('user02', 200, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 10:15:01')),
('user03', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 14:50:00')),
('user03', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 14:50:01')),
('user04', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 18:30:00')),
('user04', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 18:30:01')),
('test', -12000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 20:45:00')),
('test', 120, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 4 DAY), ' 20:45:01'));

-- [3日前: DATE_SUB(CURDATE(), INTERVAL 3 DAY)]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('user01', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 11:30:00')),
('user01', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 11:30:01')),
('user02', -18000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 16:10:00')),
('user02', 180, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 16:10:01')),
('user03', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 19:00:00')),
('user03', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 19:00:01')),
('user03', 15000, '予約キャンセルに伴うポイント返還', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 19:10:00')), -- Cancel Refund
('user03', -150, '予約キャンセルに伴うリワード回収', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 19:10:01')),
('user04', -20000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 22:30:00')),
('user04', 200, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), ' 22:30:01'));

-- [2日前: DATE_SUB(CURDATE(), INTERVAL 2 DAY)]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('test', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:00:00')),
('test', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 09:00:01')),
('user01', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 12:45:00')),
('user01', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 12:45:01')),
('user02', -12000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 15:20:00')),
('user02', 120, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 15:20:01')),
('user03', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:10:00')),
('user03', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), ' 20:10:01'));

-- [1日前: DATE_SUB(CURDATE(), INTERVAL 1 DAY)]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('user04', -18000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 10:20:00')),
('user04', 180, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 10:20:01')),
('test', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:00')),
('test', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 14:00:01')),
('user01', -20000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 17:30:00')),
('user01', 200, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 17:30:01')),
('user02', -15000, '映画チケット決済', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 21:15:00')),
('user02', 150, 'チケット決済に伴うリワード積立', CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 21:15:01'));

-- [本日: CURDATE()]
INSERT INTO point_history (userid, amount, description, regdate) VALUES
('test', -15000, '映画チケット決済', CONCAT(CURDATE(), ' 10:00:00')),
('test', 150, 'チケット決済に伴うリワード積立', CONCAT(CURDATE(), ' 10:00:01')),
('user01', -12000, '映画チケット決済', CONCAT(CURDATE(), ' 14:30:00')),
('user01', 120, 'チケット決済に伴うリワード積立', CONCAT(CURDATE(), ' 14:30:01')),
('user02', -15000, '映画チケット決済', CONCAT(CURDATE(), ' 18:00:00')),
('user02', 150, 'チケット決済に伴うリワード積立', CONCAT(CURDATE(), ' 18:00:01')),
('user03', -18000, '映画チケット決済', CONCAT(CURDATE(), ' 20:15:00')),
('user03', 180, 'チケット決済に伴うリワード積立', CONCAT(CURDATE(), ' 20:15:01')),
('user03', 18000, '予約キャンセルに伴うポイント返還', CONCAT(CURDATE(), ' 20:25:00')), -- Cancel Refund
('user03', -180, '予約キャンセルに伴うリワード回収', CONCAT(CURDATE(), ' 20:25:01'));

COMMIT;reservation