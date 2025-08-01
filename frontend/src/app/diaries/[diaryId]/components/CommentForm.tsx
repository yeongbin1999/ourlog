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
      // 상태 업데이트
      onCommentAdd(result.data);

      alert("댓글 등록에 성공하였습니다.");
    } catch (error) {
      console.error(error);
      alert("댓글 등록 중 오류가 발생했습니다.");
    }
  };

  return (
    <>
      <h2 className="text-xl font-semibold">댓글</h2>
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
    </>
  );
}
