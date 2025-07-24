"use client";

export default function page() {
  return (
    <main className="max-w-3xl mx-auto p-6 space-y-10">
      {/* 페이지 타이틀 */}
      <h1 className="text-center text-4xl font-bold text-gray-800">Title</h1>

      <section className="border rounded-xl p-6 shadow-sm bg-white">
        <div className="flex flex-col md:flex-row items-center gap-8">
          {/* 포스터 영역 */}
          <div className="w-full md:w-1/2">
            <div className="aspect-[16/9] bg-gray-200 rounded-lg shadow-sm flex items-center justify-center text-gray-400 text-lg">
              포스터 이미지
            </div>
          </div>

          {/* 텍스트 정보 */}
          <div className="w-full md:w-1/2 space-y-4">
            <h2 className="text-2xl font-semibold text-gray-800">
              content_title
            </h2>
            <p className="text-gray-700 leading-relaxed">content_description</p>
            <div className="text-sm text-gray-500">
              출시일: content_released_at
            </div>
          </div>
        </div>
      </section>

      {/* 사용자 감상일기 */}
      <section className="p-6 border rounded-xl shadow-sm bg-white space-y-4">
        <header className="flex flex-col gap-1">
          <div className="text-yellow-500 text-xl">
            ⭐️⭐️⭐️⭐️⭐️ 5.0 / 5.0
          </div>
        </header>
        <p className="text-gray-800">
          해당 작품은 명작이네요. 여러분도 꼭 보시길 바랍니다.
        </p>
        <div className="flex gap-2">
          <span className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm">
            #명작
          </span>
          <span className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm">
            #감동
          </span>
        </div>
      </section>

      {/* 댓글 작성 폼 */}
      <section className="p-6 border rounded-xl shadow-sm bg-white space-y-4">
        <form className="flex flex-col gap-3">
          <label className="text-sm text-gray-600">Nickname</label>
          <textarea
            className="border p-2 rounded-md h-24 resize-none"
            name="content"
            placeholder="댓글을 입력하세요"
          />
          <button
            type="submit"
            className="self-end px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
          >
            등록
          </button>
        </form>
      </section>

      {/* 댓글 목록 */}
      <section className="space-y-4">
        <div className="p-4 border rounded-md bg-white shadow-sm">
          <h2>댓글</h2>

          {/* 말풍선 형태로 수정된 comment_content */}
          <div className="relative max-w-full md:max-w-[80%]">
            <div className="bg-gray-100 text-gray-800 p-4 rounded-xl shadow-md relative">
              <p>comment_content</p>
              {/* 말풍선 꼬리 */}
              <div className="absolute -left-2 top-4 w-0 h-0 border-t-8 border-b-8 border-r-8 border-t-transparent border-b-transparent border-r-gray-100"></div>
            </div>
          </div>

          {/* 작성자 정보 */}
          <div className="text-sm text-gray-500 mt-2 flex gap-2">
            <span>👤 profile</span>
            <span>nickname</span>
            <span> date</span>
          </div>
        </div>
        {/* 여러 개의 댓글이 있다면 여기에 반복 렌더링 */}
      </section>
    </main>
  );
}
