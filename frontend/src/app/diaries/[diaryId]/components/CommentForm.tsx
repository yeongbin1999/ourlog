import { useState } from "react";
import { Comment } from "../../types/detail";

export default function CommentForm({
  diaryId,
  onCommentAdd,
}: {
  diaryId: number;
  onCommentAdd: (newComment: Comment) => void;
}) {
  const [content, setContent] = useState("");

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!content.trim()) {
      alert("댓글 내용을 입력해주세요");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/v1/comments", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ diaryId, content }),
      });

      if (!response.ok) throw new Error("댓글 등록 실패");

      const result = await response.json();
      setContent("");
      onCommentAdd(result.data);
    } catch (error) {
      console.error(error);
      alert("댓글 등록 중 오류가 발생했습니다.");
    }
  };

  return (
    <section className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 space-y-4">
      <h2 className="text-lg font-semibold text-gray-900">댓글 작성</h2>
      <form onSubmit={handleSubmit} className="space-y-3">
        <textarea
          className="w-full border border-gray-300 rounded-xl p-4 h-28 resize-none text-sm focus:outline-none focus:ring-2 focus:ring-gray-300"
          placeholder="댓글을 입력하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
        <div className="flex justify-end">
          <button
            type="submit"
            className="bg-gray-900 text-white px-6 py-2 rounded-xl text-sm font-medium hover:bg-gray-800 transition"
          >
            등록
          </button>
        </div>
      </form>
    </section>
  );
}
