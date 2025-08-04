import { DiaryInfoProps } from "../../types/detail";

export default function DiaryInfo({
  rating,
  contentText,
  tagNames,
  onEdit,
  onDelete,
}: DiaryInfoProps & {
  onEdit: () => void;
  onDelete: () => void;
}) {
  return (
    <section className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8 space-y-6">
      {/* 내용 텍스트 */}
      <div className="text-gray-800 text-base leading-relaxed whitespace-pre-line">
        {contentText}
      </div>

      {/* 태그 */}
      {tagNames.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {tagNames.map((tag, index) => (
            <span
              key={`tag-${index}`}
              className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-sm"
            >
              #{tag}
            </span>
          ))}
        </div>
      )}

      {/* 별점 (읽기 전용) */}
      <div className="flex items-center gap-3">
        <div className="flex items-center gap-1">
          {[1, 2, 3, 4, 5].map((value) => (
            <span
              key={value}
              className={`text-3xl ${
                value <= rating ? "text-yellow-400" : "text-gray-300"
              }`}
            >
              ★
            </span>
          ))}
        </div>
        <div className="text-base text-gray-600 font-medium">
          {rating.toFixed(1)} / 5.0
        </div>
      </div>

      {/* 버튼 영역 */}
      <div className="flex justify-end gap-3 pt-4">
        <button
          onClick={onEdit}
          className="px-4 py-2 rounded-xl border border-gray-300 text-sm text-gray-700 hover:bg-gray-100 transition"
        >
          수정
        </button>
        <button
          onClick={onDelete}
          className="px-4 py-2 rounded-xl border border-red-200 text-sm text-red-500 hover:bg-red-50 transition"
        >
          삭제
        </button>
      </div>
    </section>
  );
}
