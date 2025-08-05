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
    async function fetchData() {
      try {
        const [diaryRes, commentsRes, contentRes] = await Promise.all([
          fetch(`http://localhost:8080/api/v1/diaries/${diaryId}`),
          fetch(`http://localhost:8080/api/v1/comments/${diaryId}`),
          fetch(`http://localhost:8080/api/v1/contents/${diaryId}`),
        ]);

        if (!diaryRes.ok || !commentsRes.ok || !contentRes.ok)
          throw new Error("데이터 로딩 실패");

        const diaryData = await diaryRes.json();
        const commentsData = await commentsRes.json();
        const contentData = await contentRes.json();

        setDiary(diaryData.data);
        setComments(commentsData.data);
        setContent(contentData.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }

    fetchData();
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
      const res = await fetch(`http://localhost:8080/api/v1/diaries/${diaryId}`, {
        method: "DELETE",
        credentials: "include",
      });

      if (!res.ok) throw new Error("삭제 실패");

      alert("삭제 완료!");
      router.push("/");
    } catch (err) {
      console.error(err);
      alert("삭제 중 오류 발생");
    }
  };

  if (loading) {
    return (
      <main className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 mx-auto mb-4"></div>
          <p className="text-gray-600 font-medium">로딩 중...</p>
        </div>
      </main>
    );
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
    <main className="bg-gray-50 min-h-screen py-8 lg:py-12">
      <div className="max-w-5xl mx-auto px-4 lg:px-6 space-y-8">
        <DiaryTitle title={diary.title} />

        {content && (
          <ContentInfo
            content={content}
            genreNames={diary.genreNames}
            ottNames={diary.ottNames}
          />
        )}

        <DiaryInfo
          rating={diary.rating}
          contentText={diary.contentText}
          tagNames={diary.tagNames}
          onEdit={() => router.push(`/diaries/${diaryId}/edit`)}
          onDelete={handleDelete}
        />

        {/* 댓글 섹션 */}
        <div className="bg-white border border-gray-200 rounded-3xl shadow-sm overflow-hidden">
          <div className="p-8">
            <div className="flex items-center gap-3 mb-8">
              <h2 className="text-2xl font-bold text-gray-900">댓글</h2>
              <span className="bg-gray-100 text-gray-600 px-3 py-1 rounded-full text-sm font-medium">
                {comments.length}
              </span>
            </div>
            <CommentInfo comments={comments} setComments={setComments} />
          </div>
        </div>

        <CommentForm diaryId={Number(diaryId)} onCommentAdd={handleCommentAdd} />
      </div>
    </main>
  );
}