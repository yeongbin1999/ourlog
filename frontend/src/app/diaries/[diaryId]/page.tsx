"use client";

import { useEffect, useState, useRef } from "react";
import { Diary, DiaryInfoProps, Comment, Content } from "../types/detail";
import { useParams } from "next/navigation";
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
        console.log(json.data);
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
