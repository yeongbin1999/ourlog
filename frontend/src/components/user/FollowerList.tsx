'use client';

import React, { useEffect, useState } from 'react';
import { axiosInstance } from '@/lib/api-client';
import UserProfileCard from './UserProfileCard';

type Props = {
  myUserId: number;
};

type FollowerUserResponse = {
  userId: number;
  nickname: string;
  email: string;
  bio: string;
  profileImageUrl: string;
  isFollowing: boolean;
  followId: number;
};

export default function FollowerList({ myUserId }: Props) {
  const [followers, setFollowers] = useState<FollowerUserResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchFollowers = async () => {
    try {
      // ← 이 부분만 'followers'로 수정!
      const res = await axiosInstance.get(`/api/v1/follows/followers?userId=${myUserId}`);
      const data = Array.isArray(res.data) ? res.data : res.data.data ?? [];
      setFollowers(data);
    } catch (err) {
      console.error('팔로워 목록 불러오기 실패', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (myUserId) {
      fetchFollowers();
    }
  }, [myUserId]);

  if (loading) return <div className="text-center mt-10">로딩 중...</div>;
  if (followers.length === 0) return <div className="text-center mt-10">아직 팔로워가 없습니다.</div>;

  return (
    <div className="space-y-6">
      {followers.map((user) => (
        <UserProfileCard
          key={user.userId}
          userId={String(user.userId)}
          userType="followers"
          followId={user.followId}
          isFollowing={user.isFollowing}
        />
      ))}
    </div>
  );
}