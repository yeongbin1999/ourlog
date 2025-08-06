"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import DiaryForm from "@/components/diary/DiaryForm";
import { Content, Diary } from "@/app/diaries/types/detail";

const EditDiaryPage = () => {
  const { diaryId } = useParams();
  const [diary, setDiary] = useState<Diary | null>(null);
  const [content, setContent] = useState<Content | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchDiaryAndContent = async () => {
      try {
        const [diaryRes, contentRes] = await Promise.all([
          fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/diaries/${diaryId}`).then(res => res.json()),
          fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/contents/${diaryId}`).then(res => res.json()),
        ]);
        
        setDiary(diaryRes.data);
        setContent(contentRes.data);
      } catch (error) {
        console.error("Fetch error:", error);
        alert("감상일기를 불러올 수 없습니다");
      } finally {
        setIsLoading(false);
      }
    };

    if (diaryId) {
      fetchDiaryAndContent();
    }
  }, [diaryId]);

  if (isLoading || !diary || !content) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-8 h-8 border-2 border-black border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">데이터를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <DiaryForm
      mode="edit"
      diaryId={Number(diaryId)}
      externalId={content.externalId}
      type={content.type as "MOVIE" | "BOOK" | "MUSIC"} 
      title={content.title}
      creatorName={content.creatorName}
      description={content.description}
      posterUrl={content.posterUrl}
      releasedAt={content.releasedAt}
      genres={diary.genreNames}
      initialValues={{
        title: diary.title,
        contentText: diary.contentText,
        isPublic: diary.isPublic,
        rating: diary.rating,
        tagNames: diary.tagNames,
        genreNames: diary.genreNames,
        ottNames: diary.ottNames, 
      }}
    />
  );
};

export default EditDiaryPage;