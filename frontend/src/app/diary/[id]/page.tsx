"use client";

import { useEffect, useState } from "react";
import { Diary, DiaryInfoProps } from "../types/detail";
{
  /* 페이지 타이틀 */
}
function DiaryTitle({ title }: { title: string }) {
  return (
    <h1 className="text-center text-4xl font-bold text-gray-800">{title}</h1>
  );
}

function ContentInfo() {
  return (
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
  );
}

function DiaryInfo({ rating, contentText, tagNames }: DiaryInfoProps) {
  return (
    <section className="p-6 border rounded-xl shadow-sm bg-white space-y-4">
      <header className="flex flex-col gap-1">
        <div className="text-yellow-500 text-xl">
          ⭐️⭐️⭐️⭐️⭐️ {rating} / 5.0
        </div>
      </header>
      <p className="text-gray-800">{contentText}</p>
      <div className="flex gap-2">
        {tagNames.map((tag, index) => (
          <span
            key={index}
            className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm"
          >
            #{tag}
          </span>
        ))}
      </div>
    </section>
  );
}

function CommentForm() {
  const [content, setContent] = useState("");
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/v1/comments", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          diaryId: 1, // 실제로는 props나 route param으로 받아올 예정
          content,
        }),
      });

      if (!response.ok) {
        throw new Error("댓글 등록 실패");
      }

      const result = await response.json();
      alert("댓글 등록에 성공하였습니다.");
      console.log("댓글 등록 성공:", result);

      // 입력 초기화
      setContent("");
    } catch (error) {
      console.error(error);
      alert("댓글 등록 중 오류가 발생했습니다.");
    }
  };

  return (
    <section className="p-6 border rounded-xl shadow-sm bg-white space-y-4">
      <form className="flex flex-col gap-3" onSubmit={handleSubmit}>
        <label className="text-sm text-gray-600">Nickname</label>
        <textarea
          className="border p-2 rounded-md h-24 resize-none"
          name="content"
          placeholder="댓글을 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <button
          type="submit"
          className="self-end px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600"
        >
          등록
        </button>
      </form>
    </section>
  );
}

function CommentInfo() {
  return (
    <section className="space-y-4">
      <div className="p-4 border rounded-md bg-white shadow-sm">
        <h2>댓글</h2>

        {/* 말풍선 형태로 수정된 comment_content */}
        <div className="relative max-w-full md:max-w-[80%]">
          <div className="bg-gray-100 text-gray-800 p-4 rounded-xl shadow-md relative">
            <p>comment_content</p>
            <div className="absolute -left-2 top-4 w-0 h-0 border-t-8 border-b-8 border-r-8 border-t-transparent border-b-transparent border-r-gray-100"></div>
          </div>
        </div>

        <div className="text-sm text-gray-500 mt-2 flex gap-2">
          <span>👤 profile</span>
          <span>nickname</span>
          <span> date</span>
        </div>
      </div>
    </section>
  );
}

export default function Page() {
  const [diary, setDiary] = useState<Diary | null>(null);
  const [loading, setLoading] = useState(true);
  const diaryId = 1; // 나중에 pathVariable로 받아올 예정

  useEffect(() => {
    async function fetchDiary() {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/diaries/${diaryId}`
        );
        if (!res.ok) {
          throw new Error("Failed to fetch Diary");
        }
        const json = await res.json();
        setDiary(json.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }

    fetchDiary();
  }, [diaryId]);

  if (loading) {
    return <main className="p-6 text-center">로딩 중...</main>;
  }

  if (!diary) {
    return (
      <main className="p-6 text-center text-red-500">
        데이터를 불러오지 못했습니다.
      </main>
    );
  }

  return (
    <main className="max-w-3xl mx-auto p-6 space-y-10">
      <DiaryTitle title={diary.title} />
      <ContentInfo />
      <DiaryInfo
        rating={diary.rating}
        contentText={diary.contentText}
        tagNames={diary.tagNames}
      />
      <CommentForm />
      <CommentInfo />
    </main>
  );
}
