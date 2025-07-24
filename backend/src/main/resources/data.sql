-- USERS
INSERT INTO users (id, email, password, nickname, role, created_at, updated_at)
VALUES
(1, 'user1@ourlog.com', '1234', '영화러버', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 'user2@ourlog.com', '1234', '북덕후', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, 'user3@ourlog.com', '1234', '음덕이', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(4, 'user4@ourlog.com', '1234', '드라마쟁이', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(5, 'user5@ourlog.com', '1234', '혼종유저', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(6, 'user6@ourlog.com', '1234', '혼종유저', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(7, 'user5432ourlog.com', '1234', '이런', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(8, 'user54@ourlog.com', '1234', '혼', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(9, 'user523@ourlog.com', '1234', '혼유저', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(10, 'user2345@ourlog.com', '1234', '혼유저', 'USER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- CONTENTS
INSERT INTO content (id, title, type, description, poster_url, created_at, updated_at)
VALUES
(1, '인셉션', 'MOVIE', '놀란의 명작', 'https://image.com/inception.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, '해리포터와 마법사의 돌', 'BOOK', '마법의 시작', 'https://image.com/harrypotter.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, 'IU - Love Poem', 'MUSIC', '감성적인 음악', 'https://image.com/lovepoem.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(4, '더 글로리', 'MOVIE', '복수극의 정석', 'https://image.com/theglory.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(5, '어바웃 타임', 'MOVIE', '시간여행 로맨스', 'https://image.com/abouttime.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(6, '더 글로리', 'MOVIE', '복수극의 정석', 'https://image.com/theglory.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(7, '더 리', 'MOVIE', '복수의 정석', 'https://image.com/theglory.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(8, '호야', 'MOVIE', '복극의 정석', 'https://image.com/theglory.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(9, ' 글로리', 'MOVIE', '복의 정석', 'https://image.com/theglory.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(10, '리', 'MOVIE', '복수극의 석', 'https://image.com/theglory.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- DIARY (공개)
INSERT INTO diary (id, user_id, content_id, title, content_text, rating, is_public, created_at, updated_at)
VALUES
(1, 1, 1, '인셉션 감상평', '드림 속 드림의 미학', 4.5, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 2, 2, '해리포터 독서일지', '호그와트 입학하고 싶다', 5.0, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(3, 3, 3, 'IU 노래 듣기', '감성에 젖는다...', 4.8, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(4, 4, 4, '더 글로리 후기', '복수는 성공했을까?', 4.2, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(5, 5, 5, '어바웃 타임 후기', '이 영화는 사랑이다', 5.0, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(6, 4, 4, '더 ', '복수는 성공했을까?', 4.2, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(7, 4, 4, '더 기', '복수는 성공했을까?', 4.2, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(8, 4, 4, '더 글기', '복수는 성공했을까?', 4.2, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(9, 4, 4, '더 글리 후기', '복수는 성공했을까?', 4.2, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(10, 4, 4, '더 글로리 기', '복수는 성공했을까?', 4.2, true, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
