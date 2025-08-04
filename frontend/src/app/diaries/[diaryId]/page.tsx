"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { Diary, DiaryInfoProps, Comment, Content } from "../types/detail";
import DiaryTitle from "./components/DiaryTitle";
import DiaryInfo from "./components/DiaryInfo";
import CommentForm from "./components/CommentForm";
import CommentInfo from "./components/CommentInfo";
import ContentInfo from "./components/ContentInfo";

export default function Page() {
  const [diary, setDiary] = useState<Diary | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [content, setContent] = useState<Content | null>(null);

  const { diaryId } = useParams();
  const searchParams = useSearchParams();
  const router = useRouter();

  async function fetchDiary() {
    try {
      const res = await fetch(`http://localhost:8080/api/v1/diaries/${diaryId}`);
      if (!res.ok) throw new Error("Failed to fetch Diary");
      const json = await res.json();
      setDiary(json.data);
    } catch (err) {
      console.error(err);
    }
  }

  async function fetchComments() {
    try {
      const res = await fetch(`http://localhost:8080/api/v1/comments/${diaryId}`);
      if (!res.ok) throw new Error("Failed to fetch comments");
      const json = await res.json();
      setComments(json.data);
    } catch (err) {
      console.error(err);
    }
  }

  async function fetchContent() {
    try {
      const res = await fetch(`http://localhost:8080/api/v1/contents/${diaryId}`);
      if (!res.ok) throw new Error("Failed to fetch content");
      const json = await res.json();
      setContent(json.data);
    } catch (err) {
      console.error(err);
    }
  }

  useEffect(() => {
    if (!diaryId) return;
    
    setLoading(true);
    Promise.all([fetchDiary(), fetchComments(), fetchContent()])
      .finally(() => setLoading(false));
  }, [diaryId]);

  // refresh 파라미터 처리
  useEffect(() => {
    const shouldRefresh = searchParams.get("refresh") === "1";
    if (shouldRefresh) {
      // URL에서 refresh 파라미터 제거
      const newUrl = new URL(window.location.href);
      newUrl.searchParams.delete("refresh");
      window.history.replaceState({}, "", newUrl.toString());
      
      // 강제로 다시 fetch
      setLoading(true);
      Promise.all([fetchDiary(), fetchComments(), fetchContent()])
        .finally(() => setLoading(false));
    }
  }, [searchParams]);

  const handleCommentAdd = (newComment: Comment) => {
    setComments((prev) => [newComment, ...prev]);
  };

  const handleDelete = async () => {
    const confirmed = confirm("정말 삭제하시겠습니까?");
    if (!confirmed) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/v1/diaries/${diaryId}`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );

      if (!res.ok) throw new Error("삭제 실패");

      alert("삭제 완료!");
      router.push("/");
    } catch (err) {
      console.error(err);
      alert("삭제 중 오류 발생");
    }
  };

  if (loading) {
    return <main className="p-6 text-center">로딩 중...</main>;
  }

  if (!diary) {
    return (
      <main className="flex flex-col items-center justify-center h-[60vh] text-center space-y-4">
        <div className="text-6xl">😢</div>
        <div className="text-xl font-semibold text-gray-700">
          존재하지 않는 페이지입니다.
        </div>
        <div className="text-gray-500">
          주소가 잘못 입력되었거나, 삭제된 일기일 수 있어요.
        </div>
        <button
          onClick={() => router.push("/")}
          className="mt-4 px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-100"
        >
          홈으로 이동
        </button>
      </main>
    );
  }

  return (
    <main className="max-w-3xl mx-auto p-6 space-y-10">
      <div className="flex justify-end gap-2">
        <button
          onClick={() => router.push(`/diaries/${diaryId}/edit`)}
          className="px-4 py-2 border rounded hover:bg-gray-100"
        >
          수정
        </button>
        <button
          onClick={handleDelete}
          className="px-4 py-2 border rounded text-red-500 hover:bg-red-50"
        >
          삭제
        </button>
      </div>

      <DiaryTitle title={diary.title} />
      {content && (
        <ContentInfo
          content={content}
          genreNames={diary.genreNames}
          ottNames={diary.ottNames.slice(0, 1)} 
        />
      )}
      <DiaryInfo
        rating={diary.rating}
        contentText={diary.contentText}
        tagNames={diary.tagNames}
      />
      <CommentForm
        diaryId={Number(diaryId ?? 1)}
        onCommentAdd={handleCommentAdd}
      />
      <CommentInfo comments={comments} setComments={setComments} />
    </main>
  );
}