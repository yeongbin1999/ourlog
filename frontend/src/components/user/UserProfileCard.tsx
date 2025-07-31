'use client';

import React, { useEffect, useState } from 'react';
import axios from 'axios';

type Props = {
  userId: string;
};

type UserProfile = {
  email: string;
  nickname: string;
  profileImageUrl: string;
  bio: string;
};

export default function UserProfileCard({ userId }: Props) {
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [error, setError] = useState<string | null>(null); // 🔥 에러 상태 추가

  useEffect(() => {
    axios.get(`http://localhost:8080/api/v1/users/${userId}`)
      .then((res) => {
        setProfile(res.data);
        setError(null); // 성공 시 에러 초기화
      })
      .catch((err) => {
        console.error('존재하지 않는 사용자입니다.', err);
        setError('존재하지 않는 사용자입니다.'); // 👈 사용자 친화적인 에러 메시지
      });
  }, [userId]);

  if (error) {
    return (
      <div className="text-center text-black text-lg mt-10">
        {error}
      </div>
    );
  }

  if (!profile) return <div className="text-center">⏳ 로딩 중...</div>;

  return (
    <div className="w-full max-w-sm bg-white p-6 rounded-3xl shadow-md border border-black mx-auto flex flex-col items-center text-center">
      <div
        className="w-20 h-20 mb-4 rounded-full bg-center bg-cover"
        style={{ backgroundImage: `url(${profile.profileImageUrl})` }}
      />
      <h2 className="text-2xl font-bold mb-1">{profile.nickname}</h2>
      <p className="text-sm text-gray-600 mb-2">{profile.bio}</p>

      <hr className="my-4 w-full" />

      <ul className="space-y-2 text-sm text-gray-600 w-full text-left pl-4 ml-28">
        <li>Email: {profile.email}</li>
        <li>Feature 2: Coming soon</li>
        <li>Feature 3: Coming soon</li>
      </ul>

      <button className="mt-6 px-4 py-2 border border-black rounded-md hover:bg-black hover:text-white transition">
        Start Now
      </button>
    </div>
  );
}
