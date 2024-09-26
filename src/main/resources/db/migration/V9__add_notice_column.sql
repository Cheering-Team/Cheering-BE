ALTER TABLE notice_tb
ADD COLUMN image VARCHAR(512),
ADD COLUMN content VARCHAR(1000);

UPDATE notice_tb
SET image = "https://cheering-bucket.s3.ap-northeast-2.amazonaws.com/player-apply-3.png",
    content = E'안녕하세요.\n스타디움입니다.\n\n내가 응원하고 싶은 선수가 아직 등록되어 있지 않다면 아래의 양식에 맞춰서 신청해주세요.\n\n리그명은 생략가능하며, 배경사진은 직접 찍은 사진으로 등록해주세요.\n\n 감사합니다.'
WHERE notice_id = 1;