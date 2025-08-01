import { useState } from "react";
import CommentMenuButton from "./CommentMenuButton";
import { Comment } from "../../types/detail";

export default function CommentInfo({
  comments,
  setComments,
}: {
  comments: Comment[];
  setComments: React.Dispatch<React.SetStateAction<Comment[]>>;
}) {
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState("");

  const handleEdit = (comment: Comment) => {
    setEditingId(comment.id);
    setEditContent(comment.content);
  };

  const handleCancel = () => {
    setEditingId(null);
    setEditContent("");
  };

  const handleDelete = async (commentId: number) => {
    if (!confirm("정말 삭제하시겠습니까?")) return;
    // 삭제 API 요청 후 상태 갱신

    try {
      const res = await fetch(
        `http://localhost:8080/api/v1/comments/${commentId}`,
        {
          method: "DELETE",
        }
      );
      if (!res.ok) throw new Error("Failed to delete comment");
      const json = await res.json();
      console.log(json.data);
      setComments((prev) => prev.filter((comment) => comment.id !== commentId));
    } catch (err) {
      console.error(err);
    }
  };

  const handleUpdate = async (e: React.FormEvent, id: number) => {
    e.preventDefault();
    // 수정 API 요청 후 상태 갱신

    try {
      const res = await fetch("http://localhost:8080/api/v1/comments", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ id, content: editContent }),
      });

      if (!res.ok) throw new Error("댓글 수정 실패");

      setComments((prev) =>
        prev.map((comment) =>
          comment.id === id ? { ...comment, content: editContent } : comment
        )
      );

      setEditingId(null);
      setEditContent("");
    } catch (error) {
      console.error(error);
      alert("댓글 수정 중 오류가 발생했습니다.");
    }
  };

  return (
    <section className="space-y-4">
      {comments.length === 0 ? (
        <p className="text-gray-500">등록된 댓글이 없습니다.</p>
      ) : (
        <div className="border rounded-md bg-white shadow-sm">
          {comments.map((comment) => (
            <div key={comment.id} className="p-4 group relative">
              <div className="bg-gray-100 text-gray-800 p-4 rounded-xl relative max-w-full md:max-w-[80%]">
                {editingId === comment.id ? (
                  <form onSubmit={(e) => handleUpdate(e, comment.id)}>
                    <textarea
                      className="w-full p-2 border rounded"
                      value={editContent}
                      onChange={(e) => setEditContent(e.target.value)}
                    />
                    <div className="mt-2 flex justify-end gap-2">
                      <button type="button" onClick={handleCancel}>
                        취소
                      </button>
                      <button type="submit" className="text-blue-500">
                        저장
                      </button>
                    </div>
                  </form>
                ) : (
                  <p>{comment.content}</p>
                )}
                <div className="absolute -left-2 top-4 w-0 h-0 border-t-8 border-b-8 border-r-8 border-t-transparent border-b-transparent border-r-gray-100" />
              </div>

              <div className="text-sm text-gray-500 mt-2 flex gap-2">
                <span>{comment.nickname}</span>
                <span>{new Date(comment.createdAt).toLocaleString()}</span>
                <span>
                  <CommentMenuButton
                    onEdit={() => handleEdit(comment)}
                    onDelete={() => handleDelete(comment.id)}
                  />
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
