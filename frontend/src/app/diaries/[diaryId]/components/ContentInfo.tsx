import { Content } from "../../types/detail";

export default function ContentInfo({
  content,
  genreNames,
  ottNames,
}: {
  content: Content;
  genreNames: string[];
  ottNames: string[];
}) {
  return (
    <section className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
      <div className="flex flex-col md:flex-row gap-8">
        {/* 포스터 */}
        <div className="w-full md:w-1/3">
          <div className="aspect-[3/4] bg-gray-100 rounded-xl overflow-hidden shadow-sm">
            {content.posterUrl ? (
              <img
                src={content.posterUrl}
                alt={`${content.title} 포스터`}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-gray-400 text-sm">
                이미지 없음
              </div>
            )}
          </div>
        </div>

        {/* 텍스트 */}
        <div className="w-full md:w-2/3 space-y-4">
          <h2 className="text-2xl font-bold text-gray-900">{content.title}</h2>
          <p className="text-sm text-gray-600 leading-relaxed">{content.description}</p>

          <div className="text-sm text-gray-500">
            <span className="font-medium text-gray-600">출시일: </span>
            {new Date(content.releasedAt).toLocaleDateString()}
          </div>

          {/* 장르 */}
          {genreNames.length > 0 && (
            <div className="space-y-1">
              <div className="text-sm font-medium text-gray-600">장르</div>
              <div className="flex flex-wrap gap-2">
                {genreNames.map((genre, i) => (
                  <span
                    key={i}
                    className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-xs"
                  >
                    {genre}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* OTT */}
          {ottNames.length > 0 && (
            <div className="space-y-1">
              <div className="text-sm font-medium text-gray-600">시청 플랫폼</div>
              <div className="flex flex-wrap gap-2">
                {ottNames.map((ott, i) => (
                  <span
                    key={i}
                    className="bg-gray-100 text-gray-700 px-3 py-1 rounded-full text-xs"
                  >
                    {ott}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
