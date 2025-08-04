import Link from "next/link";

export default function DiaryTitle({ title }: { title: string }) {
  return (
    <div className="space-y-4">
      <Link
        href="/"
        className="text-xl text-gray-500 hover:underline inline-flex items-center gap-1"
      >
        ← 
      </Link>

      <h1 className="text-4xl font-extrabold text-gray-900 text-center tracking-tight">
        {title}
      </h1>
    </div>
  );
}
