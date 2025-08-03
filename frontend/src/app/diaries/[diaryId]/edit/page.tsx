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
          fetch(`/api/v1/diaries/${diaryId}`).then(res => res.json()),
          fetch(`/api/v1/contents/${diaryId}`).then(res => res.json()),
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

    fetchDiaryAndContent();
  }, [diaryId]);

  if (isLoading || !diary || !content) return <div>Loading...</div>;

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
