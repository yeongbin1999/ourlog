"use client";

import { useEffect, useState } from "react";
import { Diary, Comment, Content } from "../types/detail";
import { useParams, useRouter } from "next/navigation";
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
  const router = useRouter();

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
  }, [diaryId]);

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
      <main className="min-h-screen bg-[#f9fafb] flex items-center justify-center text-gray-500 text-sm">
        로딩 중...
      </main>
    );
  }

  if (!diary) {
    return (
      <main className="min-h-screen bg-[#f9fafb] flex items-center justify-center text-red-500">
        데이터를 불러오지 못했습니다.
      </main>
    );
  }

  return (
    <main className="bg-[#f9fafb] min-h-screen py-12 px-4">
      <div className="max-w-4xl mx-auto space-y-6">
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

        <section className="space-y-4">
          <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 space-y-4">
            <h2 className="text-lg font-semibold text-gray-900">댓글</h2>
            <CommentInfo comments={comments} setComments={setComments} />
          </div>

          <CommentForm diaryId={Number(diaryId)} onCommentAdd={handleCommentAdd} />
        </section>
      </div>
    </main>
  );
}
