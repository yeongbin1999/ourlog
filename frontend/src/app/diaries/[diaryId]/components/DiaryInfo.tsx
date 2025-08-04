import { FaStar, FaRegStar } from "react-icons/fa"; // 꽉 찬 별, 빈 별
import { DiaryInfoProps } from "../../types/detail";

export default function DiaryInfo({
  rating,
  contentText,
  tagNames,
}: DiaryInfoProps) {
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
        <div className="text-yellow-500 text-xl">{rating.toFixed(1)} / 5.0</div>
      </header>
      <p className="text-gray-800">{contentText}</p>
      {/* 감상 태그 */}
      <div className="flex gap-2 flex-wrap">
        {tagNames.map((tag, index) => (
          <span
            key={`tag-${index}`}
            className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm"
          >
            #{tag}
          </span>
        ))}
      </div>
    </section>
  );
}
