// src/components/user/SentRequestList.tsx
'use client';

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import UserProfileCard from './UserProfileCard';
import { axiosInstance } from '@/lib/api-client';

type Props = {
  myUserId: number;
};

type SentUserResponse = {
  userId: number;
  nickname: string;
  email: string;
  bio: string;
  profileImageUrl: string;
};

// 내가 보낸 팔로우 요청..
export default function SentRequestList({ myUserId }: Props) {
  const [sentRequests, setSentRequests] = useState<SentUserResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchSentRequests = async () => {
    try {
      const res = await axiosInstance.get(`/api/v1/follows/sent-requests?userId=${myUserId}`);
      setSentRequests(res.data);
    } catch (err) {
      console.error('보낸 요청 불러오기 실패', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSentRequests();
  }, []);

  if (loading) return <div className="text-center mt-10">로딩 중...</div>;
  if (sentRequests.length === 0) return <div className="text-center mt-10">보낸 요청이 없습니다.</div>;

  return (
    <div className="space-y-6">
      {sentRequests.map((user) => (
        <UserProfileCard
          key={user.userId}
          userId={String(user.userId)}
          userType="sent"
        />
      ))}
    </div>
  );
}
