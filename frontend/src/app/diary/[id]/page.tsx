"use client";

import { useEffect, useState } from "react";
import { Diary, DiaryInfoProps, Comment, Content } from "../types/detail";
import { FaStar, FaRegStar } from "react-icons/fa"; // 꽉 찬 별, 빈 별

{
  /* 페이지 타이틀 */
}
function DiaryTitle({ title }: { title: string }) {
  return (
    <h1 className="text-center text-4xl font-bold text-gray-800">{title}</h1>
  );
}

function ContentInfo({ content }: { content: Content }) {
  return (
    <section className="border rounded-xl p-6 shadow-sm bg-white">
      <div className="flex flex-col md:flex-row items-center gap-8">
        {/* 포스터 영역 */}
        <div className="w-full md:w-1/2">
          <div className="aspect-[16/9] bg-gray-200 rounded-lg shadow-sm flex items-center justify-center text-gray-400 text-lg overflow-hidden">
            {content.posterUrl ? (
              <img
                src={content.posterUrl}
                alt="포스터 이미지"
                className="w-full h-full object-cover rounded-lg"
              />
            ) : (
              "포스터 이미지 없음"
            )}
          </div>
        </div>

        {/* 텍스트 정보 */}
        <div className="w-full md:w-1/2 space-y-4">
          <h2 className="text-2xl font-semibold text-gray-800">
            {content.title}
          </h2>
          <p className="text-gray-700 leading-relaxed">{content.description}</p>
          <div className="text-sm text-gray-500">
            출시일: {new Date(content.releasedAt).toLocaleDateString()}
          </div>
        </div>
      </div>
    </section>
  );
}

function DiaryInfo({ rating, contentText, tagNames }: DiaryInfoProps) {
  const stars = Array.from({ length: 5 }, (_, i) =>
    i < rating ? (
      <FaStar key={i} className="text-yellow-400" />
    ) : (
      <FaRegStar key={i} className="text-gray-300" />
    )
  );

  return (
    <section className="p-6 border rounded-xl shadow-sm bg-white space-y-4">
      <header className="flex items-center gap-2">
        <div className="flex items-center gap-1">{stars}</div>
        <div className="text-yellow-500 text-xl">{rating} / 5.0</div>
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

function CommentForm({
  diaryId,
  onCommentAdd,
}: {
  diaryId: number;
  onCommentAdd: (newComment: Comment) => void;
}) {
  const [content, setContent] = useState("");
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/api/v1/comments", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ diaryId, content }),
      });

      if (!response.ok) {
        throw new Error("댓글 등록 실패");
      }

      const result = await response.json();

      // 입력 초기화
      setContent("");
      console.log(result.data);
      // 부모 상태 업데이트
      onCommentAdd(result.data);

      alert("댓글 등록에 성공하였습니다.");
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

function CommentInfo({ comments }: { comments: Comment[] }) {
  return (
    <section className="space-y-4">
      <h2 className="text-xl font-semibold">댓글</h2>
      {comments.length === 0 ? (
        <p className="text-gray-500">등록된 댓글이 없습니다.</p>
      ) : (
        <div className="border rounded-md bg-white shadow-sm">
          {comments.map((comment) => (
            <div key={comment.id} className="p-4">
              <div className="bg-gray-100 text-gray-800 p-4 rounded-xl relative max-w-full md:max-w-[80%]">
                <p>{comment.content}</p>
                <div className="absolute -left-2 top-4 w-0 h-0 border-t-8 border-b-8 border-r-8 border-t-transparent border-b-transparent border-r-gray-100"></div>
              </div>
              <div className="text-sm text-gray-500 mt-2 flex gap-2">
                <span>{comment.profileImageUrl}</span>
                <span>{comment.nickname}</span>
                <span>{new Date(comment.createdAt).toLocaleString()}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}

export default function Page() {
  const [diary, setDiary] = useState<Diary | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [content, setContent] = useState<Content | null>(null);
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
    setComments((prev) => [...prev, newComment]);
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
      {content && <ContentInfo content={content} />}
      <DiaryInfo
        rating={diary.rating}
        contentText={diary.contentText}
        tagNames={diary.tagNames}
      />
      <CommentForm diaryId={diaryId} onCommentAdd={handleCommentAdd} />
      <CommentInfo comments={comments} />
    </main>
  );
}
