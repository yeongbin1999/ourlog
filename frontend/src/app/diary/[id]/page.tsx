"use client";

export default function page() {
  return (
    <>
      {/* 컨텐츠 정보 */}
      <h1>Title</h1>
      <div>content_title</div>
      <div>content_description</div>
      <div>content_released_at</div>

      {/* 사용자 감상일기(평가) */}
      <div>별점</div>
      <div>⭐️⭐️⭐️⭐️⭐️ 5.0/5.0</div>
      <div>해당 작품은 명작이네요 여러분도 꼭 보시길 바랍니다.</div>
      <span>명작 </span>
      <span>감동 </span>

      {/* 댓글 작성 폼 */}
      <form>
        <div>Nickname</div>
        <textarea className="border-1" name="content"></textarea>
        <button className="border-1">등록</button>
      </form>

      {/* 해당 감상일기의 댓글 조회 */}
      <div>comment_content</div>
      <span>profile </span>
      <span>nickname </span>
      <span>date</span>
    </>
  );
}
