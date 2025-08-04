import Link from "next/link";

/* 페이지 타이틀 */
export default function DiaryTitle({ title }: { title: string }) {
  return (
    <>
      <Link
        href="/"
        className="text-blue-600 hover:underline text-lg font-bold"
      >
        ← Back to Feed
      </Link>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-bold text-gray-800 text-center flex-1">
          {title}
        </h1>
      </div>
    </>
  );
}
