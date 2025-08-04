"use client";

import { useEffect, useState } from "react";
import { Diary, DiaryInfoProps, Comment, Content } from "../types/detail";
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

    async function fetchComments() {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/comments/${diaryId}`
        );
        if (!res.ok) throw new Error("Failed to fetch comments");

        const json = await res.json();
        setComments(json.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }

    async function fetchContent() {
      try {
        const res = await fetch(
          `http://localhost:8080/api/v1/contents/${diaryId}`
        );
        if (!res.ok) throw new Error("Failed to fetch content");
        const json = await res.json();
        setContent(json.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }

    fetchDiary();
    fetchComments();
    fetchContent();
  }, [diaryId]);

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
          credentials: "include", // 쿠키 인증 대비
        }
      );

      if (!res.ok) {
        throw new Error("삭제 실패");
      }

      alert("삭제 완료!");
      router.push("/"); // 홈으로 -> userId 받아오게 되면 프로필 이동으로..
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
          ottNames={diary.ottNames}
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
