import { useState } from "react";
import CommentMenuButton from "./CommentMenuButton";
import { Comment } from "../../types/detail";
import { useRouter } from "next/navigation";

export default function CommentInfo({
  comments,
  setComments,
}: {
  comments: Comment[];
  setComments: React.Dispatch<React.SetStateAction<Comment[]>>;
}) {
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState("");
  const router = useRouter();

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
        <div className="space-y-4">
          {comments.map((comment) => (
            <div
              key={comment.id}
              className="flex items-start gap-3 bg-white border rounded-lg p-4 shadow-sm"
            >
              {/* 프로필 이미지 */}

              <img
                src={comment.profileImageUrl}
                alt="프로필 이미지"
                className="w-8 h-8 rounded-full object-cover cursor-pointer"
                title={`${comment.nickname}님의 프로필로 이동`}
                onClick={() => router.push(`/profile/${comment.userId}`)}
              />

              {/* 댓글 본문 */}
              <div className="flex-1">
                {/* 닉네임 + 메뉴 */}
                <div className="flex justify-between items-center">
                  <span
                    className="font-semibold text-gray-800 cursor-pointer"
                    title={`${comment.nickname}님의 프로필로 이동`}
                    onClick={() => router.push(`/profile/${comment.userId}`)}
                  >
                    {comment.nickname}
                  </span>
                  <CommentMenuButton
                    onEdit={() => handleEdit(comment)}
                    onDelete={() => handleDelete(comment.id)}
                  />
                </div>

                {/* 댓글 내용 or 수정 폼 */}
                <div className="bg-gray-100 rounded-xl px-4 py-2 mt-1 text-gray-800 whitespace-pre-wrap">
                  {editingId === comment.id ? (
                    <form onSubmit={(e) => handleUpdate(e, comment.id)}>
                      <textarea
                        className="w-full p-2 border rounded"
                        value={editContent}
                        onChange={(e) => setEditContent(e.target.value)}
                      />
                      <div className="mt-2 flex justify-end gap-2">
                        <button
                          type="button"
                          onClick={handleCancel}
                          className="text-sm text-gray-500"
                        >
                          취소
                        </button>
                        <button
                          type="submit"
                          className="text-sm text-blue-500 font-medium"
                        >
                          저장
                        </button>
                      </div>
                    </form>
                  ) : (
                    <p>{comment.content}</p>
                  )}
                </div>

                {/* 작성일 */}
                <div className="text-xs text-gray-400 mt-1">
                  {new Date(comment.createdAt).toLocaleString()}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
