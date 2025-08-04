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
    <section className="border rounded-xl p-6 shadow-sm bg-white">
      <div className="flex flex-col md:flex-row items-center gap-8">
        {/* 포스터 영역 */}
        <div className="w-full md:w-1/2">
          <div className="aspect-[16/9] bg-gray-200 rounded-lg shadow-sm flex items-center justify-center text-gray-400 text-lg overflow-hidden">
            {content.posterUrl ? (
              <img
                src={content.posterUrl}
                alt="포스터 이미지"
                width={1600}
                height={900}
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

          {/* 출시일 */}
          <div className="text-sm text-gray-500">
            <span className="font-medium text-gray-600">출시일: </span>
            {new Date(content.releasedAt).toLocaleDateString()}
          </div>

          {/* 장르 정보 */}
          <div className="space-y-2">
            <div className="text-sm font-medium text-gray-600">장르</div>
            <div className="flex flex-wrap gap-2">
              {genreNames.map((genre, index) => (
                <span
                  key={index}
                  className="bg-purple-100 text-purple-700 px-2 py-1 rounded-full text-sm"
                >
                  {genre}
                </span>
              ))}
            </div>
          </div>

          {/* OTT 정보 */}
          <div className="space-y-2">
            <div className="text-sm font-medium text-gray-600">OTT</div>
            <div className="flex flex-wrap gap-2">
              {ottNames.map((ott, index) => (
                <span
                  key={index}
                  className="bg-green-100 text-green-700 px-2 py-1 rounded-full text-sm"
                >
                  {ott}
                </span>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
